package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.GameData;
import emu.grasscutter.game.quest.enums.QuestCond;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.ItemGivingReqOuterClass.ItemGivingReq;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketItemGivingRsp;
import emu.grasscutter.server.packet.send.PacketItemGivingRsp.Mode;
import java.util.*;

@Opcodes(PacketOpcodes.ItemGivingReq)
public final class HandlerItemGivingReq extends PacketHandler {
    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        var req = ItemGivingReq.parseFrom(payload);

        var player = session.getPlayer();
        var inventory = player.getInventory();

        var giveId = req.getGivingId();
        var items = req.getItemParamListList();
    }
}
