package emu.grasscutter.server.packet.send;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.CombatInvocationsNotifyOuterClass.CombatInvocationsNotify;
import emu.grasscutter.net.proto.CombatInvokeEntryOuterClass.CombatInvokeEntry;
import emu.grasscutter.net.proto.CombatTypeArgumentOuterClass.CombatTypeArgument;
import emu.grasscutter.net.proto.ForwardTypeOuterClass.ForwardType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PacketEvtBeingHealedNotify extends BasePacket {
    public PacketEvtBeingHealedNotify(GameEntity source, GameEntity target, float healAmount, float realHealAmount) {
        super(PacketOpcodes.CombatInvocationsNotify, true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        CodedOutputStream out = CodedOutputStream.newInstance(bos);
        try {
            out.writeFloat(4, healAmount);
            out.writeUInt32(9, target.getId());
            out.writeFloat(14, realHealAmount);
            out.writeUInt32(15, source != null ? source.getId() : target.getId());
            out.flush();
        } catch (IOException e) {
        }

        CombatInvokeEntry entry = CombatInvokeEntry.newBuilder()
                .setArgumentType(CombatTypeArgument.CombatTypeArgument_COMBAT_BEING_HEALED_NTF)
                .setForwardType(ForwardType.ForwardType_FORWARD_TO_ALL)
                .setCombatData(ByteString.copyFrom(bos.toByteArray()))
                .build();

        CombatInvocationsNotify proto = CombatInvocationsNotify.newBuilder()
                .addInvokeList(entry)
                .build();

        this.setData(proto);
    }
}
