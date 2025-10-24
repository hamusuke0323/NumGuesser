package com.hamusuke.numguesser.network.protocol.packet.play;

import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.PacketDirection;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.*;

public class PlayPacketTypes {
    public static final PacketType<AttackRsp> ATTACK_RSP = createClientbound("attack_rsp");
    public static final PacketType<AttackSuccNotify> ATTACK_SUCC = createClientbound("attack_succ");
    public static final PacketType<CardForAttackSelectReq> CARD_FOR_ATTACK_SELECT_REQ = createClientbound("card_for_attack_select_req");
    public static final PacketType<CardOpenNotify> CARD_OPEN = createClientbound("card_open");
    public static final PacketType<CardsOpenNotify> CARDS_OPEN = createClientbound("cards_open");
    public static final PacketType<EndGameRoundNotify> END_GAME_ROUND = createClientbound("end_game_round");
    public static final PacketType<ExitGameSuccNotify> EXIT_GAME_SUCC = createClientbound("exit_game_succ");
    public static final PacketType<GamePhaseTransitionNotify> GAME_PHASE_TRANSITION = createClientbound("game_phase_transition");
    public static final PacketType<PairColorChangeNotify> PAIR_COLOR_CHANGE_NOTIFY = createClientbound("pair_color_change_notify");
    public static final PacketType<PairMakingStartNotify> PAIR_MAKING_START = createClientbound("pair_making_start");
    public static final PacketType<PlayerCardSelectionSyncNotify> PLAYER_CARD_SELECTION_SYNC = createClientbound("player_card_selection_sync");
    public static final PacketType<PlayerDeckSyncNotify> PLAYER_DECK_SYNC = createClientbound("player_deck_sync");
    public static final PacketType<PlayerNewCardAddNotify> PLAYER_NEW_CARD_ADD = createClientbound("player_new_card_add");
    public static final PacketType<PlayerNewDeckNotify> PLAYER_NEW_DECK = createClientbound("player_new_deck");
    public static final PacketType<PlayerStartAttackNotify> PLAYER_START_ATTACK = createClientbound("player_start_attack");
    public static final PacketType<RemotePlayerSelectCardForAttackNotify> REMOTE_PLAYER_SELECT_CARD_FOR_ATTACK = createClientbound("remote_player_select_card_for_attack");
    public static final PacketType<RemotePlayerSelectCardForTossNotify> REMOTE_PLAYER_SELECT_CARD_FOR_TOSS = createClientbound("remote_player_select_card_for_toss");
    public static final PacketType<RemotePlayerSelectTossOrAttackNotify> REMOTE_PLAYER_SELECT_TOSS_OR_ATTACK = createClientbound("remote_player_select_toss_or_attack");
    public static final PacketType<RemotePlayerStartAttackNotify> REMOTE_PLAYER_START_ATTACK = createClientbound("remote_player_start_attack");
    public static final PacketType<SeatingArrangementNotify> SEATING_ARRANGEMENT = createClientbound("seating_arrangement");
    public static final PacketType<StartGameRoundNotify> START_GAME_ROUND = createClientbound("start_game_round");
    public static final PacketType<TossNotify> TOSS_NOTIFY = createClientbound("toss_notify");
    public static final PacketType<TossOrAttackSelectionNotify> TOSS_OR_ATTACK_SELECTION = createClientbound("toss_or_attack_selection");
    public static final PacketType<TossReq> TOSS_REQ = createClientbound("toss_req");
    public static final PacketType<AttackReq> ATTACK_REQ = createServerbound("attack_req");
    public static final PacketType<CardForAttackSelectRsp> CARD_FOR_ATTACK_SELECT_RSP = createServerbound("card_for_attack_select_rsp");
    public static final PacketType<CardSelectReq> CARD_SELECT_REQ = createServerbound("card_select_req");
    public static final PacketType<ClientCommandReq> CLIENT_COMMAND = createServerbound("client_command");
    public static final PacketType<GameExitedNotify> GAME_EXITED = createServerbound("game_exited");
    public static final PacketType<PairColorChangeReq> PAIR_COLOR_CHANGE_REQ = createServerbound("pair_color_change_req");
    public static final PacketType<PairMakingDoneReq> PAIR_MAKING_DONE = createServerbound("pair_making_done");
    public static final PacketType<TossRsp> TOSS_RSP = createServerbound("toss_rsp");

    private static <T extends Packet<ServerPlayPacketListener>> PacketType<T> createServerbound(String id) {
        return new PacketType<>(PacketDirection.SERVERBOUND, id);
    }

    private static <T extends Packet<ClientPlayPacketListener>> PacketType<T> createClientbound(String id) {
        return new PacketType<>(PacketDirection.CLIENTBOUND, id);
    }
}
