package com.hamusuke.numguesser.network.listener.server.info;

import com.hamusuke.numguesser.network.listener.server.ServerPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.info.ServerInfoReq;

public interface ServerInfoPacketListener extends ServerPacketListener {
    void handleInfoReq(ServerInfoReq packet);
}
