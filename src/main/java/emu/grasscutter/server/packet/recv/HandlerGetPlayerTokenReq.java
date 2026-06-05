package emu.grasscutter.server.packet.recv;

import static emu.grasscutter.config.Configuration.ACCOUNT;

import emu.grasscutter.*;
import emu.grasscutter.database.DatabaseHelper;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.GetPlayerTokenReqOuterClass.GetPlayerTokenReq;
import emu.grasscutter.server.event.game.PlayerCreationEvent;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.game.GameSession.SessionState;
import emu.grasscutter.server.packet.send.PacketGetPlayerTokenRsp;
import emu.grasscutter.utils.*;
import emu.grasscutter.utils.helpers.ByteHelper;
import java.nio.ByteBuffer;
import java.security.Signature;
import javax.crypto.Cipher;

@Opcodes(PacketOpcodes.GetPlayerTokenReq)
public class HandlerGetPlayerTokenReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        var req = GetPlayerTokenReq.parseFrom(payload);

        var accountId = req.getAccountUid();
        var account = DispatchUtils.authenticate(accountId, req.getAccountToken());

        if (account == null && !DebugConstants.ACCEPT_CLIENT_TOKEN) {
            session.close();
            return;
        } else if (account == null && DebugConstants.ACCEPT_CLIENT_TOKEN) {
            account = DispatchUtils.getAccountById(accountId);
            if (account == null) {
                session.close();
                return;
            }
        }

        session.setAccount(account);

        boolean kicked = false;
        var exists = Grasscutter.getGameServer().getPlayerByAccountId(accountId);
        if (exists != null) {
            var existsSession = exists.getSession();
            if (existsSession != session) {
                exists.onLogout();
                existsSession.close();
                Grasscutter.getLogger()
                    .warn("Player {} was kicked due to duplicated login", account.getUsername());
                kicked = true;
            }
        }

        if (!kicked) {

            if (ACCOUNT.maxPlayer > -1
                && Grasscutter.getGameServer().getPlayers().size() >= ACCOUNT.maxPlayer) {
                session.close();
                return;
            }
        }

        var event = new PlayerCreationEvent(session, Player.class);
        event.call();

        var player = DatabaseHelper.getPlayerByAccount(account, event.getPlayerClass());

        if (player == null) {
            var nextPlayerUid =
                DatabaseHelper.getNextPlayerId(session.getAccount().getReservedPlayerUid());

            player =
                event.getPlayerClass().getDeclaredConstructor(GameSession.class).newInstance(session);

            DatabaseHelper.generatePlayerUid(player, nextPlayerUid);
        }

        session.setPlayer(player);

        if (session.getAccount().isBanned()) {
            session.setState(SessionState.ACCOUNT_BANNED);
            session.send(
                new PacketGetPlayerTokenRsp(
                    session, 21, "FORBID_CHEATING_PLUGINS", session.getAccount().getBanEndTime()));
            return;
        }

        player.loadFromDatabase();

        if (Grasscutter.getConfig().server.game.useXorEncryption) {
            session.setState(SessionState.WAITING_FOR_LOGIN);

            if (req.getKeyId() > 0) {
                var encryptSeed = session.getEncryptSeed();
                try {
                    var cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                    cipher.init(Cipher.DECRYPT_MODE, Crypto.CUR_SIGNING_KEY);

                    var clientSeedEncrypted = Utils.base64Decode(req.getClientRandKey());
                    var clientSeed = ByteBuffer.wrap(cipher.doFinal(clientSeedEncrypted)).getLong();

                    var combinedSeed = encryptSeed ^ clientSeed;
                    var seedBytes = ByteBuffer.wrap(new byte[8]).putLong(combinedSeed).array();

                    cipher.init(Cipher.ENCRYPT_MODE, Crypto.EncryptionKeys.get(req.getKeyId()));
                    var seedEncrypted = cipher.doFinal(seedBytes);

                    var privateSignature = Signature.getInstance("SHA256withRSA");
                    privateSignature.initSign(Crypto.CUR_SIGNING_KEY);
                    privateSignature.update(seedBytes);

                    session.send(new PacketGetPlayerTokenRsp(
                            session,
                            Utils.base64Encode(seedEncrypted),
                            Utils.base64Encode(privateSignature.sign()),
                            req.getKeyId()));
                    session.setUseSecretKey(true);
                } catch (Exception ignored) {

                    Grasscutter.getLogger().error("GetPlayerTokenReq RSA failed (key_id={}, clientRandKey len={}): {}",
                        req.getKeyId(),
                        req.getClientRandKey().isEmpty() ? 0 : Utils.base64Decode(req.getClientRandKey()).length,
                        ignored.getClass().getSimpleName() + ": " + ignored.getMessage());
                    var clientBytes = Utils.base64Decode(req.getClientRandKey());
                    var seed = ByteHelper.longToBytes(encryptSeed);
                    Crypto.xor(clientBytes, seed);

                    var base64str = Utils.base64Encode(clientBytes);
                    session.send(new PacketGetPlayerTokenRsp(session, base64str, "bm90aGluZyBoZXJl", req.getKeyId()));
                    session.setUseSecretKey(true);
                }
            } else {
                session.send(new PacketGetPlayerTokenRsp(session, req.getKeyId()));
            }
        } else {
            session.setState(SessionState.WAITING_FOR_LOGIN);
            session.send(new PacketGetPlayerTokenRsp(session, req.getKeyId()));
        }
    }
}
