package com.hamusuke.numguesser.client.game;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;
import com.hamusuke.numguesser.client.game.card.ClientFrameCard;
import com.hamusuke.numguesser.client.game.card.RemoteCard;
import com.hamusuke.numguesser.game.card.Card.CardColor;

import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import static com.hamusuke.numguesser.client.gui.component.table.SinglePlayerGameTable.VOID_CARD;

public class SinglePlayerGame {
    private final Difficulty difficulty;
    private final Random random = new SecureRandom();
    private final List<AbstractClientCard> deck = Lists.newArrayList();

    public SinglePlayerGame(final Difficulty difficulty) {
        this.difficulty = difficulty;
        this.setupDeck();
    }

    public static boolean isPermutationValid(Vector<Vector> cardTable) {
        for (var cards : cardTable) {
            var list = Lists.newArrayList(cards);
            list.removeIf(o -> o instanceof ClientFrameCard || o == VOID_CARD);

            for (int i = 0; i < list.size() - 1; i++) {
                if (((AbstractClientCard) list.get(i)).compareTo((AbstractClientCard) list.get(i + 1)) >= 0) {
                    return false;
                }
            }
        }

        return true;
    }

    protected void setupDeck() {
        this.deck.clear();
        for (var color : CardColor.values()) {
            for (int i = 0; i < 12; i++) {
                this.deck.add(this.createCard(color, i));
            }
        }

        Collections.shuffle(this.deck, this.random);
    }

    protected AbstractClientCard createCard(CardColor color, int num) {
        var card = new RemoteCard(color);
        card.setNum(num);
        return card;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    @Nullable
    public AbstractClientCard nextCard() {
        return this.deck.isEmpty() ? null : this.deck.remove(0);
    }

    @Nullable
    public AbstractClientCard getNextCard() {
        return this.deck.isEmpty() ? null : this.deck.get(0);
    }

    public enum Difficulty {
        EASY(8, 3, 7),
        HARD(6, 4, 11);

        public final int firstOpenedCardNum;
        public final int maxRowCardNum;
        public final int columnSize;
        public final int centerIndex;

        Difficulty(int firstOpenedCardNum, int maxRowCardNum, int columnSize) {
            this.firstOpenedCardNum = firstOpenedCardNum;
            this.maxRowCardNum = maxRowCardNum;
            this.columnSize = columnSize;
            this.centerIndex = this.columnSize / 2;
        }
    }
}
