package emu.grasscutter.server.packet.send;

import com.google.protobuf.CodedOutputStream;
import emu.grasscutter.net.packet.*;
import java.io.ByteArrayOutputStream;

public class PacketSceneDataNotify extends BasePacket {

    private static final int F_SCENE_ID = 3;

    public PacketSceneDataNotify(int sceneId) {
        super(PacketOpcodes.SceneDataNotify);
        this.setData(build(sceneId));
    }

    private static byte[] build(int sceneId) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(16);
            CodedOutputStream cos = CodedOutputStream.newInstance(baos);
            if (sceneId != 0) cos.writeUInt32(F_SCENE_ID, sceneId);
            cos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PacketSceneDataNotify.build failed", e);
        }
    }
}
