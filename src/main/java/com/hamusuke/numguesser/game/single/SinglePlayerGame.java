package com.hamusuke.numguesser.game.single;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hamusuke.numguesser.client.game.card.RemoteCard;
import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.card.Card.CardColor;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SinglePlayerGame<C extends Card> {
    private final Difficulty difficulty;
    private final Random random = new SecureRandom();
    private final List<C> deck = Lists.newArrayList();
    private final Map<Integer, List<C>> cardTable = Maps.newHashMap();

    public SinglePlayerGame(final Difficulty difficulty) {
        this.difficulty = difficulty;
        this.setupDeck();
    }

    private static <C extends Card> boolean isPermutationValid(List<C> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).compareTo(list.get(i + 1)) >= 0) {
                return false;
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

    protected C createCard(CardColor color, int num) {
        var card = new RemoteCard(color);
        card.setNum(num);
        return (C) card;
    }

    public void start() {
        this.cardTable.clear();
        this.lineupCards();
    }

    private void lineupCards() {
        for (int i = 0; i < this.difficulty.firstOpenedCardNum; i++) {
            var list = Lists.<C>newArrayList();
            list.add(this.deck.remove(0));
            this.cardTable.put(i, list);
        }
    }

    public void putCard(int listIndex, int cardIndex) {
        var list = this.cardTable.get(listIndex);
        if (list == null) {
            return;
        }

        list.add(cardIndex, this.deck.remove(0));

        if (!isPermutationValid(list)) {
            this.endGame();
        }
    }

    protected void endGame() {

    }

    public enum Difficulty {
        EASY(8, 3),
        HARD(6, 4);

        private final int firstOpenedCardNum;
        private final int maxRowCardNum;

        Difficulty(int firstOpenedCardNum, int maxRowCardNum) {
            this.firstOpenedCardNum = firstOpenedCardNum;
            this.maxRowCardNum = maxRowCardNum;
        }
    }
}
