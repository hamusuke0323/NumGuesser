package com.hamusuke.numguesser.network.protocol.packet.login;

import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.ProtocolInfo;
import com.hamusuke.numguesser.network.protocol.ProtocolInfoBuilder;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.*;

public class LoginProtocols {
    public static final ProtocolInfo<ServerLoginPacketListener> SERVERBOUND = ProtocolInfoBuilder
            .serverboundProtocol(Protocol.LOGIN, builder -> {
                builder.addPacket(LoginPacketTypes.ALIVE_REQ, AliveReq.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.ENCRYPTION_SETUP, EncryptionSetupReq.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.ENTER_NAME_RSP, EnterNameRsp.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.KEY_EXCHANGE_REQ, KeyExchangeReq.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.LOBBY_JOINED, LobbyJoinedNotify.STREAM_CODEC);
            });
    public static final ProtocolInfo<ClientLoginPacketListener> CLIENTBOUND = ProtocolInfoBuilder
            .clientboundProtocol(Protocol.LOGIN, builder -> {
                builder.addPacket(LoginPacketTypes.ALIVE_RSP, AliveRsp.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.ENTER_NAME_REQ, EnterNameReq.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.KEY_EXCHANGE_RSP, KeyExchangeRsp.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.LOGIN_COMPRESSION, LoginCompressionNotify.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.LOGIN_DISCONNECT, LoginDisconnectNotify.STREAM_CODEC)
                        .addPacket(LoginPacketTypes.LOGIN_SUCCESS, LoginSuccessNotify.STREAM_CODEC);
            });
}
