package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.server.packet.send.PacketServerGlobalValueChangeNotify;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

@AbilityAction(AbilityModifierAction.Type.SetGlobalValue)
public final class ActionSetGlobalValue extends AbilityActionHandler {

    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
                var owner = ability.getOwner();
                var properties = new Object2FloatOpenHashMap<String>();

                for (var property : FightProperty.values()) {
                    var name = property.name();
                    var value = owner.getFightProperty(property);
                    properties.put(name, value);
                }

                properties.putAll(ability.getAbilitySpecials());

                var valueKey = action.key;
                var value = action.ratio;
                float computedValue = value.get(properties, 0f);
                target.getGlobalAbilityValues().put(valueKey, computedValue);
                target.onAbilityValueUpdate();

            target.getScene().getHost().sendPacket(new PacketServerGlobalValueChangeNotify(target, valueKey, computedValue));
        return true;
    }
}
