package com.hamusuke.numguesser.game.phase.action.actions;

import com.hamusuke.numguesser.game.pair.PlayerPair;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;

public abstract sealed class PairMakingActions permits PairMakingActions.PairColorChange, PairMakingActions.PairMakingDone {
    public static final class PairColorChange extends PairMakingActions {
        public static final StreamCodec<IntelligentByteBuf, PairColorChange> STREAM_CODEC = StreamCodec.ofMember(PairColorChange::write, PairColorChange::new);
        private final int playerId;
        private final PlayerPair.PairColor color;

        public PairColorChange(final int playerId, final PlayerPair.PairColor color) {
            this.playerId = playerId;
            this.color = color;
        }

        private PairColorChange(final IntelligentByteBuf buf) {
            this(buf.readVarInt(), buf.readEnum(PlayerPair.PairColor.class));
        }

        private void write(final IntelligentByteBuf buf) {
            buf.writeVarInt(this.playerId);
            buf.writeEnum(this.color);
        }

        public int getPlayerId() {
            return this.playerId;
        }

        public PlayerPair.PairColor getColor() {
            return this.color;
        }
    }

    public static final class PairMakingDone extends PairMakingActions {
        public static final PairMakingDone INSTANCE = new PairMakingDone();
        public static final StreamCodec<IntelligentByteBuf, PairMakingDone> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        private PairMakingDone() {
        }
    }
}
