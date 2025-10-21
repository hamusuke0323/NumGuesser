package com.hamusuke.numguesser.util;

import com.google.common.collect.Lists;
import com.hamusuke.numguesser.network.protocol.packet.Packet;
import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.PingReq;
import com.hamusuke.numguesser.network.protocol.packet.loop.clientbound.RTTChangeNotify;
import com.hamusuke.numguesser.network.protocol.packet.loop.serverbound.PongRsp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class PacketUtil {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final List<String> LOOP_PACKETS = Lists.newArrayList(
            PongRsp.class.getSimpleName(),
            PingReq.class.getSimpleName(),
            RTTChangeNotify.class.getSimpleName()
    );

    public static boolean isPacketTrash(Packet<?> packet) {
        return LOOP_PACKETS.contains(packet.getClass().getSimpleName());
    }

    public static String getPacketDetails(Packet<?> packet, String byteStr) {
        var buf = new StringBuilder(packet.getClass().getSimpleName() + byteStr).append('\n');

        Arrays.asList(packet.getClass().getDeclaredFields()).forEach(field -> {
            try {
                field.setAccessible(true);
                buf.append(field.getName()).append(" = ");
                var obj = field.get(packet);
                if (obj instanceof String) {
                    buf.append(String.format("\"%s\"", obj));
                } else {
                    buf.append(obj);
                }

                buf.append(";\n");
            } catch (Exception e) {
                LOGGER.warn("Failed to access the field", e);
            }
        });

        return buf.toString();
    }

    public static String convertBytes(long bytes) {
        if (bytes < 0) {
            return convertBytes(Long.MAX_VALUE);
        }

        var curSize = Size.B;
        var remainBytes = (double) bytes;

        while ((remainBytes / 1024.0D) >= 1.0D && curSize.next() != null) {
            curSize = curSize.next();
            remainBytes /= 1024.0D;
        }

        return "%.1f %s".formatted(remainBytes, curSize);
    }

    private enum Size {
        B,
        KB,
        MB,
        GB,
        TB,
        PB,
        EB;

        @Nullable
        private Size next() {
            var next = this.ordinal() + 1;
            var v = values();
            return this == EB ? null : v[next];
        }
    }
}
