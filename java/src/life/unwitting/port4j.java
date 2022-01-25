package life.unwitting;

import java.net.DatagramSocket;
import java.net.ServerSocket;

public class port4j extends lib<Integer> {
    public static final int MIN_PORT = 0;
    public static final int MAX_PORT = 65535;

    public port4j(Integer obj) {
        super(obj);
    }

    @Override
    public boolean isJsonString() {
        boolean b = false;
        if (super.isJsonString()) {
            Integer i = this.m_instance;
            b = i >= port4j.MIN_PORT && i <= port4j.MAX_PORT;
        }
        return b;
    }

    public boolean isUsing() {
        boolean inUse = true;
        if (this.isJsonString()) {
            ServerSocket tcp = null;
            DatagramSocket udp = null;
            try {
                tcp = new ServerSocket(this.m_instance);
                tcp.setReuseAddress(true);
                udp = new DatagramSocket(this.m_instance);
                udp.setReuseAddress(true);
                inUse = false;
            } catch (Exception ignored) {

            } finally {
                if (tcp != null) {
                    try {
                        tcp.close();
                    } catch (Exception ignored) {
                    }
                }
                if (udp != null) {
                    udp.close();
                }
            }
        }
        return inUse;
    }
}
