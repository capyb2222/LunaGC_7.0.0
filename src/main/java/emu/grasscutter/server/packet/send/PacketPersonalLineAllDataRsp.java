package emu.grasscutter.server.packet.send;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.quest.*;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.PersonalLineAllDataRspOuterClass;
import java.util.*;
import java.util.stream.Collectors;

public class PacketPersonalLineAllDataRsp extends BasePacket {

    public PacketPersonalLineAllDataRsp(Collection<GameMainQuest> gameMainQuestList) {
        super(PacketOpcodes.PersonalLineAllDataRsp);

        var proto = PersonalLineAllDataRspOuterClass.PersonalLineAllDataRsp.newBuilder();

        var questList =
                gameMainQuestList.stream()
                        .map(GameMainQuest::getChildQuests)
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .map(GameQuest::getSubQuestId)
                        .collect(Collectors.toSet());

        // can_be_unlocked_personal_line_list is one of three unnamed `repeated uint32` fields in the
        // 7.0 dump, and nothing distinguishes them, so the list is left empty rather than written to
        // a field that may mean something else entirely.
        GameData.getPersonalLineDataMap().values().stream()
                .filter(i -> !questList.contains(i.getStartQuestId()))
                .forEach(i -> {});

        this.setData(proto);
    }
}
