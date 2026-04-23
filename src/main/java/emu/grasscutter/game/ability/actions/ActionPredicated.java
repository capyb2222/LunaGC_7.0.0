package emu.grasscutter.game.ability.actions;

import com.google.protobuf.ByteString;
import emu.grasscutter.data.binout.AbilityModifier;
import emu.grasscutter.game.ability.Ability;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.ability.AbilityManager;
import emu.grasscutter.game.ability.AbilityModifierController;
import emu.grasscutter.net.proto.ChangeHpReasonOuterClass.ChangeHpReason;
import emu.grasscutter.server.packet.send.PacketEntityFightPropChangeReasonNotify;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;
import emu.grasscutter.net.proto.PropChangeReasonOuterClass.PropChangeReason;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.proto.PropChangeReasonOuterClass;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.server.packet.send.PacketServerGlobalValueChangeNotify;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.ability.actions.ActionChangePhlogiston;

@AbilityAction(value = AbilityModifier.AbilityModifierAction.Type.Predicated)
public final class ActionPredicated extends AbilityActionHandler {

    private static final float[] DAMAGE_MULTIPLIERS = {0.024f, 0.016f, 0.024f, 0.016f, 0.024f, 0.016f, 0.024f, 0.036f};
    private static final Random RANDOM = new Random();

    @Override
    public boolean execute(Ability ability, AbilityModifier.AbilityModifierAction action, ByteString abilityData, GameEntity target) {
        Grasscutter.getLogger().info("Predicated action executed for Target: {}", target.getId());
        var owner = ability.getOwner();
        Grasscutter.getLogger().info("Predicated action details: {}", ability.getData().abilityName);
        if (owner instanceof EntityClientGadget ownerGadget) {
            owner = ownerGadget.getScene().getEntityById(ownerGadget.getOwnerEntityId());
        }

         if (owner instanceof EntityAvatar avatar && avatar.getAvatar().getAvatarId() == 10000089) {
            var team = ability.getPlayerOwner().getTeamManager().getActiveTeam();
            float multiplier = DAMAGE_MULTIPLIERS[RANDOM.nextInt(DAMAGE_MULTIPLIERS.length)];

            for (EntityAvatar teamMember : team) {
                float curHP = teamMember.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
                float maxHP = teamMember.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
                float consumeHP = multiplier * maxHP;
                int avatarId = teamMember.getAvatar().getAvatarId();

                boolean isFurina = avatarId == 10000089;

                if ((isFurina && curHP > 0.55f * maxHP) || (!isFurina && curHP > 0.5f * maxHP)) {
                    teamMember.damage(consumeHP);
                    teamMember.getWorld().broadcastPacket(new PacketEntityFightPropChangeReasonNotify(
                        teamMember,
                        FightProperty.FIGHT_PROP_CUR_HP,
                        -consumeHP,
                        PropChangeReasonOuterClass.PropChangeReason.PropChangeReason_PROP_CHANGE_ABILITY,
                        ChangeHpReason.ChangeHpReason_CHANGE_HP_SUB_ABILITY
                    ));
                }
            }
        }
    return true;
    }
}
