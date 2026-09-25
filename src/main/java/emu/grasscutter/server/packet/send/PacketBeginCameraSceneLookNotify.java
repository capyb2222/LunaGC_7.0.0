package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.world.Position;
import emu.grasscutter.net.packet.*;
import java.util.*;
import lombok.*;

public final class PacketBeginCameraSceneLookNotify extends BasePacket {

    public PacketBeginCameraSceneLookNotify(CameraSceneLookNotify parameters) {
        super(PacketOpcodes.BeginCameraSceneLookNotify);
    }

    @Data
    @NoArgsConstructor
    public static class CameraSceneLookNotify {
        Position lookPos = new Position();
        Position followPos = new Position();
        float duration = 0.0f;
        boolean isAllowInput = true;
        boolean setFollowPos = false;
        boolean isScreenXY = false;
        boolean recoverKeepCurrent = true;
        boolean isForceWalk = false;
        boolean isForce = false;
        boolean isChangePlayMode = false;
        float screenY = 0.0f;
        float screenX = 0.0f;
        int entityId = 0;
        Collection<String> otherParams = new ArrayList<>(0);
    }
}
