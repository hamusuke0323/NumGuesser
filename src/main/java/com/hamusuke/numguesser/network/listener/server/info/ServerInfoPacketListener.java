package com.hamusuke.numguesser.network.listener.server.info;

import com.hamusuke.numguesser.network.listener.server.ServerboundBasePacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.info.serverbound.ServerInfoReq;

public interface ServerInfoPacketListener extends ServerboundBasePacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.INFO;
    }

    void handleInfoReq(ServerInfoReq packet);
}
