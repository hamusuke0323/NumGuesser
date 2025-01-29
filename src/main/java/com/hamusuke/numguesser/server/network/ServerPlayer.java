package com.hamusuke.numguesser.server.network;

import com.hamusuke.numguesser.command.CommandSource;
import com.hamusuke.numguesser.network.PacketSendListener;
import com.hamusuke.numguesser.network.Player;
import com.hamusuke.numguesser.network.listener.server.ServerboundPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.ChatNotify;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.PlayerReadySyncNotify;
import com.hamusuke.numguesser.network.protocol.packet.common.clientbound.PlayerTipPointSyncNotify;
import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.RTTChangeNotify;
import com.hamusuke.numguesser.network.protocol.packet.play.clientbound.PlayerNewDeckNotify;
import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.game.ServerPlayerDeck;
import com.hamusuke.numguesser.server.room.ServerRoom;

import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.util.Random;

public class ServerPlayer extends Player implements CommandSource {
    public final NumGuesserServer server;
    public ServerboundPacketListener connection;
    private boolean isAuthorized;
    @Nullable
    public ServerRoom curRoom;
    private final Random random = new SecureRandom();
    private ServerPlayerDeck deck;
    private boolean isDefeated;

    public ServerPlayer(String name, NumGuesserServer server) {
        super(name);
        this.server = server;
    }

    public void makeNewDeck() {
        this.deck = new ServerPlayerDeck(this);
        this.sendPacket(PlayerNewDeckNotify.INSTANCE);
        this.setIsDefeated(false);
    }

    public ServerPlayerDeck getDeck() {
        return this.deck;
    }

    public Random getRandom() {
        return this.random;
    }

    public boolean isDefeated() {
        return this.isDefeated;
    }

    public void setIsDefeated(boolean isDefeated) {
        this.isDefeated = isDefeated;
    }

    @Override
    public void setPing(int ping) {
        super.setPing(ping);

        if (this.curRoom != null) {
            this.curRoom.sendPacketToAllInRoom(new RTTChangeNotify(this.getId(), ping));
        }
    }

    @Override
    public void setTipPoint(int tipPoint) {
        super.setTipPoint(tipPoint);

        if (this.curRoom != null && this.curRoom.getGame() != null) {
            this.curRoom.getGame().getPlayingPlayers()
                    .forEach(p -> p.sendPacket(new PlayerTipPointSyncNotify(this.getId(), this.getTipPoint())));
        }
    }

    @Override
    public void setReady(boolean ready) {
        super.setReady(ready);

        if (this.curRoom != null) {
            this.curRoom.sendPacketToAllInRoom(new PlayerReadySyncNotify(this.getId(), ready));
        }
    }

    public boolean isAuthorized() {
        return this.isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        this.isAuthorized = authorized;
    }

    public void sendPacket(Packet<?> packet) {
        this.sendPacket(packet, null);
    }

    public void sendPacket(Packet<?> packet, PacketSendListener callback) {
        this.connection.getConnection().sendPacket(packet, callback);
    }

    @Override
    public ServerPlayer getSender() {
        return this;
    }

    @Override
    public NumGuesserServer getServer() {
        return this.server;
    }

    @Override
    public void sendMessage(String msg, boolean all) {
        this.sendPacket(new ChatNotify(String.format("<%s> %s", this.getDisplayName(), msg)));

        if (all && this.curRoom != null) {
            this.curRoom.sendPacketToOthersInRoom(this, new ChatNotify(String.format("<%s> %s", this.getDisplayName(), msg)));
        }
    }

    @Override
    public void sendCommandFeedback(String msg, boolean all) {
        this.sendPacket(new ChatNotify(msg));

        if (all && this.curRoom != null) {
            this.curRoom.sendPacketToOthersInRoom(this, new ChatNotify(String.format("[%s]: %s", this.getDisplayName(), msg)));
        }
    }

    @Override
    public String getDisplayName() {
        return this.getName();
    }
}
