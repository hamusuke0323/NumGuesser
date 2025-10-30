package com.hamusuke.numguesser.game.phase.action.actions;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;

public final class ButtonPressAction {
    public static final ButtonPressAction INSTANCE = new ButtonPressAction();
    public static final StreamCodec<IntelligentByteBuf, ButtonPressAction> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private ButtonPressAction() {
    }
}
