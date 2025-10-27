package com.hamusuke.numguesser.server.game.round.phase.action;

import com.google.common.collect.Maps;
import com.hamusuke.numguesser.game.phase.action.actions.*;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.*;
import com.hamusuke.numguesser.server.game.round.phase.Actable;
import com.hamusuke.numguesser.server.game.round.phase.phases.*;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@Deprecated(forRemoval = true)
public class ActionResolver {
    private static final Map<Class<? extends Packet<?>>, ActionContext<?, ?>> PACKET2ACTION = Maps.newHashMap();

    static {
        register(CardSelectReq.class, phase -> ServerAttackPhase.class.isAssignableFrom(phase.getClass()), p -> new AttackActions.Select(p.id()));
        register(AttackReq.class, phase -> ServerAttackPhase.class.isAssignableFrom(phase.getClass()), p -> new AttackActions.DoAttack(p.id(), p.num()));
        register(CardForAttackSelectRsp.class, Set.of(ServerSelectCardForAttackPhase.class), p -> new SelectCardForAttackAction(p.id()));
        register(TossRsp.class, Set.of(ServerTossPhase.class), p -> new TossAction(p.cardId()));
        register(ClientCommandReq.class, Set.of(ServerContinueOrStayPhase.class, ServerSelectTossOrAttackPhase.class), p -> switch (p.command()) {
            case CONTINUE_ATTACKING, STAY ->
                    new ContinueOrStayAction(p.command() == ClientCommandReq.Command.CONTINUE_ATTACKING);
            case LET_ALLY_TOSS, ATTACK_WITHOUT_TOSS ->
                    new SelectTossOrAttackAction(p.command() == ClientCommandReq.Command.LET_ALLY_TOSS);
            default -> null;
        });
    }

    private static <P extends Packet<?>, A> void register(final Class<P> clazz, final Set<Class<? extends Actable<? extends A>>> allowList, final Function<P, A> actionConverter) {
        register(clazz, phase -> allowList.contains(phase.getClass()), actionConverter);
    }

    private static <P extends Packet<?>, A> void register(final Class<P> clazz, final Predicate<Actable<A>> canAct, final Function<P, A> actionConverter) {
        PACKET2ACTION.put(clazz, new ActionContext<>(canAct, actionConverter));
    }

    public static boolean canActWith(final Packet<?> packet, final Actable<?> phase) {
        final ActionContext actionContext = PACKET2ACTION.get(packet.getClass());
        if (actionContext == null) {
            return false;
        }

        return actionContext.canAct.test(phase);
    }

    @Nullable
    public static <A> A resolve(final Packet<?> packet) {
        final ActionContext ctx = PACKET2ACTION.get(packet.getClass());
        if (ctx == null) {
            return null;
        }

        return (A) ctx.actionConverter.apply(packet);
    }

    private record ActionContext<P extends Packet<?>, A>(Predicate<Actable<A>> canAct,
                                                         Function<P, A> actionConverter) {
    }
}
