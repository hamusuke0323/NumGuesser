package com.hamusuke.numguesser.server;

import com.hamusuke.numguesser.logging.LogUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainServer {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws UnknownHostException {
        var s = InetAddress.getLocalHost().getHostAddress();
        int i = 16160;
        boolean noGui = false;
        for (var arg : args) {
            if (arg.contains(":")) {
                var kv = arg.split(":");
                switch (kv[0]) {
                    case "address":
                        s = kv[1];
                        break;
                    case "port":
                        i = Integer.parseInt(kv[1]);
                }
            } else if (arg.equals("nogui")) {
                noGui = true;
            } else if (arg.equals("debug")) {
                LogUtil.setLogLevel(Level.TRACE);
            }
        }

        var host = s;
        int port = i;
        final var server = NumGuesserServer.startServer(host, port, noGui);
        var thread = new Thread(() -> {
            server.stop(true);
            LogManager.shutdown();
        }, "Server Shutdown Thread");
        thread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Caught exception", e));
        Runtime.getRuntime().addShutdownHook(thread);
    }
}
