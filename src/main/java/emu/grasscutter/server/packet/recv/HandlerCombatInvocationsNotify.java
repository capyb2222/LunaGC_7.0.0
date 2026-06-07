package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.entity.EntityAvatar;
import emu.grasscutter.game.entity.EntityClientGadget;
import emu.grasscutter.game.entity.EntityMonster;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.game.world.Position;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.ForwardTypeOuterClass.ForwardType;
import emu.grasscutter.net.proto.AttackResultOuterClass.AttackResult;
import emu.grasscutter.net.proto.CombatInvocationsNotifyOuterClass.CombatInvocationsNotify;
import emu.grasscutter.net.proto.CombatInvokeEntryOuterClass.CombatInvokeEntry;
import emu.grasscutter.net.proto.EntityMoveInfoOuterClass.EntityMoveInfo;
import emu.grasscutter.net.proto.EvtAnimatorParameterInfoOuterClass.EvtAnimatorParameterInfo;
import emu.grasscutter.net.proto.EvtBeingHitInfoOuterClass.EvtBeingHitInfo;
import emu.grasscutter.net.proto.MotionInfoOuterClass.MotionInfo;
import emu.grasscutter.net.proto.MotionStateOuterClass.MotionState;
import emu.grasscutter.net.proto.PlayerDieTypeOuterClass;
import emu.grasscutter.server.event.entity.EntityMoveEvent;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;

@Opcodes(PacketOpcodes.CombatInvocationsNotify)
public class HandlerCombatInvocationsNotify extends PacketHandler {

