package emu.grasscutter.server.packet.recv;

import com.google.protobuf.CodedInputStream;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.game.GameSession;

import java.util.ArrayList;
import java.util.List;

@Opcodes(PacketOpcodes.EvtDrawPlayBulletHitNotify)
public class HandlerEvtDrawPlayBulletHitNotify extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {

        // }
        CodedInputStream stream = CodedInputStream.newInstance(payload);
        List<Long> field1 = new ArrayList<>();
        List<Long> field3 = new ArrayList<>();
        long entityId = 0;

        int tag;
        while ((tag = stream.readTag()) != 0) {
            int fieldNum = tag >>> 3;
            int wireType = tag & 0x7;
            switch (fieldNum) {
                case 1 -> {
                    if (wireType == 2) {

                        int len = stream.readRawVarint32();
                        int limit = stream.pushLimit(len);
                        while (stream.getBytesUntilLimit() > 0) field1.add(stream.readRawVarint64());
                        stream.popLimit(limit);
                    } else {
                        field1.add(stream.readRawVarint64());
                    }
                }
                case 3 -> {
                    if (wireType == 2) {
                        int len = stream.readRawVarint32();
                        int limit = stream.pushLimit(len);
                        while (stream.getBytesUntilLimit() > 0) field3.add(stream.readRawVarint64());
                        stream.popLimit(limit);
                    } else {
                        field3.add(stream.readRawVarint64());
                    }
                }
                case 12 -> entityId = stream.readRawVarint64();
                default -> stream.skipField(tag);
            }
        }

    }
}
