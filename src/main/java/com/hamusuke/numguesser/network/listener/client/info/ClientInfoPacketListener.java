package com.hamusuke.numguesser.network.listener.client.info;

import com.hamusuke.numguesser.network.listener.client.ClientboundBasePacketListener;
import com.hamusuke.numguesser.network.protocol.Protocol;
import com.hamusuke.numguesser.network.protocol.packet.info.clientbound.InfoHandshakeDoneNotify;
import com.hamusuke.numguesser.network.protocol.packet.info.clientbound.ServerInfoRsp;

public interface ClientInfoPacketListener extends ClientboundBasePacketListener {
    @Override
    default Protocol protocol() {
        return Protocol.INFO;
    }

    void handleInfoRsp(ServerInfoRsp packet);

    void handleHandshakeDone(InfoHandshakeDoneNotify packet);
}
