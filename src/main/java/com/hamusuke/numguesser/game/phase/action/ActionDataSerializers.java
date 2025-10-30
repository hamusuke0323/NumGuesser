package com.hamusuke.numguesser.game.phase.action;

import com.google.common.collect.Maps;
import com.hamusuke.numguesser.game.phase.action.actions.*;
import com.hamusuke.numguesser.network.channel.IntelligentByteBuf;
import com.hamusuke.numguesser.network.codec.StreamCodec;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ActionDataSerializers {
    private static final int UNKNOWN = -1;
    private static final AtomicInteger ID = new AtomicInteger();
    private static final Supplier<Integer> NEXT_ID = ID::getAndIncrement;
    private static final Map<Integer, StreamCodec<IntelligentByteBuf, ?>> ID_TO_CODEC = Maps.newHashMap();
    private static final Map<Class<?>, Integer> CLASS_TO_ID = Maps.newHashMap();

    static {
        register(AttackActions.DoAttack.class, AttackActions.DoAttack.STREAM_CODEC);
        register(AttackActions.Select.class, AttackActions.Select.STREAM_CODEC);
        register(ContinueOrStayAction.class, ContinueOrStayAction.STREAM_CODEC);
        register(SelectCardForAttackAction.class, SelectCardForAttackAction.STREAM_CODEC);
        register(SelectTossOrAttackAction.class, SelectTossOrAttackAction.STREAM_CODEC);
        register(TossAction.class, TossAction.STREAM_CODEC);
        register(ButtonPressAction.class, ButtonPressAction.STREAM_CODEC);
        register(PairMakingActions.PairColorChange.class, PairMakingActions.PairColorChange.STREAM_CODEC);
        register(PairMakingActions.PairMakingDone.class, PairMakingActions.PairMakingDone.STREAM_CODEC);
    }

    public static <A> A readFrom(final IntelligentByteBuf buf) {
        final int id = buf.readVarInt();
        final var codec = (StreamCodec<IntelligentByteBuf, A>) ID_TO_CODEC.get(id);
        if (codec == null) {
            throw new DecoderException("unknown id: " + id);
        }

        return codec.decode(buf);
    }

    public static <A> void writeTo(final IntelligentByteBuf buf, final A data) {
        final int id = CLASS_TO_ID.getOrDefault(data.getClass(), UNKNOWN);
        if (id == UNKNOWN) {
            throw new EncoderException("unknown data type: " + data.getClass());
        }

        buf.writeVarInt(id);
        try {
            final var codec = (StreamCodec<IntelligentByteBuf, A>) ID_TO_CODEC.get(id);
            codec.encode(buf, data);
        } catch (Throwable e) {
            throw new EncoderException("failed to encode data: " + data, e);
        }
    }

    private static <A> void register(final Class<A> type, final StreamCodec<IntelligentByteBuf, A> codec) {
        final int id = NEXT_ID.get();
        ID_TO_CODEC.put(id, codec);
        CLASS_TO_ID.put(type, id);
    }
}
