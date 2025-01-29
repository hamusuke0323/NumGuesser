package com.hamusuke.numguesser.network.protocol.packet.login;

import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.ProtocolInfo;
import com.hamusuke.numguesser.network.protocol.ProtocolInfoBuilder;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.EncryptionSetupReq;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.EnterNameRsp;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.KeyExchangeReq;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.LobbyJoinedNotify;
import com.hamusuke.numguesser.network.protocol.packet.loop.LoopPacketTypes;
import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.PingReq;
import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.RTTChangeNotify;
import com.hamusuke.numguesser.network.protocol.packet.loop.serverbound.PongRsp;

public class LoginProtocols {
    public static final ProtocolInfo<ServerLoginPacketListener> SERVERBOUND = ProtocolInfoBuilder
            .serverboundProtocol(Protocol.LOGIN, builder -> {
                builder.addPacket(LoginPacketTypes.ENCRYPTION_SETUP, EncryptionSetupReq.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.ENTER_NAME_RSP, EnterNameRsp.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.KEY_EXCHANGE_REQ, KeyExchangeReq.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.LOBBY_JOINED, LobbyJoinedNotify.STREAM_CODEC);

                builder.addPacket(LoopPacketTypes.PONG, PongRsp.STREAM_CODEC);
            });
    public static final ProtocolInfo<ClientLoginPacketListener> CLIENTBOUND = ProtocolInfoBuilder
            .clientboundProtocol(Protocol.LOGIN, builder -> {
                builder.addPacket(LoginPacketTypes.ENTER_NAME_REQ, EnterNameReq.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.KEY_EXCHANGE_RSP, KeyExchangeRsp.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.LOGIN_COMPRESSION, LoginCompressionNotify.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.LOGIN_DISCONNECT, LoginDisconnectNotify.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.LOGIN_SUCCESS, LoginSuccessNotify.STREAM_CODEC);

                builder.addPacket(LoopPacketTypes.PING, PingReq.STREAM_CODEC)
                        .addPacket(LoopPacketTypes.RTT_CHANGE, RTTChangeNotify.STREAM_CODEC);
            });
}
