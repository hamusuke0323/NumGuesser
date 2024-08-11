package com.hamusuke.numguesser.game.card;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Objects;

public abstract class Card implements Comparable<Card> {
    protected final CardColor cardColor;
    protected boolean opened;

    public Card(CardColor cardColor) {
        this.cardColor = cardColor;
    }

    public CardColor getCardColor() {
        return this.cardColor;
    }

    public abstract int getNum();

    public void open() {
        this.opened = true;
    }

    public boolean isOpened() {
        return this.opened;
    }

    @Override
    public int compareTo(@Nonnull Card card) {
        if (this.equals(card)) {
            return 0;
        }

        if (this.getNum() < card.getNum()) {
            return -1;
        } else if (this.getNum() > card.getNum()) {
            return 1;
        } else {
            return this.cardColor.compareTo(card.cardColor);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return this.getNum() == card.getNum() && cardColor == card.cardColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardColor, this.getNum());
    }

    @Override
    public String toString() {
        return this.cardColor + " Card: " + getNum();
    }

    public enum CardColor {
        BLACK(Color.WHITE, Color.BLACK),
        WHITE(Color.BLACK, Color.WHITE);

        private final Color textColor;
        private final Color bgColor;

        CardColor(Color textColor, Color bgColor) {
            this.textColor = textColor;
            this.bgColor = bgColor;
        }

        public Color getTextColor() {
            return this.textColor;
        }

        public Color getBgColor() {
            return this.bgColor;
        }
    }

    public record CardSerializer(CardColor cardColor, int num) {
        public CardSerializer(IntelligentByteBuf buf) {
            this(buf.readEnum(CardColor.class), buf.readVarInt());
        }

        public void writeTo(IntelligentByteBuf buf) {
            buf.writeEnum(this.cardColor);
            buf.writeVarInt(this.num);
        }
    }
}
