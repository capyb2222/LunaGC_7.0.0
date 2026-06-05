package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.binout.AbilityModifier;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.ability.AbilityModifierController;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.props.FightProperty;

@AbilityAction(AbilityModifierAction.Type.AttachModifier)
public final class ActionAttachModifier extends AbilityActionHandler {

    @Override
    public boolean execute(Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        Grasscutter.getLogger().debug("[Ability] AttachModifier: {}", action.modifierName);

        var modifierData = ability.getData().modifiers.get(action.modifierName);
        if (modifierData == null) {
            Grasscutter.getLogger().debug("Modifier {} not found", action.modifierName);
            return false;
        }

        if ("AllPlayerAvatars".equals(action.target)) {
            return applyToAllPlayerAvatars(ability, action, modifierData, abilityData);
        }

        Grasscutter.getLogger().debug("[Ability] AttachModifier fallback target={}", action.target);

        ability.getModifiers().put(action.modifierName,
            new AbilityModifierController(ability, ability.getData(), modifierData));
        return true;
    }

    private boolean applyToAllPlayerAvatars(Ability ability, AbilityModifierAction action,
                                             AbilityModifier modifierData, ByteString abilityData) {
        var player = ability.getPlayerOwner();
        if (player == null) return false;

        float hpFloor = 0.0f;
        if (action.predicates != null) {
            for (var pred : action.predicates) {
                if (pred instanceof java.util.Map<?,?> map && "ByTargetHPRatio".equals(map.get("$type"))) {
                    var key = (String) map.get("HPRatio");
                    if (key != null) {
                        float val = ability.getAbilitySpecials().getOrDefault(key, 0.0f);
                        hpFloor = (val > 0f) ? val : 0.5f;
                    }
                    break;
                }
            }
        }

        var team = new java.util.ArrayList<>(player.getTeamManager().getActiveTeam());
        var manager = ability.getManager();
        var seen = new java.util.HashSet<Integer>();
        for (var avatarEntity : team) {
            if (!seen.add(avatarEntity.getId())) continue;
            if (hpFloor > 0f) {
                float maxHp = avatarEntity.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
                float curHp = avatarEntity.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
                if (maxHp > 0f && curHp / maxHp <= hpFloor) continue;
            }
            for (var a : modifierData.onAdded) {
                manager.executeAction(ability, a, abilityData, avatarEntity);
            }
        }
        return true;
    }
}
