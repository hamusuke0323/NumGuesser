package com.hamusuke.numguesser.game.phase.action.actions;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.codec.StreamCodecs;

public final class SelectCardForAttackAction extends CardIdAction {
    public static final StreamCodec<IntelligentByteBuf, SelectCardForAttackAction> STREAM_CODEC = StreamCodecs.VAR_INT.xmap(SelectCardForAttackAction::new, CardIdAction::cardId);

    public SelectCardForAttackAction(final int cardId) {
        super(cardId);
    }
}
