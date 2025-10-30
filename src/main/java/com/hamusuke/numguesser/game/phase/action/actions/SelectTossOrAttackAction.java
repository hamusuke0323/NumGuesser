package com.hamusuke.numguesser.game.phase.action.actions;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.codec.StreamCodecs;

public final class SelectTossOrAttackAction extends BooleanAction {
    public static final StreamCodec<IntelligentByteBuf, SelectTossOrAttackAction> STREAM_CODEC = StreamCodecs.BOOLEAN.xmap(SelectTossOrAttackAction::new, SelectTossOrAttackAction::isToss);

    public SelectTossOrAttackAction(boolean isToss) {
        super(isToss);
    }

    public boolean isToss() {
        return this.flag;
    }
}
