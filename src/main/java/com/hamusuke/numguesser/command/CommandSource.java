package com.hamusuke.numguesser.command;

import com.hamusuke.numguesser.server.NumGuesserServer;
import com.hamusuke.numguesser.server.network.ServerPlayer;

import javax.annotation.Nullable;

public interface CommandSource {
    NumGuesserServer getServer();

    @Nullable
    ServerPlayer getSender();

    String getDisplayName();

    default void sendMessageToAll(String msg) {
        this.sendMessage(msg, true);
    }

    default void sendError(String msg) {
        this.sendCommandFeedback(msg, false);
    }

    void sendCommandFeedback(String msg, boolean all);

    void sendMessage(String msg, boolean all);
}
