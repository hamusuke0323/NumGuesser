package com.hamusuke.numguesser.network.listener.client.info;

import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.info.InfoHandshakeDoneNotify;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.info.ServerInfoRsp;

public interface ClientInfoPacketListener extends PacketListener {
    void handleInfoRsp(ServerInfoRsp packet);

    void handleHandshakeDone(InfoHandshakeDoneNotify packet);
}
