package com.hamusuke.numguesser.network.listener.server.handshake;

import com.hamusuke.numguesser.network.listener.server.ServerPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.serverbound.handshake.HandshakeReq;

public interface ServerHandshakePacketListener extends ServerPacketListener {
    void handleHandshake(HandshakeReq packet);
}
