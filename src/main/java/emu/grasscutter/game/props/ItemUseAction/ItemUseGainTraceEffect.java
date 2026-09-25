package emu.grasscutter.game.props.ItemUseAction;

import emu.grasscutter.data.GameData;
import emu.grasscutter.game.props.ItemUseAction.ItemUseInt;
import emu.grasscutter.game.props.ItemUseAction.UseItemParams;
import emu.grasscutter.game.props.ItemUseOp;

public class ItemUseGainTraceEffect
extends ItemUseInt {
    public ItemUseGainTraceEffect(String[] useParam) {
        super(useParam);
    }

    @Override
    public ItemUseOp getItemUseOp() {
        return ItemUseOp.ITEM_USE_UNLOCK_AVATAR_TRACE;
    }

    @Override
    public boolean useItem(UseItemParams params) {
        if (GameData.getAvatarTraceEffectDataMap().containsKey(this.i)) {
            params.player.addTraceEffect(this.i);
        }
        return true;
    }
}

