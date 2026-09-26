package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.RetcodeOuterClass.Retcode;
import emu.grasscutter.net.proto.SetOpenStateRspOuterClass.SetOpenStateRsp;

public class PacketSetOpenStateRsp extends BasePacket {

    public PacketSetOpenStateRsp(int openState, int value) {
        super(PacketOpcodes.SetOpenStateRsp);
        this.setData(SetOpenStateRsp.newBuilder().setKey(openState).setValue(value));
    }

    public PacketSetOpenStateRsp(Retcode retcode) {
        super(PacketOpcodes.SetOpenStateRsp);
        this.setData(SetOpenStateRsp.newBuilder().setRetcode(retcode.getNumber()));
    }
}
