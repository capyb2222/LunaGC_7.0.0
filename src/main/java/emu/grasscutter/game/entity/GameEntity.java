package emu.grasscutter.game.entity;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.*;
import emu.grasscutter.game.ability.*;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.*;
import emu.grasscutter.game.world.*;
import emu.grasscutter.net.proto.ChangeHpDebtsReasonOuterClass.ChangeHpDebtsReason;
import emu.grasscutter.net.proto.ChangeHpReasonOuterClass.ChangeHpReason;
import emu.grasscutter.net.proto.FightPropPairOuterClass.FightPropPair;
import emu.grasscutter.net.proto.AbilityStringOuterClass.AbilityString;
import emu.grasscutter.net.proto.GadgetInteractReqOuterClass.GadgetInteractReq;
import emu.grasscutter.net.proto.MotionInfoOuterClass.MotionInfo;
import emu.grasscutter.net.proto.MotionStateOuterClass.MotionState;
import emu.grasscutter.net.proto.PropChangeReasonOuterClass.PropChangeReason;
import emu.grasscutter.net.proto.SceneEntityInfoOuterClass.SceneEntityInfo;
import emu.grasscutter.net.proto.VectorOuterClass.Vector;
import emu.grasscutter.scripts.data.controller.EntityController;
import emu.grasscutter.net.proto.DetailAbilityInfoOuterClass.DetailAbilityInfo;
import emu.grasscutter.net.proto.PropChangeDetailInfoOuterClass.PropChangeDetailInfo;
import emu.grasscutter.server.event.entity.*;
import emu.grasscutter.server.packet.send.PacketEntityFightPropChangeReasonNotify;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;
import it.unimi.dsi.fastutil.ints.*;
import emu.grasscutter.server.packet.send.PacketAvatarFightPropNotify;
import emu.grasscutter.*;
import emu.grasscutter.data.GameData;

import java.util.*;


import lombok.*;

import static emu.grasscutter.GameConstants.ENTITY_ID_BIT_SHIFT;

public abstract class GameEntity {
    @Getter private final Scene scene;
    private boolean restrictedFromHealing = false;
    private boolean convertToHpDebt = false;
    @Getter @Setter public int id;
    @Getter @Setter private SpawnDataEntry spawnEntry;
    @Setter private PropChangeDetailInfo propChangeDetailInfo;
    @Getter @Setter private DetailAbilityInfo detailAbilityInfo;

    @Getter @Setter private int campId;
    @Getter @Setter private int campType;

    @Getter @Setter private int blockId;
    @Getter @Setter private int configId;
    @Getter @Setter private int groupId;

    @Getter @Setter private MotionState motionState;
    @Getter @Setter private int lastMoveSceneTimeMs;

    @Getter @Setter private int lastMoveReliableSeq;

    @Getter @Setter private boolean lockHP;
    private boolean limbo;
    private float limboHpThreshold;

    @Setter(AccessLevel.PROTECTED)
    @Getter
    private boolean isDead = false;

    // Lua controller for specific actions
    @Getter @Setter private EntityController entityController;
    @Getter private ElementType lastAttackType = ElementType.None;

    @Getter private List<Ability> instancedAbilities = new ArrayList<>();

    @Getter
    private Int2ObjectMap<AbilityModifierController> instancedModifiers =
            new Int2ObjectOpenHashMap<>();

    @Getter private Map<String, Float> globalAbilityValues = new HashMap<>();

    public GameEntity(Scene scene) {
        this.scene = scene;
        this.motionState = MotionState.MotionState_MOTION_NONE;
    }

    public abstract void initAbilities();

    public EntityType getEntityType() {
        return EntityIdType.toEntityType(this.getId() >> ENTITY_ID_BIT_SHIFT);
    }
    public boolean isConvertToHpDebt() {
        return convertToHpDebt;
    }

    public float getNyxValue() {
        if (this.getGlobalAbilityValues().containsKey("NyxValue")) {
            return this.getGlobalAbilityValues().get("NyxValue");
        } else {
            Grasscutter.getLogger().info("NyxValue not found");
            return 0f;
        }
    }


    public void setConvertToHpDebt(boolean convertToHpDebt) {
        this.convertToHpDebt = convertToHpDebt;
    }

    public abstract int getEntityTypeId();

    public World getWorld() {
        return this.getScene().getWorld();
    }
        public boolean isRestrictedFromHealing() {
            return restrictedFromHealing;
        }

        public void setRestrictedFromHealing(boolean restricted) {
            this.restrictedFromHealing = restricted;
        }

    public boolean isAlive() {
        return !this.isDead;
    }
    public LifeState getLifeState() {
        return this.isAlive() ? LifeState.LIFE_ALIVE : LifeState.LIFE_DEAD;
    }

