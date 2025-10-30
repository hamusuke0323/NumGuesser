package com.hamusuke.numguesser.game.phase.action.actions;

import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import com.hamusuke.numguesser.network.codec.StreamCodecs;

public sealed abstract class AttackActions extends CardIdAction permits AttackActions.DoAttack, AttackActions.Select {
    protected AttackActions(final int cardId) {
        super(cardId);
    }

    public static final class Select extends AttackActions {
        public static final StreamCodec<IntelligentByteBuf, Select> STREAM_CODEC = StreamCodecs.VAR_INT.xmap(Select::new, Select::cardId);

        public Select(final int cardId) {
            super(cardId);
        }
    }

    public static final class DoAttack extends AttackActions {
        public static final StreamCodec<IntelligentByteBuf, DoAttack> STREAM_CODEC = StreamCodec.ofMember(DoAttack::write, DoAttack::new);
        private final int numExpected;

        public DoAttack(final int cardId, final int numExpected) {
            super(cardId);
            this.numExpected = numExpected;
        }

        private DoAttack(final IntelligentByteBuf buf) {
            this(buf.readVarInt(), buf.readVarInt());
        }

        private void write(final IntelligentByteBuf buf) {
            buf.writeVarInt(this.cardId);
            buf.writeVarInt(this.numExpected);
        }

        public int numExpected() {
            return this.numExpected;
        }
    }
}