    private float cachedLandingSpeed = 0;
    private long cachedLandingTimeMillisecond = 0;
    private boolean monitorLandingEvent = false;

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        CombatInvocationsNotify notif = CombatInvocationsNotify.parseFrom(payload);
        for (CombatInvokeEntry entry : notif.getInvokeListList()) {

            switch (entry.getArgumentType()) {
                case CombatTypeArgument_COMBAT_EVT_BEING_HIT -> {
                    EvtBeingHitInfo hitInfo = EvtBeingHitInfo.parseFrom(entry.getCombatData());
                    AttackResult attackResult = hitInfo.getAttackResult();
                    Player player = session.getPlayer();

                    if (attackResult.getAttackerId()
                                    != player.getTeamManager().getCurrentAvatarEntity().getId()
                            && player.getAbilityManager().isAbilityInvulnerable()) break;

                    {
                        int defenseId = attackResult.getDefenseId();
                        float computedDamage = attackResult.getDamage();
                        boolean changed = false;

                        float hexRatio = 0f, normalPct = 0f;
                        EntityClientGadget attackerGadget = null;
                        if (computedDamage == 0.0f) {
                            var attackerEntity = player.getScene().getEntityById(attackResult.getAttackerId());
                            if (attackerEntity instanceof EntityClientGadget gadget) {
                                for (var ab : gadget.getInstancedAbilities()) {
                                    if (ab == null) continue;
                                    var sp = ab.getAbilitySpecials();
                                    if (hexRatio == 0f) hexRatio = sp.getFloat("Hexenzirkel_NormalAttack_Ratio");
                                    if (normalPct == 0f) {
                                        for (String k : sp.keySet()) {
                                            if (k.startsWith("NormalAttack_") && k.endsWith("_Damage_Percentage")) {
                                                normalPct = sp.getFloat(k);
                                                break;
                                            }
                                        }
                                    }
                                    if (hexRatio > 0f && normalPct > 0f) break;
                                }
                                if (hexRatio == 0f || normalPct == 0f) {
                                    var vars = player.getAbilityManager().computeGadgetVarOverrides(gadget);
                                    if (hexRatio == 0f) hexRatio = vars.getOrDefault("Hexenzirkel_NormalAttack_Ratio", 0f);
                                    if (normalPct == 0f) {
                                        for (var e : vars.entrySet()) {
                                            if (e.getKey().startsWith("NormalAttack_") && e.getKey().endsWith("_Damage_Percentage")) {
                                                normalPct = e.getValue();
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (hexRatio > 0f && normalPct > 0f) {
                                    attackerGadget = gadget;
                                }
                            }
                        }

                        GameEntity nearestMonster = null;
                        if ((defenseId == 0 || attackerGadget != null)
                                && (computedDamage > 0f || attackerGadget != null)) {
                            var avatarPos = player.getTeamManager().getCurrentAvatarEntity().getPosition();
                            float nearestDist2 = Float.MAX_VALUE;
                            for (var e : player.getScene().getEntities().values()) {
                                if (!(e instanceof EntityMonster)) continue;
                                if (e.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP) <= 0f) continue;
                                var p = e.getPosition();
                                float dx = avatarPos.getX() - p.getX();
                                float dz = avatarPos.getZ() - p.getZ();
                                float dist2 = dx * dx + dz * dz;
                                if (dist2 < nearestDist2) {
                                    nearestDist2 = dist2;
                                    nearestMonster = e;
                                }
                            }
                            if (nearestMonster != null) {
                                defenseId = nearestMonster.getId();
                                changed = true;
                            }
                        }

                        if (attackerGadget != null) {
                            var avatarEntity = player.getTeamManager().getCurrentAvatarEntity();
                            float atk = avatarEntity.getFightProperty(FightProperty.FIGHT_PROP_CUR_ATTACK);
                            float anemoDmgBonus = avatarEntity.getFightProperty(FightProperty.FIGHT_PROP_WIND_ADD_HURT);
                            float critRate = avatarEntity.getFightProperty(FightProperty.FIGHT_PROP_CRITICAL);
                            float critDmg = avatarEntity.getFightProperty(FightProperty.FIGHT_PROP_CRITICAL_HURT);

                            computedDamage = atk * hexRatio * normalPct * (1f + anemoDmgBonus);

                            if (Math.random() < critRate) {
                                computedDamage *= (1f + critDmg);
                            }

                            changed = true;
                        }

                        if (changed) {
                            AttackResult newResult = attackResult.toBuilder()
                                .setDefenseId(defenseId)
                                .setDamage(computedDamage)
                                .build();
                            hitInfo = hitInfo.toBuilder().setAttackResult(newResult).build();
                            entry = entry.toBuilder()
                                .setCombatData(hitInfo.toByteString())
                                .setForwardType(ForwardType.ForwardType_FORWARD_TO_ALL)
                                .build();
                            attackResult = newResult;
                        }
                    }

                    player.getAttackResults().add(attackResult);
                    player.getEnergyManager().handleAttackHit(hitInfo);
                }
                case CombatTypeArgument_ENTITY_MOVE -> {

                    EntityMoveInfo moveInfo = EntityMoveInfo.parseFrom(entry.getCombatData());
                    GameEntity entity = session.getPlayer().getScene().getEntityById(moveInfo.getEntityId());
                    if (entity != null
                            && session.getPlayer().getSceneLoadState() != Player.SceneLoadState.LOADING) {

                        MotionInfo motionInfo = moveInfo.getMotionInfo();
                        MotionState motionState = motionInfo.getState();

                        EntityMoveEvent event =
                                new EntityMoveEvent(
                                        entity,
                                        new Position(motionInfo.getPos()),
                                        new Position(motionInfo.getRot()),
                                        motionState);
                        event.call();

                        entity.move(event.getPosition(), event.getRotation());
                        entity.setLastMoveSceneTimeMs(moveInfo.getSceneTime());
                        entity.setLastMoveReliableSeq(moveInfo.getReliableSeq());
                        entity.setMotionState(motionState);

                        session
                                .getPlayer()
                                .getStaminaManager()
                                .handleCombatInvocationsNotify(session, moveInfo, entity);

                        if (motionState == MotionState.MotionState_MOTION_LAND_SPEED) {
                            cachedLandingSpeed = motionInfo.getSpeed().getY();
                            cachedLandingTimeMillisecond = System.currentTimeMillis();
                            monitorLandingEvent = true;
                        }
                        if (monitorLandingEvent) {
                            if (motionState == MotionState.MotionState_MOTION_FALL_ON_GROUND) {
                                monitorLandingEvent = false;
                                handleFallOnGround(session, entity, motionState);
                            }
                        }

                        if (motionState == MotionState.MotionState_MOTION_NOTIFY
                                || motionState == MotionState.MotionState_MOTION_FIGHT) {
                            continue;
                        }
                    }
                }
                case CombatTypeArgument_COMBAT_ANIMATOR_PARAMETER_CHANGED -> {
                    EvtAnimatorParameterInfo paramInfo =
                            EvtAnimatorParameterInfo.parseFrom(entry.getCombatData());
                    if (paramInfo.getIsServerCache()) {
                        paramInfo = paramInfo.toBuilder().setIsServerCache(false).build();
                        entry = entry.toBuilder().setCombatData(paramInfo.toByteString()).build();
                    }
                }
                default -> {
                    Grasscutter.getLogger().debug("UnhandledCombatType: type={} typeVal={} dataSize={}",
                        entry.getArgumentType(), entry.getArgumentTypeValue(), entry.getCombatData().size());
                }
            }

            session.getPlayer().getCombatInvokeHandler().addEntry(entry.getForwardType(), entry);
        }
    }

    private void handleFallOnGround(GameSession session, GameEntity entity, MotionState motionState) {
        if (session.getPlayer().isInGodMode()) {
            return;
        }

        int maxDelay = 200;
        long actualDelay = System.currentTimeMillis() - cachedLandingTimeMillisecond;
        Grasscutter.getLogger()
                .trace(
                        "MOTION_FALL_ON_GROUND received after "
                                + actualDelay
                                + "/"
                                + maxDelay
                                + "ms."
                                + (actualDelay > maxDelay ? " Discard" : ""));
        if (actualDelay > maxDelay) {
            return;
        }
        float currentHP = entity.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
        float maxHP = entity.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
        float damageFactor = 0;
        if (cachedLandingSpeed < -23.5) {
            damageFactor = 0.33f;
        }
        if (cachedLandingSpeed < -25) {
            damageFactor = 0.5f;
        }
        if (cachedLandingSpeed < -26.5) {
            damageFactor = 0.66f;
        }
        if (cachedLandingSpeed < -28) {
            damageFactor = 1f;
        }
        float damage = maxHP * damageFactor;
        float newHP = currentHP - damage;
        if (newHP < 0) {
            newHP = 0;
        }
        if (damageFactor > 0) {
            Grasscutter.getLogger()
                    .debug(
                            currentHP
                                    + "/"
                                    + maxHP
                                    + "\tLandingSpeed: "
                                    + cachedLandingSpeed
                                    + "\tDamageFactor: "
                                    + damageFactor
                                    + "\tDamage: "
                                    + damage
                                    + "\tNewHP: "
                                    + newHP);
        } else {
            Grasscutter.getLogger().trace(currentHP + "/" + maxHP + "\tLandingSpeed: 0\tNo damage");
        }
        entity.setFightProperty(FightProperty.FIGHT_PROP_CUR_HP, newHP);
        entity
                .getWorld()
                .broadcastPacket(
                        new PacketEntityFightPropUpdateNotify(entity, FightProperty.FIGHT_PROP_CUR_HP));
        if (newHP == 0) {
            session
                    .getPlayer()
                    .getStaminaManager()
                    .killAvatar(session, entity, PlayerDieTypeOuterClass.PlayerDieType.PlayerDieType_PLAYER_DIE_FALL);
        }
        cachedLandingSpeed = 0;
    }
}
