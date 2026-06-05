package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.EvtCreateGadgetNotifyOuterClass.EvtCreateGadgetNotify;
import emu.grasscutter.server.game.GameSession;

@Opcodes(PacketOpcodes.EvtCreateGadgetNotify)
public class HandlerEvtCreateGadgetNotify extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        EvtCreateGadgetNotify notify = EvtCreateGadgetNotify.parseFrom(payload);

        var scene = session.getPlayer().getScene();

        if (scene.getEntityById(notify.getEntityId()) != null) {
            return;
        }

        var gadgetId = notify.getConfigId();
        EntityClientGadget gadget =
                switch (gadgetId) {

                    case EntitySolarIsotomaClientGadget.GADGET_ID -> new EntitySolarIsotomaClientGadget(
                            session.getPlayer().getScene(), session.getPlayer(), notify);

                    default -> new EntityClientGadget(
                            session.getPlayer().getScene(), session.getPlayer(), notify);
                };

        session.getPlayer().getScene().onPlayerCreateGadget(gadget);

        session.getPlayer().getAbilityManager().pushBulletTalentVars(gadget);
    }
}
