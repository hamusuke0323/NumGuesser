package com.hamusuke.numguesser.network.protocol.packet.play;

import com.hamusuke.numguesser.network.listener.client.main.ClientPlayPacketListener;
import com.hamusuke.numguesser.network.listener.server.main.ServerPlayPacketListener;
import com.hamusuke.numguesser.network.protocol.PacketDirection;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.ClientActionReq;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.ClientCommandReq;
import com.hamusuke.numguesser.network.protocol.packet.play.serverbound.GameExitedNotify;

public class PlayPacketTypes {
    public static final PacketType<CardOpenNotify> CARD_OPEN = createClientbound("card_open");
    public static final PacketType<CardsOpenNotify> CARDS_OPEN = createClientbound("cards_open");
    public static final PacketType<ExitGameSuccNotify> EXIT_GAME_SUCC = createClientbound("exit_game_succ");
    public static final PacketType<GameDataSyncNotify> GAME_DATA_SYNC = createClientbound("game_data_sync");
    public static final PacketType<GamePhaseTransitionNotify> GAME_PHASE_TRANSITION = createClientbound("game_phase_transition");
    public static final PacketType<PairColorChangeNotify> PAIR_COLOR_CHANGE_NOTIFY = createClientbound("pair_color_change_notify");
    public static final PacketType<PlayerCardSelectionSyncNotify> PLAYER_CARD_SELECTION_SYNC = createClientbound("player_card_selection_sync");
    public static final PacketType<PlayerDeckSyncNotify> PLAYER_DECK_SYNC = createClientbound("player_deck_sync");
    public static final PacketType<PlayerNewCardAddNotify> PLAYER_NEW_CARD_ADD = createClientbound("player_new_card_add");
    public static final PacketType<PlayerNewDeckNotify> PLAYER_NEW_DECK = createClientbound("player_new_deck");
    public static final PacketType<StartGameRoundNotify> START_GAME_ROUND = createClientbound("start_game_round");
    public static final PacketType<TossNotify> TOSS_NOTIFY = createClientbound("toss_notify");
    public static final PacketType<ClientActionReq> CLIENT_ACTION = createServerbound("client_action");
    public static final PacketType<ClientCommandReq> CLIENT_COMMAND = createServerbound("client_command");
    public static final PacketType<GameExitedNotify> GAME_EXITED = createServerbound("game_exited");

    private static <T extends Packet<ServerPlayPacketListener>> PacketType<T> createServerbound(String id) {
        return new PacketType<>(PacketDirection.SERVERBOUND, id);
    }

    private static <T extends Packet<ClientPlayPacketListener>> PacketType<T> createClientbound(String id) {
        return new PacketType<>(PacketDirection.CLIENTBOUND, id);
    }
}
