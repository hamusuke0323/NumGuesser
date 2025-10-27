package com.hamusuke.numguesser.game.data;

public interface DataListener {
    <V> void onDataChanged(final GameDataSyncer.Entry<V> data);
}