    public abstract Int2FloatMap getFightProperties();

    public abstract Position getPosition();

    public abstract Position getRotation();

    public void setFightProperty(FightProperty prop, float value) {
        this.getFightProperties().put(prop.getId(), value);
    }

    public void setFightProperty(int id, float value) {
        this.getFightProperties().put(id, value);
    }

    public void addFightProperty(FightProperty prop, float value) {
        this.getFightProperties().put(prop.getId(), this.getFightProperty(prop) + value);
    }

    public float getFightProperty(FightProperty prop) {
        return this.getFightProperties().getOrDefault(prop.getId(), 0f);
    }

    public boolean hasFightProperty(FightProperty prop) {
        return this.getFightProperties().containsKey(prop.getId());
    }

    public void addAllFightPropsToEntityInfo(SceneEntityInfo.Builder entityInfo) {
        this.getFightProperties()
                .forEach(
                        (key, value) -> {
                            if (key == 0) return;
                            entityInfo.addFightPropList(
                                    FightPropPair.newBuilder().setPropType(key).setPropValue(value).build());
                        });
    }

    protected void setLimbo(float hpThreshold) {
        limbo = true;
        limboHpThreshold = hpThreshold;
    }
    public GameEntity getTrueOwner() {
    if (this instanceof EntityClientGadget gadget) {
        GameEntity owner = gadget.getScene().getEntityById(gadget.getOwnerEntityId());
        // Recursively resolve the owner if the owner is also a gadget
        return (owner instanceof EntityClientGadget) ? owner.getTrueOwner() : owner;
    }
    return this; // If not a gadget, return itself as the owner
}

    public void onAddAbilityModifier(AbilityModifier data) {
        if (data.properties == null) {
            return;
        }
        float hpThresholdRatio = data.properties.Actor_HpThresholdRatio;
        // Set limbo state (invulnerability at a certain HP threshold)
        if (data.properties != null) {
            if (data.state == AbilityModifier.State.Limbo && hpThresholdRatio > 0.0f) {
                Grasscutter.getLogger().info("Limbo set to " + hpThresholdRatio);
                this.setLimbo(hpThresholdRatio);
            }
        }
    }


    protected MotionInfo getMotionInfo() {
        return MotionInfo.newBuilder()
                .setPos(this.getPosition().toProto())
                .setRot(this.getRotation().toProto())
                .setSpeed(Vector.newBuilder())
                .setState(this.getMotionState())
                .build();
    }
    public float heal(float amount) {
        return heal(amount, false);
    }


    public float heal(float amount, boolean mute) {
        if (this.getFightProperties() == null) {
            return 0f;
        }

        float toHeal = 0f;
        float toRepay = 0f;
        float curHp = this.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
        float maxHp = this.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
        float curHpDebt = this.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP_DEBTS);

        if (curHp >= maxHp && curHpDebt <= 0) {
            return 0f;
        }

        toRepay = Math.min(amount, curHpDebt);
        toHeal = Math.min(maxHp - curHp, amount - toRepay);
        this.addFightProperty(FightProperty.FIGHT_PROP_CUR_HP, toHeal);
        this.addFightProperty(FightProperty.FIGHT_PROP_CUR_HP_DEBTS, -toRepay);

