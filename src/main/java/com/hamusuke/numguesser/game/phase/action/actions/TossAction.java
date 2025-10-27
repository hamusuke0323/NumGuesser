package com.hamusuke.numguesser.game.phase.action.actions;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.codec.StreamCodecs;

public final class TossAction extends CardIdAction {
    public static final StreamCodec<IntelligentByteBuf, TossAction> STREAM_CODEC = StreamCodecs.VAR_INT.xmap(TossAction::new, TossAction::cardId);

    public TossAction(final int cardId) {
        super(cardId);
    }
}
