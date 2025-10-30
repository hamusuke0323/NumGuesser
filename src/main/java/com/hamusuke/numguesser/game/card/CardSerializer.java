package com.hamusuke.numguesser.game.card;

import com.hamusuke.numguesser.game.card.Card.CardColor;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;

import static com.hamusuke.numguesser.game.card.Card.UNKNOWN;

public record CardSerializer(int id, CardColor cardColor, int num) {
    public static final CardSerializer DUMMY = new CardSerializer(-1, CardColor.BLACK, -1);
    public static final StreamCodec<IntelligentByteBuf, CardSerializer> STREAM_CODEC = StreamCodec.ofMember(CardSerializer::write, CardSerializer::new);

    private CardSerializer(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readEnum(CardColor.class), buf.readVarInt());
    }

    public CardSerializer toUnknown() {
        return new CardSerializer(this.id, this.cardColor, UNKNOWN);
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeEnum(this.cardColor);
        buf.writeVarInt(this.num);
    }
}