        if (toHeal > 0) {
            this.getScene().broadcastPacket(new PacketEntityFightPropUpdateNotify(this, FightProperty.FIGHT_PROP_CUR_HP));
        }
        if (toRepay > 0) {
            this.getScene().broadcastPacket(new PacketEntityFightPropUpdateNotify(this, FightProperty.FIGHT_PROP_CUR_HP_DEBTS));
            // Clear bond if is 0 debt left
            if (this.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP_DEBTS) > 0) {
                this.getScene().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(this, FightProperty.FIGHT_PROP_CUR_HP_DEBTS, toRepay,
                                                        mute
                                                                ? PropChangeReason.PropChangeReason_PROP_CHANGE_NONE
                                                                : PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY,

                                                        ChangeHpDebtsReason.CHANGE_HP_DEBTS_REASON_CHANGE_HP_DEBTS_PAY
                ));
            } else {
                this.getScene().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(this, FightProperty.FIGHT_PROP_CUR_HP_DEBTS, toRepay,
                                                        mute
                                                                ? PropChangeReason.PropChangeReason_PROP_CHANGE_NONE
                                                                : PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY,

                                                        ChangeHpDebtsReason.CHANGE_HP_DEBTS_REASON_CHANGE_HP_DEBTS_PAY_FINISH
                                                       ));
            }
        }

        return toHeal;
    }

    public void damage(float amount) {
        GameEntity ownerEntity = resolveOwnerEntity(this);
        this.damage(amount, 0, ElementType.None);
    }
    private GameEntity resolveOwnerEntity(GameEntity owner) {
        if (owner instanceof EntityClientGadget ownerGadget) {
            // Recursively find the owner entity
            GameEntity nextOwner = ownerGadget.getScene().getEntityById(ownerGadget.getOwnerEntityId());
            return resolveOwnerEntity(nextOwner); // Keep resolving until you reach the actual entity
        }
        return owner; // Return the entity if it is not a gadget
    }
      public void addSpecialEnergy(float energy){
       float curSpecialEnergy = getFightProperty(FightProperty.FIGHT_PROP_CUR_SPECIAL_ENERGY);
       float maxSpecialEnergy = getFightProperty(FightProperty.FIGHT_PROP_MAX_SPECIAL_ENERGY);
       curSpecialEnergy+=energy;
       if (curSpecialEnergy >= maxSpecialEnergy){
            curSpecialEnergy = maxSpecialEnergy;
       }
       setFightProperty(FightProperty.FIGHT_PROP_CUR_SPECIAL_ENERGY, curSpecialEnergy);
       this.getScene().broadcastPacket(new PacketEntityFightPropUpdateNotify(this, FightProperty.FIGHT_PROP_CUR_SPECIAL_ENERGY));
    }

    public void clearSpecialEnergy(){
        setFightProperty(FightProperty.FIGHT_PROP_CUR_SPECIAL_ENERGY, 0);
        this.getScene().broadcastPacket(new PacketEntityFightPropUpdateNotify(this, FightProperty.FIGHT_PROP_CUR_SPECIAL_ENERGY));
    }

    public void damage(float amount, ElementType attackType) {
        this.damage(amount, 0, attackType);
    }

    public void damage(float amount, int killerId, ElementType attackType) {
        this.damage(amount, 0, attackType, PropChangeReason.PropChangeReason_PROP_CHANGE_NONE, ChangeHpReason.ChangeHpReason_CHANGE_HP_NONE);
    }

    public void damage(float amount, PropChangeReason propChangeReason, ChangeHpReason changeHpReason) {
        this.damage(amount, 0, ElementType.None, propChangeReason, changeHpReason);
    }

    public void damage(float amount, int killerId, ElementType attackType, PropChangeReason propChangeReason, ChangeHpReason changeHpReason) {
        // Check if the entity has properties.
        if (this.getFightProperties() == null || !hasFightProperty(FightProperty.FIGHT_PROP_CUR_HP)) {
            return;
        }

        // Invoke entity damage event.
        EntityDamageEvent event =
                new EntityDamageEvent(this, amount, attackType, this.getScene().getEntityById(killerId));
        event.call();
        if (event.isCanceled()) {
            return; // If the event is canceled, do not damage the entity.
        }

        float effectiveDamage = 0;
        float curHp = getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
        if (limbo) {
            float maxHp = getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
            float curRatio = curHp / maxHp;
            if (curRatio > limboHpThreshold) {
                // OK if this hit takes HP below threshold.
                effectiveDamage = event.getDamage();
            }
            if (effectiveDamage >= curHp && limboHpThreshold > .0f) {
                // Don't let entity die while in limbo.
                effectiveDamage = curHp - 1;
            }
        } else if (curHp != Float.POSITIVE_INFINITY && !lockHP
                || lockHP && curHp <= event.getDamage()) {
            effectiveDamage = event.getDamage();
        }

        // Add negative HP to the current HP property.
        this.addFightProperty(FightProperty.FIGHT_PROP_CUR_HP, -effectiveDamage);

        this.lastAttackType = attackType;
        this.checkIfDead();
        this.runLuaCallbacks(event);

        // Packets
        this.getScene()
                .broadcastPacket(
                        new PacketEntityFightPropUpdateNotify(this, FightProperty.FIGHT_PROP_CUR_HP));

        // Check if dead.
        if (this.isDead) {
            this.getScene().killEntity(this, killerId);
        }
    }

    public void checkIfDead() {
        if (this.getFightProperties() == null || !hasFightProperty(FightProperty.FIGHT_PROP_CUR_HP)) {
            return;
        }

        if (this.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP) <= 0f) {
            this.setFightProperty(FightProperty.FIGHT_PROP_CUR_HP, 0f);
            float debt = this.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP_DEBTS);
            if (debt >= 0) {
                this.setFightProperty(FightProperty.FIGHT_PROP_CUR_HP_DEBTS, 0f);
                this.getScene().broadcastPacket(new PacketEntityFightPropUpdateNotify(this, FightProperty.FIGHT_PROP_CUR_HP_DEBTS));
                this.getScene().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(this, FightProperty.FIGHT_PROP_CUR_HP_DEBTS, -debt, PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY, ChangeHpDebtsReason.CHANGE_HP_DEBTS_REASON_CHANGE_HP_DEBTS_CLEAR));
            }
            this.isDead = true;
        }
    }

    /**
     * Runs the Lua callbacks for {@link EntityDamageEvent}.
     *
     * @param event The damage event.
     */
    public void runLuaCallbacks(EntityDamageEvent event) {
        if (entityController != null) {
            entityController.onBeHurt(this, event.getAttackElementType(), true); // todo is host handling
        }
    }

    /**
     * Move this entity to a new position.
     *
     * @param position The new position.
     * @param rotation The new rotation.
     */
    public void move(Position position, Position rotation) {
        // Set the position and rotation.
        this.getPosition().set(position);
        this.getRotation().set(rotation);
    }

    /**
     * Called when a player interacts with this entity
     *
     * @param player Player that is interacting with this entity
     * @param interactReq Interact request protobuf data
     */
    public void onInteract(Player player, GadgetInteractReq interactReq) {}

    /** Called when this entity is added to the world */
    public void onCreate() {}

    public void onRemoved() {}

    private int[] parseCountRange(String range) {
        var split = range.split(";");
        if (split.length == 1)
            return new int[] {Integer.parseInt(split[0]), Integer.parseInt(split[0])};
        return new int[] {Integer.parseInt(split[0]), Integer.parseInt(split[1])};
    }

    public boolean dropSubfieldItem(int dropId) {
        var drop = GameData.getDropSubfieldMappingMap().get(dropId);
        if (drop == null) return false;
        var dropTableEntry = GameData.getDropTableExcelConfigDataMap().get(drop.getItemId());
        if (dropTableEntry == null) return false;

        Int2ObjectMap<Integer> itemsToDrop = new Int2ObjectOpenHashMap<>();
        switch (dropTableEntry.getRandomType()) {
            case 0: // select one
                {
                    int weightCount = 0;
                    for (var entry : dropTableEntry.getDropVec()) weightCount += entry.getWeight();

                    int randomValue = new Random().nextInt(weightCount);

                    weightCount = 0;
                    for (var entry : dropTableEntry.getDropVec()) {
                        if (randomValue >= weightCount && randomValue < (weightCount + entry.getWeight())) {
                            var countRange = parseCountRange(entry.getCountRange());
                            itemsToDrop.put(
                                    entry.getItemId(),
                                    Integer.valueOf((new Random().nextBoolean() ? countRange[0] : countRange[1])));
                        }
                    }
                }
                break;
            case 1: // Select various
                {
                    for (var entry : dropTableEntry.getDropVec()) {
                        if (entry.getWeight() < new Random().nextInt(10000)) {
                            var countRange = parseCountRange(entry.getCountRange());
                            itemsToDrop.put(
                                    entry.getItemId(),
                                    Integer.valueOf((new Random().nextBoolean() ? countRange[0] : countRange[1])));
                        }
                    }
                }
                break;
        }

        for (var entry : itemsToDrop.int2ObjectEntrySet()) {
            var item =
                    new EntityItem(
                            scene,
                            null,
                            GameData.getItemDataMap().get(entry.getIntKey()),
                            getPosition().nearby2d(1f).addY(0.5f),
                            entry.getValue(),
                            true);

            scene.addEntity(item);
        }

        return true;
    }

    public boolean dropSubfield(String subfieldName) {
        var subfieldMapping = GameData.getSubfieldMappingMap().get(getEntityTypeId());
        if (subfieldMapping == null || subfieldMapping.getSubfields() == null) return false;

        for (var entry : subfieldMapping.getSubfields()) {
            if (entry.getSubfieldName().compareTo(subfieldName) == 0) {
                return dropSubfieldItem(entry.getDrop_id());
            }
        }

        return false;
    }

    public void onTick(int sceneTime) {
        if (entityController != null) {
            entityController.onTimer(this, sceneTime);
        }
    }

    public int onClientExecuteRequest(int param1, int param2, int param3) {
        if (entityController != null) {
            return entityController.onClientExecuteRequest(this, param1, param2, param3);
        }
        return 0;
    }

    /**
     * Called when this entity dies
     *
     * @param killerId Entity id of the entity that killed this entity
     */
    public void onDeath(int killerId) {
        // Invoke entity death event.
        EntityDeathEvent event = new EntityDeathEvent(this, killerId);
        event.call();

        // Run Lua callbacks.
        if (entityController != null) {
            entityController.onDie(this, getLastAttackType());
        }

        this.isDead = true;
    }

    /** Invoked when a global ability value is updated. */
    public void onAbilityValueUpdate() {
        // Does nothing.
    }

    public abstract SceneEntityInfo toProto();

    @Override
    public String toString() {
        return "Entity ID: %s; Group ID: %s; Config ID: %s"
                .formatted(this.getId(), this.getGroupId(), this.getConfigId());
    }
}
