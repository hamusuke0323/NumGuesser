package com.hamusuke.numguesser.server.game.card;

import com.hamusuke.numguesser.game.card.Card;
import com.hamusuke.numguesser.game.card.CardSerializer;
import com.hamusuke.numguesser.network.Player;

import java.util.function.Predicate;

public class ServerCard extends Card {
    private final int num;

    public ServerCard(CardColor cardColor, int num) {
        super(cardColor);
        this.num = num;
    }

    @Override
    public int getNum() {
        return this.num;
    }

    public CardSerializer toSerializer() {
        return this.toSerializer(VisibleTester.EVERYONE);
    }

    public CardSerializer toSerializer(final VisibleTester visibleTester) {
        return new CardSerializer(this.getId(), this.getCardColor(), visibleTester.test(this) ? this.getNum() : UNKNOWN);
    }

    public sealed interface VisibleTester extends Predicate<ServerCard> permits VisibleTester.EveryOne, VisibleTester.Never, VisibleTester.OnlyOwner {
        EveryOne EVERYONE = new EveryOne();
        Never NEVER = new Never();

        final class EveryOne implements VisibleTester {
            private EveryOne() {
            }

            @Override
            public boolean test(final ServerCard card) {
                return true;
            }
        }

        final class Never implements VisibleTester {
            private Never() {
            }

            @Override
            public boolean test(final ServerCard card) {
                return false;
            }
        }

        final class OnlyOwner implements VisibleTester {
            private final Player player;

            private OnlyOwner(final Player player) {
                this.player = player;
            }

            public static OnlyOwner testFor(final Player player) {
                return new OnlyOwner(player);
            }

            @Override
            public boolean test(final ServerCard card) {
                return this.player.equals(card.owner);
            }
        }
    }
}
