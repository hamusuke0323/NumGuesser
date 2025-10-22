package com.hamusuke.numguesser.game.phase;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;

public enum PhaseType {
    PREPARE,
    PULL,
    CHECK_BUDDY_ALIVE,
    SELECT_ATTACK_CARD,
    SELECT_TOSS_OR_ATTACK,
    ATTACK,
    TOSS,
    PAIR_ATTACK,
    CONTINUE_OR_STAY,
    END,
    PAIR_END;

    public static final StreamCodec<IntelligentByteBuf, PhaseType> STREAM_CODEC = StreamCodec.ofMember(PhaseType::write, buf -> buf.readEnum(PhaseType.class));

    private void write(final IntelligentByteBuf buf) {
        buf.writeEnum(this);
    }
}
