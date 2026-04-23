package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.data.common.DynamicFloat;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.*;
import emu.grasscutter.net.proto.ChangeHpDebtsReasonOuterClass;
import emu.grasscutter.net.proto.PropChangeReasonOuterClass.PropChangeReason;
import emu.grasscutter.net.proto.PropChangeReasonOuterClass;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;
import emu.grasscutter.server.packet.send.PacketEntityFightPropChangeReasonNotify;
import emu.grasscutter.net.proto.ChangeEnergyReasonOuterClass.ChangeEnergyReason;

import emu.grasscutter.game.props.FightProperty;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;
import emu.grasscutter.server.packet.send.PacketEntityFightPropChangeReasonNotify;
import emu.grasscutter.server.packet.send.PacketServerGlobalValueChangeNotify;

@AbilityAction(AbilityModifierAction.Type.HealHP)
public final class ActionHealHP extends AbilityActionHandler {
    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        var owner = ability.getOwner();

        if (owner != null) {
                Grasscutter.getLogger().debug("Owner: {}", owner);
                Grasscutter.getLogger().debug("Target: {}", target);
        }

        // handle client gadgets, that the effective caster is the current local avatar
        if (owner instanceof EntityClientGadget ownerGadget) {
            owner =
                    ownerGadget
                            .getScene()
                            .getEntityById(ownerGadget.getOwnerEntityId()); // Caster for EntityClientGadget
            if (DebugConstants.LOG_ABILITIES) {
                Grasscutter.getLogger()
                        .debug(
                                "Owner {} has top owner {}: {}",
                                ability.getOwner(),
                                ownerGadget.getOwnerEntityId(),
                                owner);
            }
        }
        if (owner instanceof EntityClientGadget ownerGadget) {
                owner = ownerGadget.getScene().getEntityById(ownerGadget.getOwnerEntityId());

                if (ownerGadget.gadgetId == 41089013 || ownerGadget.gadgetId == 41089012 || ownerGadget.gadgetId == 41089011) {
                    if (owner == null) {
                        owner = ability.getPlayerOwner().getTeamManager().getCurrentAvatarEntity();
                    }
                }
            }

        if (owner == null) return false;

        // Get all properties.
        var properties = new Object2FloatOpenHashMap<String>();
        // Add entity fight properties.
        for (var property : FightProperty.values()) {
            var name = property.name();
            var value = owner.getFightProperty(property);
            properties.put(name, value);
        }
        // Add ability properties.
        properties.putAll(ability.getAbilitySpecials());

        // Calculate ratios from properties.
        var amountByCasterMaxHPRatio = action.amountByCasterMaxHPRatio.get(properties, 0);
        var amountByCasterAttackRatio = action.amountByCasterAttackRatio.get(properties, 0);
        var amountByCasterCurrentHPRatio = action.amountByCasterCurrentHPRatio.get(properties, 0);
        var amountByTargetCurrentHPRatio = action.amountByTargetCurrentHPRatio.get(properties, 0);
        var amountByTargetMaxHPRatio = action.amountByTargetMaxHPRatio.get(properties, 0);
        var amountToRegenerate = action.amount.get(properties, 0);

        if (action.amount.get(ability) != 0 &&
            (amountByCasterMaxHPRatio != 0 ||
            amountByCasterAttackRatio != 0 ||
            amountByCasterCurrentHPRatio != 0 ||
            amountByTargetCurrentHPRatio != 0 ||
            amountByTargetMaxHPRatio != 0)) {
            amountToRegenerate += action.amount.get(ability);  // Add the amount to the regeneration if both are present
        }

