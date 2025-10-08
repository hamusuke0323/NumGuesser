package com.hamusuke.numguesser.server.game.mode;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hamusuke.numguesser.game.pair.PlayerPair.PairColor;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.ChatNotify;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.PairColorChangeNotify;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.PairMakingStartNotify;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.PairColorChangeReq;
import com.hamusuke.numguesser.server.game.pair.ServerPlayerPair;
import com.hamusuke.numguesser.server.game.round.GameRound;
import com.hamusuke.numguesser.server.game.round.PairGameRound;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.server.room.ServerRoom;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PairPlayGameMode extends NormalGameMode {
    private final ServerPlayerPair bluePair = new ServerPlayerPair(PairColor.BLUE);
    private final ServerPlayerPair redPair = new ServerPlayerPair(PairColor.RED);
    private boolean hasMadeTeam;

    public PairPlayGameMode(ServerRoom room, List<ServerPlayer> players) {
        super(room, players);
    }

    @Override
    public void startGame() {
        if (!this.hasMadeTeam) {
            this.makePairRandomly();
            this.sendPacketToAllInGame(PairMakingStartNotify.from(this.toPairMap()));
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
        this.sendPacketToAllInGame(new PairColorChangeNotify(req.id(), req.color()));
    }

    public synchronized void onPairMakingDone() {
        if (this.hasMadeTeam) {
            return;
        }

        this.hasMadeTeam = true;
        var bluePlayers = this.players.stream().filter(player -> player.getPairColor() == PairColor.BLUE).toList();
        var redPlayers = this.players.stream().filter(player -> player.getPairColor() == PairColor.RED).toList();

        if (bluePlayers.size() != 2 || redPlayers.size() != 2) {
            this.hasMadeTeam = false;
            return;
        }

        // Make pairs
        this.bluePair.left(bluePlayers.get(0));
        this.bluePair.right(bluePlayers.get(1));
        this.redPair.left(redPlayers.get(0));
        this.redPair.right(redPlayers.get(1));

        this.startGame();
    }

    @Override
    protected GameRound getFirstRound() {
        return new PairGameRound(this, this.players, null);
    }

    @Override
    protected PairGameRound getRound() {
        return (PairGameRound) super.getRound();
    }

    public ServerPlayerPair getBluePair() {
        return this.bluePair;
    }

    public ServerPlayerPair getRedPair() {
        return this.redPair;
    }

    private Map<ServerPlayer, PairColor> toPairMap() {
        Map<ServerPlayer, PairColor> pairMap = Maps.newHashMap();
        pairMap.putAll(this.bluePair.toMap());
        pairMap.putAll(this.redPair.toMap());
        return ImmutableMap.copyOf(pairMap);
    }

    public void makePairRandomly() {
        var random = this.room.getOwner().getRandom();

        var copied = Lists.newArrayList(this.players);
        Collections.shuffle(copied, random);

        for (int i = 0; i < this.players.size(); i++) {
            var pair = i % 2 == 0 ? this.bluePair : this.redPair;
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
        this.sendPacketToAllInGame(new ChatNotify("このゲームモードは少なくとも" + this.room.getGameMode().minPlayer + "人必要です"));
        this.room.abortGame();
    }
}
