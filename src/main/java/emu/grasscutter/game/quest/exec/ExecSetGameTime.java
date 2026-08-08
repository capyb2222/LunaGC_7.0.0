package emu.grasscutter.game.quest.exec;

import emu.grasscutter.data.excels.quest.QuestData;
import emu.grasscutter.game.quest.*;
import emu.grasscutter.game.quest.enums.QuestExec;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import lombok.val;

@QuestValueExec(QuestExec.QUEST_EXEC_SET_GAME_TIME)
public class ExecSetGameTime extends QuestExecHandler {
    @Override
    public boolean execute(GameQuest quest, QuestData.QuestExecParam condition, String... paramStr) {
        if (paramStr.length < 1) return false;
        val timeArgs = paramStr[0].split("\\.");
        try {
            val hours = Integer.parseInt(timeArgs[0]);
            val minutes = timeArgs.length > 1 ? Integer.parseInt(timeArgs[1]) : 0;
            val targetTime = hours * 60 + minutes;
            quest.getOwner().getWorld().changeTime(targetTime, 0);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}