        amountToRegenerate +=
                amountByCasterMaxHPRatio * owner.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
        amountToRegenerate +=
                amountByCasterAttackRatio * owner.getFightProperty(FightProperty.FIGHT_PROP_CUR_ATTACK);
        amountToRegenerate +=
                amountByCasterCurrentHPRatio * owner.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);

        var abilityRatio = 1.0f;
        if (!action.ignoreAbilityProperty)
            abilityRatio +=
                    target.getFightProperty(FightProperty.FIGHT_PROP_HEAL_ADD)
                            + target.getFightProperty(FightProperty.FIGHT_PROP_HEALED_ADD);

        amountToRegenerate +=
                amountByTargetCurrentHPRatio * target.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
        amountToRegenerate +=
                amountByTargetMaxHPRatio * target.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);


        String healTag = action.healTag;
        float healToHpDebtsRatio = 0.0f;
        if ("Clorinde_ElementalArt_Heal".equals(healTag)) {
            target.getGlobalAbilityValues().put("_ABILITY_Clorinde_Dodge_HealFlag", 0f);
        } else {
            target.getGlobalAbilityValues().put("_ABILITY_Clorinde_Dodge_HealFlag", 1f);
        }


        target.getWorld().broadcastPacket(new PacketServerGlobalValueChangeNotify(target, "_ABILITY_Clorinde_Dodge_HealFlag", 0f));


    if (target.isConvertToHpDebt() && ability.getOwner() != target) {
        if (target instanceof EntityAvatar avatar) {

        float healAmount = amountToRegenerate * abilityRatio * action.healRatio.get(ability, 1f);
            Grasscutter.getLogger().debug("Initial heal amount before applying debt ratio: {}", healAmount);

        if (avatar.getAvatar().getAvatarId() == 10000098) {
            healToHpDebtsRatio = 1.0f;
        }
        if (avatar.getAvatar().getAvatarId() == 10000096) {
            healToHpDebtsRatio = 0.0f;
        }

        healAmount *= healToHpDebtsRatio;

        float curDebt = target.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP_DEBTS);
        float newDebt = curDebt + healAmount;
        newDebt = Math.max(0, Math.min(2 * target.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP), newDebt));

        target.setFightProperty(FightProperty.FIGHT_PROP_CUR_HP_DEBTS, newDebt);
        target.getWorld().broadcastPacket(new PacketEntityFightPropUpdateNotify(target, FightProperty.FIGHT_PROP_CUR_HP_DEBTS));

        float changeDebt = newDebt - curDebt;
        if (changeDebt > 0) {
            target.getWorld().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(
                target,
                FightProperty.FIGHT_PROP_CUR_HP_DEBTS,
                changeDebt,
                PropChangeReasonOuterClass.PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY,
                ChangeHpDebtsReasonOuterClass.ChangeHpDebtsReason.CHANGE_HP_DEBTS_REASON_CHANGE_HP_DEBTS_ADD_ABILITY
            ));
        }

        Grasscutter.getLogger().warn("[HealHP] Converted {}% of healing ({}) to HP debt for target {}", healToHpDebtsRatio * 100, healAmount, target);

        return true;
    }
}

        if ("MizukiBurstSelf".equals(healTag)) {
            amountToRegenerate *= 2.0f;  // Double the healing amount
            Grasscutter.getLogger().debug("Healing increased by 100% for target {}", target);
        }


        // --- New Functionality: Capture pre-heal HP before applying heal ---
        float preHealHp = target.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
        float preMaxHp = target.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);

        // Apply the healing if the mixin isnt active
        target.heal(
                amountToRegenerate * abilityRatio * action.healRatio.get(ability, 1f),
                action.muteHealEffect);

        // --- New Functionality: Schedule Furina's heal on nearby party members (if not already active) ---
        if (target instanceof EntityAvatar avatar) {
            // Ensure Furina is in the team
            var team = ability.getPlayerOwner().getTeamManager().getActiveTeam();
            EntityAvatar furina = null;
            for (EntityAvatar member : team) {
                if (member.getAvatar().getAvatarId() == 10000089) {
                    furina = member;
                    break;
                }
            }

        float curHp = target.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
        float maxHp = target.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
        if (curHp < maxHp && furina != null) {
                float energyToAdd = 4.0f;
                furina.addEnergy(energyToAdd, PropChangeReasonOuterClass.PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY);
            }

        if (furina != null && ability.getOwner() != furina) { // Healing source is not Furina
            if (preHealHp >= preMaxHp) { // Healing overflow condition.
                Float scheduledFlag = furina.getGlobalAbilityValues().get("_FurinaScheduledHealActive");
                    if (scheduledFlag != null && scheduledFlag > 0) {
                        // Already scheduled, so do nothing.
                    } else {
                        furina.getGlobalAbilityValues().put("_FurinaScheduledHealActive", 1f);
                        int[] delays = {2, 4}; // Ticks at which to apply the heal.
                        for (int delay : delays) {
                            final int d = delay;
                            final EntityAvatar finalFurina = furina;

                            Grasscutter.getGameServer().getScheduler().scheduleDelayedTask(() -> {
                                for (EntityAvatar member : team) {
                                    float teamHeal = member.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP) * 0.02f;
                                    member.heal(teamHeal, false);
                                    Grasscutter.getLogger().info("Furina's scheduled heal: healed {} for {}", member, teamHeal);
                                }
                                if (d == 4) {
                                    // Clear the flag after the final tick.
                                    finalFurina.getGlobalAbilityValues().put("_FurinaScheduledHealActive", 0f);
                                }
                            }, delay);
                        }
                    }
                }
            }
        }
        // --- End New Functionality ---
        return true;
    }
}
