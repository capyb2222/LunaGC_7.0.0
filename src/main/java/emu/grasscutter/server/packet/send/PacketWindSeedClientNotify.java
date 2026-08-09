package emu.grasscutter.server.packet.send;

import com.google.protobuf.CodedOutputStream;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.config.Configuration;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PacketWindSeedClientNotify extends BasePacket {
    /**
     * 6.7 renumbered this message and the gagful opcode map never covered it, so neither the CmdId
     * nor the payload field number is confirmed. Both are read from config so candidates can be
     * tried with a restart instead of a rebuild.
     */
    static int cmdId() {
        int configured = Configuration.GAME_OPTIONS.watermark.cmdId;
        return configured > 0 ? configured : PacketOpcodes.WindSeedType1Notify;
    }

    /** True when the wind seed notify is switched off entirely - the safe setting if a CmdId hangs the client. */
    public static boolean disabled() {
        return Configuration.GAME_OPTIONS.watermark.cmdId < 0
                || Configuration.GAME_OPTIONS.watermark.cmdId == 0;
    }

    /** Encodes a payload at the configured field number. Shared by the other wind seed packets. */
    static byte[] encode(byte[] luac) {
        return encode(luac, Configuration.GAME_OPTIONS.watermark.payloadField);
    }

    /** Encodes a payload at an explicit field number, for sweeping candidates in one login. */
    static byte[] encode(byte[] luac, int field) {
        if (field <= 0) field = 1;

        var bos = new ByteArrayOutputStream();
        var out = CodedOutputStream.newInstance(bos);
        try {
            out.writeByteArray(field, luac);
            out.flush();
        } catch (IOException e) {
            Grasscutter.getLogger().error("Failed to encode the wind seed payload.", e);
        }
        return bos.toByteArray();
    }

    public PacketWindSeedClientNotify(String givenPath) {
        super(cmdId());
        final Path path = Paths.get(givenPath, new String[0]);
        byte[] data;
        try {
            data = Files.readAllBytes(path);
        } catch (Exception e) {
            data = FileUtils.readResource("/lua/UID.luac");
        }

        this.setData(encode(data));
    }

    public PacketWindSeedClientNotify(byte[] data) {
        super(cmdId());
        this.setData(encode(data));
    }

    /** One candidate of a sweep: an explicit CmdId and payload field rather than the configured pair. */
    public PacketWindSeedClientNotify(byte[] data, int cmdId, int payloadField) {
        super(cmdId);
        this.setData(encode(data, payloadField));
    }
}
