package emu.grasscutter.game.dungeons;

import emu.grasscutter.net.proto.GrantReasonOuterClass;
import emu.grasscutter.net.proto.TrialAvatarGrantRecordOuterClass.TrialAvatarGrantRecord;
import java.util.List;
import lombok.*;

@Data
@AllArgsConstructor
public class DungeonTrialTeam {
    List<Integer> trialAvatarIds;
    GrantReasonOuterClass.GrantReason grantReason;
}
