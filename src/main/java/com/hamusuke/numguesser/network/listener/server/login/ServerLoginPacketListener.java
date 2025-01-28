package com.hamusuke.numguesser.network.listener.server.login;

import com.hamusuke.numguesser.network.listener.server.ServerboundPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.login.serverbound.*;

public interface ServerLoginPacketListener extends ServerboundPacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.LOGIN;
    }

    void handleKeyEx(KeyExchangeReq packet);

    void handleEncryption(EncryptionSetupReq packet);

    void handlePing(AliveReq packet);

    void handleEnterName(EnterNameRsp packet);

    void handleLobbyJoined(LobbyJoinedNotify packet);
}
