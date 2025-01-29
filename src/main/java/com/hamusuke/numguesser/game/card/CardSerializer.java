package com.hamusuke.numguesser.game.card;

import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.ClientFrameCard;
import com.hamusuke.numguesser.client.game.card.LocalCard;
import com.hamusuke.numguesser.client.game.card.RemoteCard;
import com.hamusuke.numguesser.game.card.Card.CardColor;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;

public record CardSerializer(int id, CardColor cardColor, int num) {
    public static final StreamCodec<IntelligentByteBuf, CardSerializer> STREAM_CODEC = StreamCodec.ofMember(CardSerializer::write, CardSerializer::new);

    private CardSerializer(IntelligentByteBuf buf) {
        this(buf.readVarInt(), buf.readEnum(CardColor.class), buf.readVarInt());
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeEnum(this.cardColor);
        buf.writeVarInt(this.num);
    }

    public AbstractClientCard toClientCard() {
        if (this.num() == -2) {
            return new ClientFrameCard();
        }

        var card = this.num() >= 0 ? new LocalCard(this.cardColor(), this.num()) : new RemoteCard(this.cardColor());
        card.setId(this.id);
        return card;
    }
}
