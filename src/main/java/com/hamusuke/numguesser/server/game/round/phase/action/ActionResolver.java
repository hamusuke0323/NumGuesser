package com.hamusuke.numguesser.server.game.round.phase.action;

import com.google.common.collect.Maps;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.*;
import com.hamusuke.numguesser.server.game.round.phase.ActableGamePhase;
import com.hamusuke.numguesser.server.game.round.phase.action.actions.*;
import com.hamusuke.numguesser.server.game.round.phase.phases.AttackPhase;
import com.hamusuke.numguesser.server.game.round.phase.phases.ContinueOrStayPhase;
import com.hamusuke.numguesser.server.game.round.phase.phases.SelectCardForAttackPhase;
import com.hamusuke.numguesser.server.game.round.phase.phases.pair.SelectTossOrAttackPhase;
import com.hamusuke.numguesser.server.game.round.phase.phases.pair.TossPhase;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class ActionResolver {
    private static final Map<Class<? extends Packet<?>>, ActionContext<?, ?>> PACKET2ACTION = Maps.newHashMap();

    static {
        register(CardSelectReq.class, phase -> AttackPhase.class.isAssignableFrom(phase.getClass()), p -> new AttackActions.Select(p.id()));
        register(AttackReq.class, phase -> AttackPhase.class.isAssignableFrom(phase.getClass()), p -> new AttackActions.DoAttack(p.id(), p.num()));
        register(CardForAttackSelectRsp.class, Set.of(SelectCardForAttackPhase.class), p -> new SelectCardForAttackAction(p.id()));
        register(TossRsp.class, Set.of(TossPhase.class), p -> new TossAction(p.cardId()));
        register(ClientCommandReq.class, Set.of(ContinueOrStayPhase.class, SelectTossOrAttackPhase.class), p -> switch (p.command()) {
            case CONTINUE_ATTACKING, STAY ->
                    new ContinueOrStayAction(p.command() == ClientCommandReq.Command.CONTINUE_ATTACKING);
            case LET_ALLY_TOSS, ATTACK_WITHOUT_TOSS ->
                    new SelectTossOrAttackAction(p.command() == ClientCommandReq.Command.LET_ALLY_TOSS);
            default -> null;
        });
    }

    private static <P extends Packet<?>, A> void register(final Class<P> clazz, final Set<Class<? extends ActableGamePhase<? extends A, ?>>> allowList, final Function<P, A> actionConverter) {
        register(clazz, phase -> allowList.contains(phase.getClass()), actionConverter);
    }

    private static <P extends Packet<?>, A> void register(final Class<P> clazz, final Predicate<ActableGamePhase<A, ?>> canAct, final Function<P, A> actionConverter) {
        PACKET2ACTION.put(clazz, new ActionContext<>(canAct, actionConverter));
    }

    public static boolean canActWith(final Packet<?> packet, final ActableGamePhase<?, ?> phase) {
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

    private record ActionContext<P extends Packet<?>, A>(Predicate<ActableGamePhase<A, ?>> canAct,
                                                         Function<P, A> actionConverter) {
    }
}
