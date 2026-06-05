package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.AvatarFightPropNotifyOuterClass.AvatarFightPropNotify;
import java.util.HashMap;

public class PacketAvatarFightPropNotify extends BasePacket {

    private static final int PROP_HP_DEBTS = FightProperty.FIGHT_PROP_CUR_HP_DEBTS.getId();

    public PacketAvatarFightPropNotify(Avatar avatar) {
        super(PacketOpcodes.AvatarFightPropNotify);

        var props = new HashMap<>(avatar.getFightProperties());
        props.remove(PROP_HP_DEBTS);

        AvatarFightPropNotify proto =
                AvatarFightPropNotify.newBuilder()
                        .setAvatarGuid(avatar.getGuid())
                        .putAllFightPropMap(props)
                        .build();

        this.setData(proto);
    }
}
