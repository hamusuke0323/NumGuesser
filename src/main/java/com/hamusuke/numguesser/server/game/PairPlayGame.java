package com.hamusuke.numguesser.server.game;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.game.pair.PlayerPair.PairColor;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.PairColorChangeReq;
import com.hamusuke.numguesser.server.game.event.events.GameMessageEvent;
import com.hamusuke.numguesser.server.game.event.events.PairColorChangeEvent;
import com.hamusuke.numguesser.server.game.event.events.PairMakingStartEvent;
import com.hamusuke.numguesser.server.game.pair.ServerPlayerPairRegistry;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.PairGameRound;
import com.hamusuke.numguesser.server.game.seating.PairPlaySeatingArranger;
import com.hamusuke.numguesser.server.game.seating.SeatingArranger;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.room.ServerRoom;

import java.util.Collections;
import java.util.List;

public class PairPlayGame extends NormalGame {
    private final ServerPlayerPairRegistry pairRegistry = new ServerPlayerPairRegistry();
    private boolean hasMadeTeam;

    public PairPlayGame(ServerRoom room, List<ServerPlayer> players) {
        super(room, players);
    }

    @Override
    protected SeatingArranger newSeatingArranger() {
        return new PairPlaySeatingArranger(this.pairRegistry);
    }

    @Override
    public void startGame() {
        if (!this.hasMadeTeam) {
            this.makePairRandomly();
            this.eventBus.post(new PairMakingStartEvent(this.pairRegistry));
            return;
        }

        super.startGame();
    }

    @Override
    public void onTossSelected(ServerPlayer selector) {
        this.getRound().onTossSelected(selector);
    }

    @Override
    public void onAttackSelected(ServerPlayer selector) {
        this.getRound().onAttackSelected(selector);
    }

    @Override
    public void onToss(ServerPlayer tosser, int cardId) {
        this.getRound().onToss(tosser, cardId);
    }

    public void onPairColorChange(PairColorChangeReq req) {
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

    public synchronized void onPairMakingDone() {
        if (this.hasMadeTeam) {
            return;
        }

        for (final var color : PairColor.values()) {
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

    @Override
    protected GameRound getFirstRound() {
        return new PairGameRound(this, this.players);
    }

    @Override
    protected PairGameRound getRound() {
        return (PairGameRound) super.getRound();
    }

    public ServerPlayerPairRegistry getPairRegistry() {
        return this.pairRegistry;
    }

    public void makePairRandomly() {
        var random = this.room.getOwner().getRandom();

        var copied = Lists.newArrayList(this.players);
        Collections.shuffle(copied, random);

        for (int i = 0; i < this.players.size(); i++) {
            final var pair = this.pairRegistry.get(i % 2 == 0 ? PairColor.BLUE : PairColor.RED);
            if (i < 2) {
                pair.left(copied.get(i));
            } else {
                pair.right(copied.get(i));
            }
        }
    }

    @Override
    public synchronized void leavePlayer(ServerPlayer player) {
        this.players.remove(player);
        this.eventBus.post(new GameMessageEvent("このゲームモードは少なくとも" + this.room.getGameMode().minPlayer + "人必要です"));
        this.room.abortGame();
    }
}
