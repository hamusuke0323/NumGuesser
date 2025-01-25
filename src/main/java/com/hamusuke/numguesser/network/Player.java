package com.hamusuke.numguesser.network;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Player {
    private static final AtomicInteger PLAYER_ID_INCREMENTER = new AtomicInteger();
    protected int id = PLAYER_ID_INCREMENTER.getAndIncrement();
    private int ping;
    protected final String name;
    protected boolean ready;
    protected int tipPoint;

    protected Player(String name) {
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getPing() {
        return this.ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void addTipPoint(int delta) {
        this.setTipPoint(this.getTipPoint() + delta);
    }

    public void subTipPoint(int delta) {
        this.addTipPoint(-delta);
    }

    public int getTipPoint() {
        return this.tipPoint;
    }

    public void setTipPoint(int tipPoint) {
        this.tipPoint = tipPoint;
    }

    public boolean isReady() {
        return this.ready;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return this.id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
