package com.hamusuke.numguesser.network.protocol.packet.play.clientbound;

import com.google.common.collect.ImmutableMap;
import com.hamusuke.numguesser.game.pair.PlayerPair.PairColor;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.PlayPacketTypes;
import com.hamusuke.numguesser.server.network.ServerPlayer;
import com.hamusuke.numguesser.util.Util;

import java.util.Map;
import java.util.function.Function;

public record PairMakingStartNotify(Map<Integer, PairColor> pairMap) implements Packet<ClientPlayPacketListener> {
    public static final StreamCodec<IntelligentByteBuf, PairMakingStartNotify> STREAM_CODEC = Packet.codec(PairMakingStartNotify::write, PairMakingStartNotify::new);

    private PairMakingStartNotify(IntelligentByteBuf buf) {
        this(buf.readJustMap(IntelligentByteBuf::readVarInt, b -> b.readEnum(PairColor.class), ImmutableMap::copyOf));
    }

    public static PairMakingStartNotify from(Map<ServerPlayer, PairColor> pairMap) {
        return new PairMakingStartNotify(Util.transformToNewImmutableMapOnlyKeys(pairMap, Player::getId));
    }

    public <P extends Player> Map<P, PairColor> toPlayerPairMap(Function<Integer, P> idToPlayer) {
        return Util.transformToNewImmutableMapOnlyKeys(this.pairMap, idToPlayer);
    }

    private void write(IntelligentByteBuf buf) {
        buf.writeMap(this.pairMap, (e, b) -> {
            b.writeVarInt(e.getKey());
            b.writeEnum(e.getValue());
        });
    }

    @Override
    public PacketType<PairMakingStartNotify> type() {
        return PlayPacketTypes.PAIR_MAKING_START;
    }

    @Override
    public void handle(ClientPlayPacketListener listener) {
        listener.handlePairMakingStart(this);
    }
}
