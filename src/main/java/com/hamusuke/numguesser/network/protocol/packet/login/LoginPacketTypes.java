package com.hamusuke.numguesser.network.protocol.packet.login;

import com.hamusuke.numguesser.network.listener.client.login.ClientLoginPacketListener;
import com.hamusuke.numguesser.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.PacketDirection;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.PacketType;
import com.hamusuke.numguesser.network.protocol.packet.login.clientbound.*;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.*;

public class LoginPacketTypes {
    public static final PacketType<AliveRsp> ALIVE_RSP = createClientbound("alive_rsp");
    public static final PacketType<EnterNameReq> ENTER_NAME_REQ = createClientbound("enter_name_req");
    public static final PacketType<KeyExchangeRsp> KEY_EXCHANGE_RSP = createClientbound("key_exchange_rsp");
    public static final PacketType<LoginCompressionNotify> LOGIN_COMPRESSION = createClientbound("login_compression");
    public static final PacketType<LoginDisconnectNotify> LOGIN_DISCONNECT = createClientbound("login_disconnect");
    public static final PacketType<LoginSuccessNotify> LOGIN_SUCCESS = createClientbound("login_success");
    public static final PacketType<AliveReq> ALIVE_REQ = createServerbound("alive_req");
    public static final PacketType<EncryptionSetupReq> ENCRYPTION_SETUP = createServerbound("encryption_setup");
    public static final PacketType<EnterNameRsp> ENTER_NAME_RSP = createServerbound("enter_name_rsp");
    public static final PacketType<KeyExchangeReq> KEY_EXCHANGE_REQ = createServerbound("key_exchange_req");
    public static final PacketType<LobbyJoinedNotify> LOBBY_JOINED = createServerbound("lobby_joined");

    private static <T extends Packet<ServerLoginPacketListener>> PacketType<T> createServerbound(String id) {
        return new PacketType<>(PacketDirection.SERVERBOUND, id);
    }

    private static <T extends Packet<ClientLoginPacketListener>> PacketType<T> createClientbound(String id) {
        return new PacketType<>(PacketDirection.CLIENTBOUND, id);
    }
}
