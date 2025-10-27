package com.hamusuke.numguesser.game.phase.action.actions;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.codec.StreamCodecs;

public final class ContinueOrStayAction extends BooleanAction {
    public static final StreamCodec<IntelligentByteBuf, ContinueOrStayAction> STREAM_CODEC = StreamCodecs.BOOLEAN.xmap(ContinueOrStayAction::new, ContinueOrStayAction::continueAttacking);

    public ContinueOrStayAction(final boolean continueAttacking) {
        super(continueAttacking);
    }

    public boolean continueAttacking() {
        return this.flag;
    }
}
