package com.hamusuke.numguesser.client;

public class TickCounter {
    private final float tickTime;
    private float tickDelta;
    private long prevTimeMillis;

    public TickCounter(float tps, long timeMillis) {
        this.tickTime = 1000.0F / tps;
        this.prevTimeMillis = timeMillis;
    }

    public int beginLoopTick(long timeMillis) {
        float lastDuration = (float) (timeMillis - this.prevTimeMillis) / this.tickTime;
        this.prevTimeMillis = timeMillis;
        this.tickDelta += lastDuration;
        int i = (int) this.tickDelta;
        this.tickDelta -= (float) i;
        return i;
    }
}
