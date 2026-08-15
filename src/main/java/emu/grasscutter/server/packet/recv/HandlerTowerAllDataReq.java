package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketTowerAllDataRsp;
import java.util.TreeMap;

@Opcodes(PacketOpcodes.TowerAllDataReq)
public class HandlerTowerAllDataReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        var towerManager = session.getPlayer().getTowerManager();

        // Sent once, when the abyss screen is opened. Which floors the client will let you into is
        // decided entirely from this reply, so a floor showing locked is either missing its record
        // here or missing the entrance flag - turn this up to see which.
        if (Grasscutter.getLogger().isDebugEnabled()) {
            var stars = new TreeMap<Integer, Integer>();
            towerManager
                    .getRecordMap()
                    .forEach((floorId, record) -> stars.put(floorId, record.getStarCount()));
            Grasscutter.getLogger()
                    .debug(
                            "Tower all-data: entrance cleared={}, floor stars={}",
                            towerManager.canEnterScheduleFloor(),
                            stars);
        }

        session.send(
                new PacketTowerAllDataRsp(
                        session.getServer().getTowerSystem(), session.getPlayer().getTowerManager()));
    }
}
