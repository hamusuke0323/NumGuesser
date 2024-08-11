package com.hamusuke.numguesser.util.thread;

public interface MessageListener<Msg> extends AutoCloseable {
    String getName();

    void sendMsg(Msg msg);

    @Override
    default void close() {
    }
}
