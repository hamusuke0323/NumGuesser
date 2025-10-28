package com.hamusuke.numguesser.game.phase.phases;

import com.hamusuke.numguesser.game.card.CardSerializer;
import com.hamusuke.numguesser.game.phase.GamePhase;
import com.hamusuke.numguesser.game.phase.PhaseType;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;

public abstract class AttackPhase implements GamePhase {
    @Override
    public PhaseType type() {
        return PhaseType.ATTACK;
    }

    public record SyncedAttackData(int attackerId, CardSerializer serializer) {
        public static final SyncedAttackData DUMMY = new SyncedAttackData(-1, CardSerializer.DUMMY);
        public static final StreamCodec<IntelligentByteBuf, SyncedAttackData> STREAM_CODEC = CardSerializer.STREAM_CODEC.xmap(SyncedAttackData::new, SyncedAttackData::serializer);

        private SyncedAttackData(final CardSerializer serializer) {
            this(-1, serializer);
        }

        public boolean isOwner(final Player player) {
            return this.attackerId == player.getId();
        }
    }
}
