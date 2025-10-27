package com.hamusuke.numguesser.server.game.round;

import com.hamusuke.numguesser.game.Game;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.server.game.GameDataRegistry;
import com.hamusuke.numguesser.server.game.ServerGenericGame;
import com.hamusuke.numguesser.server.game.card.ServerCard;
import com.hamusuke.numguesser.server.game.event.GameEventBus;
import com.hamusuke.numguesser.server.game.event.events.PlayerNewCardAddEvent;
import com.hamusuke.numguesser.server.game.round.phase.Actable;
import com.hamusuke.numguesser.server.game.round.phase.Cancellable;
import com.hamusuke.numguesser.server.game.round.phase.GamePhaseManager;
import com.hamusuke.numguesser.server.game.seating.SeatingArranger;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class GameRound {
    private static final Logger LOGGER = LogManager.getLogger();
    public final ServerGenericGame game;
    public final List<ServerPlayer> players;
    public final CardRegistry cardRegistry;
    public final ParentDeterminer parentDeterminer;
    public final SeatingArranger seatingArranger;
    public final GameEventBus eventBus;
    protected final Random random;
    protected final GamePhaseManager phaseManager;
    protected ServerPlayer curAttacker;
    @Nullable
    protected ServerPlayer winner;

    public GameRound(ServerGenericGame game, List<ServerPlayer> players, GamePhaseManager phaseManager) {
        this.game = game;
        this.eventBus = game.getEventBus();
        this.players = players;
        this.random = newRandom();
        this.cardRegistry = new CardRegistry(this.random);
        this.parentDeterminer = new ParentDeterminer();
        this.seatingArranger = game.getData(GameDataRegistry.SEATING_ARRANGER);
        this.phaseManager = phaseManager;
    }

    protected GameRound(final GameRound old) {
        this.game = old.game;
        this.eventBus = old.eventBus;
        this.players = old.players;
        this.random = newRandom();
        this.cardRegistry = new CardRegistry(this.random);
        this.parentDeterminer = old.parentDeterminer;
        this.parentDeterminer.next();
        this.seatingArranger = old.seatingArranger;
        this.phaseManager = old.phaseManager;
    }

    private static Random newRandom() {
        Random random;
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            random = new Random(Util.getMeasuringTimeNano());
        }

        return random;
    }

    public void startRound() {
        this.phaseManager.start(this);
    }

    public void endRound() {
        this.phaseManager.setEndForcibly(this);
    }

    public void prevPhase() {
        this.phaseManager.prev(this);
    }

    public void nextPhase() {
        this.phaseManager.next(this);
    }

    public void ownCard(ServerPlayer player, ServerCard card) {
        if (this.cardRegistry.own(player, card)) {
            int index = player.getDeck().addCard(card);
            this.eventBus.post(new PlayerNewCardAddEvent(player, index, card));
        }
    }

    public void onPlayerAction(final ServerPlayer actor, final Object data) {
        if (!(this.phaseManager.getCurrentPhase() instanceof Actable actable)) {
            return;
        }

        try {
            actable.onPlayerAction(this, actor, data);
        } catch (Throwable e) {
            LOGGER.warn("Player " + actor.getDisplayName() + " might send an invalid action", e);
        }
    }

    public void onCancelCommand(ServerPlayer canceller) {
        if (this.phaseManager.getCurrentPhase() instanceof Cancellable cancellable) {
            cancellable.onPlayerCancel(this, canceller);
        }
    }

    public boolean arePlayersDefeatedBy(@Nullable ServerPlayer player) {
        return this.players.stream()
                .filter(sp -> !sp.equals(player))
                .allMatch(ServerPlayer::isDefeated);
    }

    public void ready() {
        if (this.phaseManager.hasNext() || this.isLastRound()) {
            return;
        }

        if (this.players.stream().allMatch(Player::isReady)) {
            this.players.forEach(player -> player.setReady(false));
            this.game.startNextRound();
        }
    }

    public void nextAttacker() {
        int cur = this.seatingArranger.getSeatIndex(this.curAttacker);

        for (int i = 0; i < this.seatingArranger.size(); i++) {
            int nextIndex = (cur + 1 + i) % this.seatingArranger.size();
            var player = this.getPlayingPlayerById(this.seatingArranger.get(nextIndex));
            if (player == null || player.isDefeated()) {
                continue;
            }

            this.setCurAttacker(player);
            break;
        }
    }

    @Nullable
    protected ServerPlayer getPlayingPlayerById(int id) {
        return this.players.stream()
                .filter(player -> player.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void onPlayerLeft(ServerPlayer player) {
        this.phaseManager.getCurrentPhase().onPlayerLeft(this, player);
    }

    public boolean isLastRound() {
        return this.parentDeterminer.hasNoCandidates();
    }

    public int getGivenCardNumPerPlayer() {
        return switch (this.players.size()) {
            case 2 -> 4;
            case 3 -> 3;
            case 4 -> 2;
            default -> 0;
        };
    }

    public int getDefaultTipPointPerPlayer() {
        return switch (this.players.size()) {
            case 2 -> 400;
            case 3 -> 230;
            case 4 -> 200;
            default -> 0;
        };
    }

    public GameRound newRound() {
        return new GameRound(this);
    }

    public ServerPlayer getCurAttacker() {
        return this.curAttacker;
    }

    public void setCurAttacker(ServerPlayer curAttacker) {
        this.curAttacker = curAttacker;
        this.game.setSyncedData(Game.CURRENT_ATTACKER, curAttacker.getId());
    }

    @Nullable
    public ServerPlayer getWinner() {
        return this.winner;
    }

    public void setWinner(@Nullable ServerPlayer winner) {
        this.winner = winner;
    }
}
