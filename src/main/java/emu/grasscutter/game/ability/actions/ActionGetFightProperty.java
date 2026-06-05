package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.props.FightProperty;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import emu.grasscutter.Grasscutter;

@AbilityAction(AbilityModifierAction.Type.GetFightProperty)
public final class ActionGetFightProperty extends AbilityActionHandler {
    @Override
    public boolean execute(
        Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {

        var owner = ability.getOwner();
        var properties = new Object2FloatOpenHashMap<String>();

        for (var property : FightProperty.values()) {
            String name = property.name();
            float value = owner.getFightProperty(property);
            properties.put(name, value);
        }

        properties.putAll(ability.getAbilitySpecials());

        float fightPropertyValue = action.fightProp.get(properties, 0f);
        
        target.getGlobalAbilityValues().put(action.globalValueKey, fightPropertyValue);

        return true;
    }
}
