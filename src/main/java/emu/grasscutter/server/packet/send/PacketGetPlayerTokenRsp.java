package emu.grasscutter.server.packet.send;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.utils.Crypto;
import java.io.ByteArrayOutputStream;

public class PacketGetPlayerTokenRsp extends BasePacket {

    private static final int F_TOKEN                     = 2;
    private static final int F_SECURITY_CMD_BUFFER       = 9;
    private static final int F_PLATFORM_TYPE             = 11;
    private static final int F_UID                       = 12;
    private static final int F_CLIENT_IP_STR             = 186;
    private static final int F_CLIENT_VERSION_RANDOM_KEY = 253;
    private static final int F_COUNTRY_CODE              = 682;
    private static final int F_KEY_ID                    = 1020;
    private static final int F_SERVER_RAND_KEY           = 1127;
    private static final int F_SIGN                      = 1555;

    public PacketGetPlayerTokenRsp(GameSession session, int keyId) {
        super(PacketOpcodes.GetPlayerTokenRsp, true);
        this.setUseDispatchKey(true);
        this.setData(buildRsp(
            session.getPlayer().getUid(),
            session.getAccount().getToken(),
            new byte[0], keyId, 3, "US",
            session.getAddress().getAddress().getHostAddress(),
            "", "", "c25-314dd05b0b5f"
        ));
    }

    public PacketGetPlayerTokenRsp(GameSession session, int retcode, String msg, int blackEndTime) {
        super(PacketOpcodes.GetPlayerTokenRsp, true);
        this.setUseDispatchKey(true);
        this.setData(buildRsp(
            session.getPlayer().getUid(),
            session.getAccount().getToken(),
            new byte[0], 0, 3, "US",
            session.getAddress().getAddress().getHostAddress(),
            "", "", "c25-314dd05b0b5f"
        ));
    }

    public PacketGetPlayerTokenRsp(
        GameSession session, String encryptedSeed, String encryptedSeedSign, int keyId) {
        super(PacketOpcodes.GetPlayerTokenRsp, true);
        this.setUseDispatchKey(true);
        this.setData(buildRsp(
            session.getPlayer().getUid(),
            session.getAccount().getToken(),
            Crypto.ENCRYPT_SEED_BUFFER,
            keyId, 3, "US",
            session.getAddress().getAddress().getHostAddress(),
            encryptedSeed,
            encryptedSeedSign,
            "c25-314dd05b0b5f"
        ));
    }

    private static byte[] buildRsp(
        int uid, String token, byte[] secCmdBuf, int keyId, int platformType,
        String countryCode, String clientIpStr, String serverRandKey,
        String sign, String clientVersionRandomKey) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
            CodedOutputStream cos = CodedOutputStream.newInstance(baos);
            cos.writeString(F_TOKEN,                     token);
            if (secCmdBuf.length > 0)
                cos.writeBytes(F_SECURITY_CMD_BUFFER,    ByteString.copyFrom(secCmdBuf));
            cos.writeUInt32(F_PLATFORM_TYPE,             platformType);
            cos.writeUInt32(F_UID,                       uid);
            cos.writeString(F_CLIENT_IP_STR,             clientIpStr);
            cos.writeString(F_CLIENT_VERSION_RANDOM_KEY, clientVersionRandomKey);
            cos.writeString(F_COUNTRY_CODE,              countryCode);
            if (keyId > 0)
                cos.writeUInt32(F_KEY_ID,                keyId);
            if (!serverRandKey.isEmpty())
                cos.writeString(F_SERVER_RAND_KEY,       serverRandKey);
            if (!sign.isEmpty())
                cos.writeString(F_SIGN,                  sign);
            cos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("buildRsp failed", e);
        }
    }
}
