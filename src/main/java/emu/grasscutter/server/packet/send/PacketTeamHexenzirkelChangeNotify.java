package emu.grasscutter.server.packet.send;

import com.google.protobuf.CodedOutputStream;
import emu.grasscutter.net.packet.*;
import java.io.ByteArrayOutputStream;

public final class PacketTeamHexenzirkelChangeNotify extends BasePacket {

    private static final int F_INFO  = 1;
    private static final int F_TYPE  = 9;
    private static final int F_LEVEL = 6;

    public PacketTeamHexenzirkelChangeNotify(int hexenzirkelCount) {
        super(PacketOpcodes.TeamHexenzirkelChangeNotify);
        this.setData(build(hexenzirkelCount));
    }

    private static byte[] build(int level) {
        try {
            ByteArrayOutputStream innerBaos = new ByteArrayOutputStream(8);
            CodedOutputStream inner = CodedOutputStream.newInstance(innerBaos);
            if (level > 0) inner.writeUInt32(F_LEVEL, level);
            inner.flush();
            byte[] innerBytes = innerBaos.toByteArray();

            ByteArrayOutputStream outerBaos = new ByteArrayOutputStream(innerBytes.length + 4);
            CodedOutputStream outer = CodedOutputStream.newInstance(outerBaos);
            outer.writeByteArray(F_INFO, innerBytes);
            outer.flush();
            return outerBaos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PacketTeamHexenzirkelChangeNotify.build failed", e);
        }
    }
}
