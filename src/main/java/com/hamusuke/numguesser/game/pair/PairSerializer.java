package com.hamusuke.numguesser.game.pair;

import com.hamusuke.numguesser.game.pair.PlayerPair.PairColor;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import it.unimi.dsi.fastutil.Pair;

import java.util.function.Function;

public class PairSerializer<P extends Player> {
    private final P player;
    private final PairColor color;

    private PairSerializer(P player, PairColor color) {
        this.player = player;
        this.color = color;
    }

    public static <P extends Player> PairSerializer<P> from(Pair<P, PairColor> pair) {
        return of(pair.left(), pair.right());
    }

    public static <P extends Player> PairSerializer<P> of(P player, PairColor color) {
        return new PairSerializer<>(player, color);
    }

    public static <P extends Player> PairSerializer<P> decode(IntelligentByteBuf buf, Function<Integer, P> idToPlayer) {
        return of(idToPlayer.apply(buf.readVarInt()), buf.readEnum(PairColor.class));
    }

    public Player getPlayer() {
        return this.player;
    }

    public PairColor getColor() {
        return this.color;
    }

    public void writeTo(IntelligentByteBuf buf) {
        buf.writeVarInt(this.player.getId());
        buf.writeEnum(this.color);
    }
}
