package emu.grasscutter.server.packet.send;

import com.google.protobuf.CodedOutputStream;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import java.io.ByteArrayOutputStream;

public class PacketEvtLocalGadgetOwnerLeaveSceneNotify extends BasePacket {

    public PacketEvtLocalGadgetOwnerLeaveSceneNotify(int entityId) {
        super(PacketOpcodes.EvtLocalGadgetOwnerLeaveSceneNotify);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CodedOutputStream cos = CodedOutputStream.newInstance(baos);
            cos.writeUInt32(7, entityId);
            cos.writeBytes(13, com.google.protobuf.ByteString.copyFrom(new byte[]{1}));
            cos.flush();
            this.setData(baos.toByteArray());
        } catch (Exception e) {
            this.setData(new byte[0]);
        }
    }
}
