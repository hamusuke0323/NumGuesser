package com.hamusuke.numguesser.game.pair;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.hamusuke.numguesser.network.Player;

import java.awt.*;
import java.util.Map;

public abstract class PlayerPair<P extends Player> {
    private final PairColor color;
    private P left;
    private P right;

    public PlayerPair(PairColor color) {
        this.color = color;
    }

    public Map<P, PairColor> toMap() {
        Map<P, PairColor> map = Maps.newHashMap();
        if (this.left != null) {
            map.put(this.left, this.color);
        }
        if (this.right != null) {
            map.put(this.right, this.color);
        }

        return ImmutableMap.copyOf(map);
    }

    public PairColor getColor() {
        return this.color;
    }

    public P getBuddyOf(P player) {
        return this.left.equals(player) ? this.right : this.left;
    }

    public PlayerPair<P> left(P left) {
        this.left = left;
        this.left.setPairColor(this.color);
        return this;
    }

    public PlayerPair<P> right(P right) {
        this.right = right;
        this.right.setPairColor(this.color);
        return this;
    }

    public P left() {
        return this.left;
    }

    public P right() {
        return this.right;
    }

    public enum PairColor {
        BLUE(Color.BLUE),
        RED(Color.RED);

        public final Color color;

        PairColor(Color color) {
            this.color = color;
        }

        public PairColor opposite() {
            return this == BLUE ? RED : BLUE;
        }
    }
}
