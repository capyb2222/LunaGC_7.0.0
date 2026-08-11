package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.data.common.DynamicFloat;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.ability.AbilityManager;
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
                // The same field carries a multiplier for the actions that multiply by it, so it
                // defaults to one - which is the wrong answer for an action that WRITES it. A
                // config with no value of its own still holds that shared default instance, and
                // writing it turned every bare "clear this mark" into "set this mark".
                float computedValue = value == DynamicFloat.ONE ? 0f : value.get(properties, 0f);
                target.getGlobalAbilityValues().put(valueKey, computedValue);
                target.onAbilityValueUpdate();

            if (!AbilityManager.isServerOwnedChain()) {
                target.getScene().getHost().sendPacket(new PacketServerGlobalValueChangeNotify(target, valueKey, computedValue));
            }
        return true;
    }
}
