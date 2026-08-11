package emu.grasscutter.game.ability.actions;

import java.util.stream.Collectors;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier.AbilityModifierAction;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.ability.AbilityManager;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.game.props.FightProperty;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import emu.grasscutter.game.entity.GameEntity;

public abstract class AbilityActionHandler {
    public abstract boolean execute(
            Ability ability, AbilityModifierAction action, ByteString abilityData, GameEntity target);
            protected AbilityManager abilityManager;
          
            public AbilityActionHandler setManager(AbilityManager mgr) {
                this.abilityManager = mgr;
                return this;
            }
    /**
     * Returns the target entity.
     *
     * @param ability The ability being invoked.
     * @param entity The entity invoking the ability.
     * @param target The target entity type.
     * @return The target entity.
     */
    /**
     * The names a dynamic value may be written in terms of.
     *
     * <p>The caster's fight properties and the ability's own constants were already here; the
     * marks the abilities keep on the caster were not, so a value written as
     * {@code %_ABILITY_Iansan_NyxCostRatio} - and 273 references across the corpus are of that
     * shape - resolved against nothing and came out as zero. Globals go in first so that a name
     * defined in both places still resolves the way it always did.
     */
    protected static Object2FloatMap<String> propertiesFor(Ability ability) {
        var properties = new Object2FloatOpenHashMap<String>();
        var owner = ability.getOwner();

        if (owner != null) {
            for (var property : FightProperty.values()) {
                properties.put(property.name(), owner.getFightProperty(property));
            }

            owner.getGlobalAbilityValues().forEach(properties::put);
        }

        properties.putAll(ability.getAbilitySpecials());
        return properties;
    }

    protected GameEntity getTarget(Ability ability, GameEntity entity, String target) {
        // An action that names no target acts on whatever the modifier is attached to. Sandrone's
        // robot has several of those, and switching on the absent name threw before the action ran.
        if (target == null) return entity;

        return switch (target) {
            default -> throw new RuntimeException("Unknown target type: " + target);


            case "Self" -> entity;
            case "Team" -> ability.getPlayerOwner().getTeamManager().getEntity();
            case "OriginOwner" -> ability.getPlayerOwner().getTeamManager().getCurrentAvatarEntity();
            case "Owner" -> ability.getOwner();
            case "Applier" -> entity; // TODO: Validate.
            case "CurLocalAvatar" -> ability
                    .getPlayerOwner()
                    .getTeamManager()
                    .getCurrentAvatarEntity(); // TODO: Validate.
            case "CasterOriginOwner" -> null; // TODO: Figure out.
        };
    }
}
