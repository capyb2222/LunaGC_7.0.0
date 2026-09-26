package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.CombatInvocationsNotifyOuterClass.CombatInvocationsNotify;
import emu.grasscutter.net.proto.CombatInvokeEntryOuterClass.CombatInvokeEntry;
import emu.grasscutter.net.proto.CombatTypeArgumentOuterClass.CombatTypeArgument;
import emu.grasscutter.net.proto.EvtBeingHealedNotifyOuterClass.EvtBeingHealedNotify;
import emu.grasscutter.net.proto.ForwardTypeOuterClass.ForwardType;

public class PacketEvtBeingHealedNotify extends BasePacket {
    public PacketEvtBeingHealedNotify(GameEntity source, GameEntity target, float healAmount, float realHealAmount) {
        super(PacketOpcodes.CombatInvocationsNotify, true);

        var heal = EvtBeingHealedNotify.newBuilder()
                .setHealAmount(healAmount)
                .setTargetId(target.getId())
                .setRealHealAmount(realHealAmount)
                .setSourceId(source != null ? source.getId() : target.getId())
                .build();

        CombatInvokeEntry entry = CombatInvokeEntry.newBuilder()
                .setArgumentType(CombatTypeArgument.CombatTypeArgument_COMBAT_BEING_HEALED_NTF)
                .setForwardType(ForwardType.ForwardType_FORWARD_TO_ALL)
                .setCombatData(heal.toByteString())
                .build();

        CombatInvocationsNotify proto = CombatInvocationsNotify.newBuilder()
                .addInvokeList(entry)
                .build();

        this.setData(proto);
    }
}
