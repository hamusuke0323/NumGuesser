package com.hamusuke.numguesser.network.protocol.packet.disconnect;

import com.hamusuke.numguesser.network.listener.client.ClientboundDisconnectListener;
import com.hamusuke.numguesser.network.listener.server.ServerboundDisconnectListener;
import com.hamusuke.numguesser.network.protocol.PacketDirection;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.disconnect.clientbound.DisconnectNotify;
import com.hamusuke.numguesser.network.protocol.packet.disconnect.serverbound.DisconnectReq;

public class DisconnectPacketTypes {
    public static final PacketType<DisconnectNotify> DISCONNECT_NOTIFY = createClientbound("disconnect_notify");
    public static final PacketType<DisconnectReq> DISCONNECT_REQ = createServerbound("disconnect_req");

    private static <T extends Packet<ServerboundDisconnectListener>> PacketType<T> createServerbound(String id) {
        return new PacketType<>(PacketDirection.SERVERBOUND, id);
    }

    private static <T extends Packet<ClientboundDisconnectListener>> PacketType<T> createClientbound(String id) {
        return new PacketType<>(PacketDirection.CLIENTBOUND, id);
    }
}
