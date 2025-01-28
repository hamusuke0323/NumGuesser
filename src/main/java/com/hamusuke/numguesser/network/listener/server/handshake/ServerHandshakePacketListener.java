package com.hamusuke.numguesser.network.listener.server.handshake;

import com.hamusuke.numguesser.network.listener.server.ServerboundPacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.handshake.serverbound.HandshakeReq;

public interface ServerHandshakePacketListener extends ServerboundPacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.HANDSHAKING;
    }

    void handleHandshake(HandshakeReq packet);
}
