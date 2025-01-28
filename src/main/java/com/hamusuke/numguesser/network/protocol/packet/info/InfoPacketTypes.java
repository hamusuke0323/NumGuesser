package com.hamusuke.numguesser.network.protocol.packet.info;

import com.hamusuke.numguesser.network.listener.client.info.ClientInfoPacketListener;
import com.hamusuke.numguesser.network.listener.server.info.ServerInfoPacketListener;
import com.hamusuke.numguesser.network.protocol.PacketDirection;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.info.clientbound.InfoHandshakeDoneNotify;
import com.hamusuke.numguesser.network.protocol.packet.info.clientbound.ServerInfoRsp;
import com.hamusuke.numguesser.network.protocol.packet.info.serverbound.ServerInfoReq;

public class InfoPacketTypes {
    public static final PacketType<InfoHandshakeDoneNotify> INFO_HANDSHAKE_DONE = createClientbound("info_handshake_done");
    public static final PacketType<ServerInfoRsp> SERVER_INFO_RSP = createClientbound("server_info_rsp");
    public static final PacketType<ServerInfoReq> SERVER_INFO_REQ = createServerbound("server_info_req");

    private static <T extends Packet<ServerInfoPacketListener>> PacketType<T> createServerbound(String id) {
        return new PacketType<>(PacketDirection.SERVERBOUND, id);
    }

    private static <T extends Packet<ClientInfoPacketListener>> PacketType<T> createClientbound(String id) {
        return new PacketType<>(PacketDirection.CLIENTBOUND, id);
    }
}
