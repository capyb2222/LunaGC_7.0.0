package emu.grasscutter.game.ability.mixins;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityMixinData;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.ability.actions.AbilityActionHandler;

public abstract class AbilityMixinHandler {

    public abstract boolean execute(
            Ability ability, AbilityMixinData mixinData, ByteString abilityData,  GameEntity target);
            protected GameEntity getTarget(Ability ability, GameEntity entity, String target) {
                return AbilityActionHandler.resolveTarget(ability, entity, target);
            }
        }
        
