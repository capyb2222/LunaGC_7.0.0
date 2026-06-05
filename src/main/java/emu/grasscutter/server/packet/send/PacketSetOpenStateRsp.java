package emu.grasscutter.server.packet.send;

import com.google.protobuf.CodedOutputStream;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.RetcodeOuterClass.Retcode;
import java.io.ByteArrayOutputStream;

public class PacketSetOpenStateRsp extends BasePacket {

    private static final int F_RETCODE = 5;
    private static final int F_VALUE   = 13;
    private static final int F_KEY     = 15;

    public PacketSetOpenStateRsp(int openState, int value) {
        super(PacketOpcodes.SetOpenStateRsp);
        this.setData(buildRsp(0, openState, value));
    }

    public PacketSetOpenStateRsp(Retcode retcode) {
        super(PacketOpcodes.SetOpenStateRsp);
        this.setData(buildRsp(retcode.getNumber(), 0, 0));
    }

    private static byte[] buildRsp(int retcode, int key, int value) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(16);
            CodedOutputStream cos = CodedOutputStream.newInstance(baos);
            if (retcode != 0) cos.writeInt32(F_RETCODE, retcode);
            if (value   != 0) cos.writeUInt32(F_VALUE,  value);
            if (key     != 0) cos.writeUInt32(F_KEY,    key);
            cos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PacketSetOpenStateRsp.buildRsp failed", e);
        }
    }
}
