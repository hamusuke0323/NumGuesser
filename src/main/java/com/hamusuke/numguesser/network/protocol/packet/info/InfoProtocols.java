package com.hamusuke.numguesser.network.protocol.packet.info;

import com.hamusuke.numguesser.network.listener.client.info.ClientInfoPacketListener;
import com.hamusuke.numguesser.network.listener.server.info.ServerInfoPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.ProtocolInfo;
import com.hamusuke.numguesser.network.protocol.ProtocolInfoBuilder;
import com.hamusuke.numguesser.network.protocol.packet.info.clientbound.InfoHandshakeDoneNotify;
import com.hamusuke.numguesser.network.protocol.packet.info.clientbound.ServerInfoRsp;
import com.hamusuke.numguesser.network.protocol.packet.info.serverbound.ServerInfoReq;

public class InfoProtocols {
    public static final ProtocolInfo<ServerInfoPacketListener> SERVERBOUND = ProtocolInfoBuilder
            .serverboundProtocol(Protocol.INFO, builder -> {
                builder.addPacket(InfoPacketTypes.SERVER_INFO_REQ, ServerInfoReq.STREAM_CODEC);
            });
    public static final ProtocolInfo<ClientInfoPacketListener> CLIENTBOUND = ProtocolInfoBuilder
            .clientboundProtocol(Protocol.INFO, builder -> {
                builder.addPacket(InfoPacketTypes.INFO_HANDSHAKE_DONE, InfoHandshakeDoneNotify.STREAM_CODEC)
                        .addPacket(InfoPacketTypes.SERVER_INFO_RSP, ServerInfoRsp.STREAM_CODEC);
            });
}
