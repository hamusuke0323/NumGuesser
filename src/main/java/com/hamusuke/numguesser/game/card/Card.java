package com.hamusuke.numguesser.game.card;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Objects;

public abstract class Card implements Comparable<Card> {
    private int id = -1;
    protected final CardColor cardColor;
    protected boolean opened;

    public Card(CardColor cardColor) {
        this.cardColor = cardColor;
    }

    public CardColor getCardColor() {
        return this.cardColor;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public abstract int getNum();

    public void open() {
        this.opened = true;
    }

    public boolean isOpened() {
        return this.opened;
    }

    public int getPoint() {
        return this.getNum() == 6 ? 20 : 10;
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

    public CardSerializer toSerializer() {
        return new CardSerializer(this.getId(), this.getCardColor(), this.getNum());
    }

    public CardSerializer toSerializerForOthers() {
        return new CardSerializer(this.getId(), this.getCardColor(), -1);
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
}
