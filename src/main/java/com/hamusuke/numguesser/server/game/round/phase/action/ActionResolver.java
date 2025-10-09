package com.hamusuke.numguesser.server.game.round.phase.action;

import com.google.common.collect.Maps;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.*;
import com.hamusuke.numguesser.server.game.round.phase.action.actions.*;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

public class ActionResolver {
    private static final Map<Class<? extends Packet<?>>, Function<? extends Packet<?>, ?>> PACKET2ACTION = Maps.newHashMap();

    static {
        register(CardSelectReq.class, p -> new AttackActions.Select(p.id()));
        register(AttackReq.class, p -> new AttackActions.DoAttack(p.id(), p.num()));
        register(CardForAttackSelectRsp.class, p -> new SelectCardForAttackAction(p.id()));
        register(TossRsp.class, p -> new TossAction(p.cardId()));
        register(ClientCommandReq.class, p -> switch (p.command()) {
            case CONTINUE_ATTACKING, STAY ->
                    new ContinueOrStayAction(p.command() == ClientCommandReq.Command.CONTINUE_ATTACKING);
            case LET_ALLY_TOSS, ATTACK_WITHOUT_TOSS ->
                    new SelectTossOrAttackAction(p.command() == ClientCommandReq.Command.LET_ALLY_TOSS);
            default -> null;
        });
    }

    private static <P extends Packet<?>> void register(final Class<P> clazz, final Function<P, ?> actionConverter) {
        PACKET2ACTION.put(clazz, actionConverter);
    }

    @Nullable
    public static <A> A resolve(final Packet<?> packet) {
        final Function func = PACKET2ACTION.get(packet.getClass());
        if (func == null) {
            return null;
        }

        return (A) func.apply(packet);
    }
}
