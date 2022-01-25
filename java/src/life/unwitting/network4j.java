package life.unwitting;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("unused")
public class network4j {
    public static final String Localhost = "127.0.0.1";

    public static boolean isHostConnectable(String host, int port) {
        boolean available = false;
        Socket socket = new Socket();
        try {
            socket.setSoTimeout(1000);
            socket.connect(new InetSocketAddress(host, port));
            available = true;
        } catch (Exception ignored) {

        } finally {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
        return available;
    }

    public static boolean isPortUsable(int port) {
        boolean usable = false;
        if (lib.of(port).ToPort().isJsonString()) {
            ServerSocket socket = null;
            try {
                socket = new ServerSocket();
                socket.bind(new InetSocketAddress(port));
                usable = true;
            } catch (Exception e) {
                log4j.err(e);
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (Exception ignored) {

                }
            }
        }
        return usable;
    }

    public static void findLocalServices() {
        network4j.findLocalServices(network4j.Localhost);
    }

    public static void findLocalServices(final String host) {
        if (lib.notNullOrEmpty(host)) {
            for (int port : new int[]{21, 22, 23, 25, 80, 110, 443, 1080, 1433}) {
                new Thread(new thread4j(port) {
                    @Override
                    public void run() {
                        try {
                            int port = (Integer) this.parameter;
                            if (network4j.isHostConnectable(host, port)) {
                                log4j.info(String.format("%s:%s is available.", host, port));
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }).start();
            }
        }
    }
}
