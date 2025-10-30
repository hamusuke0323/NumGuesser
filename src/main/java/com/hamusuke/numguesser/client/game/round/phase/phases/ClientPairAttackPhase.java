package com.hamusuke.numguesser.client.game.round.phase.phases;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.game.ClientGame;
import com.hamusuke.numguesser.client.game.card.AbstractClientCard;

public class ClientPairAttackPhase extends ClientAttackPhase {
    @Override
    protected boolean isAttackable(final NumGuesser client, final ClientGame game, final AbstractClientCard card) {
        final var cardOwner = game.getCardOwner(card);
        return cardOwner != null && client.clientPlayer.getPairColor() != cardOwner.getPairColor();
    }
}
