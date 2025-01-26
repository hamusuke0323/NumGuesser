package com.hamusuke.numguesser.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.hamusuke.numguesser.network.listener.PacketListener;
import com.hamusuke.numguesser.network.listener.server.lobby.ServerLobbyPacketListener;
import com.hamusuke.numguesser.network.listener.server.login.ServerLoginPacketListener;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.common.DisconnectNotify;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.lobby.LobbyDisconnectNotify;
import com.hamusuke.numguesser.network.protocol.packet.clientbound.login.LoginDisconnectNotify;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.*;

public class Util {
    public static final LongSupplier nanoTimeSupplier = System::nanoTime;

    public static <K, K1, V, V1> Map<K1, V1> transformToNewMap(Map<K, V> from, Function<K, K1> keyTransformer, Function<V, V1> valueTransformer) {
        Map<K1, V1> newMap = Maps.newHashMapWithExpectedSize(from.size());
        for (var entry : from.entrySet()) {
            var key = keyTransformer.apply(entry.getKey());
            if (key == null) {
                continue;
            }

            newMap.put(keyTransformer.apply(entry.getKey()), valueTransformer.apply(entry.getValue()));
        }

        return newMap;
    }

    public static <K, K1, V> Map<K1, V> transformToNewMapOnlyKeys(Map<K, V> from, Function<K, K1> keyTransformer) {
        return transformToNewMap(from, keyTransformer, Function.identity());
    }

    public static <K, V, V1> Map<K, V1> transformToNewMapOnlyValues(Map<K, V> from, Function<V, V1> valueTransformer) {
        return Maps.transformValues(from, valueTransformer::apply);
    }

    public static <K, K1, V, V1> Map<K1, V1> transformToNewImmutableMap(Map<K, V> from, Function<K, K1> keyTransformer, Function<V, V1> valueTransformer) {
        return ImmutableMap.copyOf(transformToNewMap(from, keyTransformer, valueTransformer));
    }

    public static <K, K1, V> Map<K1, V> transformToNewImmutableMapOnlyKeys(Map<K, V> from, Function<K, K1> keyTransformer) {
        return ImmutableMap.copyOf(transformToNewMapOnlyKeys(from, keyTransformer));
    }

    public static <K, V, V1> Map<K, V1> transformToNewImmutableMapOnlyValues(Map<K, V> from, Function<V, V1> valueTransformer) {
        return ImmutableMap.copyOf(transformToNewMapOnlyValues(from, valueTransformer));
    }

    public static Packet<?> toDisconnectPacket(PacketListener listener, String msg) {
        if (listener instanceof ServerLoginPacketListener) {
            return new LoginDisconnectNotify(msg);
        } else if (listener instanceof ServerLobbyPacketListener) {
            return new LobbyDisconnectNotify(msg);
        } else {
            return new DisconnectNotify(msg);
        }
    }

    public static String toHTML(String s) {
        return "<html>" + s.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br/>") + "</html>";
    }

    public static String numberOnly(String s) {
        return filterBy(s, value -> '0' <= value && value <= '9');
    }

    public static String filterBy(String s, IntPredicate intPredicate) {
        var buf = new StringBuilder();
        s.chars().filter(intPredicate).forEach(value -> buf.append((char) value));
        return buf.toString();
    }

    public static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T makeAndAccess(T t, Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }

    public static long getMeasuringTimeMs() {
        return getMeasuringTimeNano() / 1000000L;
    }

    public static long getMeasuringTimeNano() {
        return nanoTimeSupplier.getAsLong();
    }

    public static <E> E chooseRandom(List<E> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
}
