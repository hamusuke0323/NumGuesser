package com.hamusuke.numguesser.server.game.round.phase.phases;

import com.hamusuke.numguesser.game.phase.phases.PairMakingPhase;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.phase.ServerGamePhase;

public class ServerPairMakingPhase extends PairMakingPhase implements ServerGamePhase {
    @Override
    public void onEnter(GameRound round) {
        ServerGamePhase.super.onEnter(round);

    }

    /*
    private void onPairColorChange(PairColorChangeReq req) {
        if (this.hasMadeTeam) {
            return;
        }

        var player = this.room.getPlayer(req.id());
        if (player == null) {
            return;
        }

        player.setPairColor(req.color());
        this.eventBus.post(new PairColorChangeEvent(player, req.color()));
    }

    private synchronized void onPairMakingDone() {
        if (this.hasMadeTeam) {
            return;
        }

        for (final var color : PlayerPair.PairColor.values()) {
            final var players = this.players.stream().filter(player -> player.getPairColor() == color).toList();
            if (players.size() != 2) {
                return;
            }

            final var pair = this.pairRegistry.get(color);
            pair.left(players.get(0));
            pair.right(players.get(1));
        }

        this.hasMadeTeam = true;
        this.startGame();
    }

    private void makePairRandomly() {
        var random = this.room.getOwner().getRandom();

        var copied = Lists.newArrayList(this.players);
        Collections.shuffle(copied, random);

        for (int i = 0; i < this.players.size(); i++) {
            final var pair = this.pairRegistry.get(i % 2 == 0 ? PlayerPair.PairColor.BLUE : PlayerPair.PairColor.RED);
            if (i < 2) {
                pair.left(copied.get(i));
            } else {
                pair.right(copied.get(i));
            }
        }
    }*/
}
