package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.ability.AbilityManager;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.server.packet.send.PacketServerGlobalValueChangeNotify;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

/**
 * Writes an ability's global value.
 *
 * <p>V2 is the same write with more fields hung off it - a min/max range, an off-stage flag - none
 * of which this side acts on, so it lands here rather than in a near-copy. Its clamp is deliberately
 * not honoured: V1 carries {@code useLimitRange} too, in 974 places against V2's 235, and applying
 * it here would quietly change the long-standing path as well as the new one.
 */
@AbilityAction({
    AbilityModifierAction.Type.SetGlobalValue,
    AbilityModifierAction.Type.SetGlobalValueV2
})
public final class ActionSetGlobalValue extends AbilityActionHandler {

    @Override
    public boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target) {
                var properties = propertiesFor(ability);

                var valueKey = action.key;
                float computedValue = action.writtenValue().get(properties, 0f);
                target.getGlobalAbilityValues().put(valueKey, computedValue);
                target.onAbilityValueUpdate();

            // Team abilities run against a pseudo-entity that is in no scene, so this fired four
            // NPEs on every single login before the guard.
            if (!AbilityManager.isServerOwnedChain()) {
                var scene = target.getScene();
                var host = scene == null ? null : scene.getHost();
                if (host != null) {
                    host.sendPacket(
                            new PacketServerGlobalValueChangeNotify(target, valueKey, computedValue));
                }
            }
        return true;
    }
}
