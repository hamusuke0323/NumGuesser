package com.hamusuke.numguesser.network.protocol.packet.room;

import com.hamusuke.numguesser.network.listener.client.main.ClientRoomPacketListener;
import com.hamusuke.numguesser.network.listener.server.main.ServerRoomPacketListener;
import com.hamusuke.numguesser.network.protocol.PacketDirection;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.room.clientbound.StartGameNotify;
import com.hamusuke.numguesser.network.protocol.packet.room.serverbound.ClientStartedGameNotify;

public class RoomPacketTypes {
    public static final PacketType<StartGameNotify> START_GAME = createClientbound("start_game");
    public static final PacketType<ClientStartedGameNotify> CLIENT_STARTED_GAME = createServerbound("client_started_game");

    private static <T extends Packet<ServerRoomPacketListener>> PacketType<T> createServerbound(String id) {
        return new PacketType<>(PacketDirection.SERVERBOUND, id);
    }

    private static <T extends Packet<ClientRoomPacketListener>> PacketType<T> createClientbound(String id) {
        return new PacketType<>(PacketDirection.CLIENTBOUND, id);
    }
}
