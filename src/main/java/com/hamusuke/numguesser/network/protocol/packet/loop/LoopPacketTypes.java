package com.hamusuke.numguesser.network.protocol.packet.loop;

import com.hamusuke.numguesser.network.listener.client.ClientboundLoopPacketListener;
import com.hamusuke.numguesser.network.listener.server.ServerboundLoopPacketListener;
import com.hamusuke.numguesser.network.protocol.PacketDirection;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.PingReq;
import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.RTTChangeNotify;
import com.hamusuke.numguesser.network.protocol.packet.loop.serverbound.PongRsp;

public class LoopPacketTypes {
    public static final PacketType<PingReq> PING = createClientbound("ping");
    public static final PacketType<RTTChangeNotify> RTT_CHANGE = createClientbound("rtt_change");
    public static final PacketType<PongRsp> PONG = createServerbound("pong");

    private static <T extends Packet<ServerboundLoopPacketListener>> PacketType<T> createServerbound(String id) {
        return new PacketType<>(PacketDirection.SERVERBOUND, id);
    }

    private static <T extends Packet<ClientboundLoopPacketListener>> PacketType<T> createClientbound(String id) {
        return new PacketType<>(PacketDirection.CLIENTBOUND, id);
    }
}
