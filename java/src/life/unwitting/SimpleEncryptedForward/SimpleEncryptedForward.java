package life.unwitting.SimpleEncryptedForward;

import life.unwitting.log4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class SimpleEncryptedForward implements Runnable {
    private int localPort = 7626;
    private String remoteHostAddr = "0.0.0.0";
    private int remoteHostPort = 0;
    private static final int TIMEOUT = 30;
    private static final HashMap<Socket, Date> clientList = new HashMap<Socket, Date>();

    public SimpleEncryptedForward() throws IOException {
        run();
    }

    public SimpleEncryptedForward(int localPort, String remoteHostAddr, int remoteHostPort) throws IOException {
        this.localPort = localPort;
        this.remoteHostAddr = remoteHostAddr;
        this.remoteHostPort = remoteHostPort;
        run();
    }

    public void run() {
        final SimpleEncryptedForward original = this;
        new Thread() {
            @Override
            public void run() {
                ServerSocket server;
                try {
                    server = new ServerSocket(original.localPort);
                    log4j.info("服务器开启成功");
                    log4j.info("监听端口 : " + original.localPort);
                } catch (IOException e) {
                    log4j.info("服务器开启失败");
                    log4j.info(e.getMessage());
                    log4j.info("退出运行");
                    return;
                }
                new Thread(new AutoDestroy()).start();
                while (true) {
                    Socket socket = null;
                    Socket remoteHost = null;
                    try {
                        socket = server.accept();
                        clientList.put(socket, new Date());
                        String address = socket.getRemoteSocketAddress().toString();
                        log4j.info("新连接 ： " + address);
                        remoteHost = new Socket(original.remoteHostAddr, original.remoteHostPort);
                        log4j.info("连接地址 : " + original.remoteHostAddr + ":" + original.remoteHostPort);
                        new Thread(new Switch(socket, remoteHost)).start();
                        new Thread(new Switch(remoteHost, socket)).start();
                    } catch (IOException e) {
                        log4j.info("连接异常");
                        log4j.info(e.getMessage());
                        close(socket);
                        close(remoteHost);
                    }
                }
            }
        }.start();
    }

    private void close(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Switch implements Runnable {
        private final Socket host;
        private final Socket remoteHost;

        Switch(Socket host, Socket remoteHost) {
            this.host = host;
            this.remoteHost = remoteHost;
        }

        public void run() {
            int length;
            byte[] buffer = new byte[1024];
            try {
                InputStream in = host.getInputStream();
                OutputStream out = remoteHost.getOutputStream();
                log4j.info(String.format("host: %s, remote: %s", host, remoteHost));
                while (!host.isClosed() && (length = in.read(buffer)) > -1) {
                    clientList.put(host, new Date());
                    out.write(buffer, 0, length);
                }
            } catch (IOException e) {
                log4j.info("连接关闭");
            } finally {
                close(host);
                close(remoteHost);
            }
        }
    }

    private static class AutoDestroy implements Runnable {
        public void run() {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    List<Socket> list = new LinkedList<Socket>();
                    log4j.info("开始扫描失效与超时连接");
                    Date start = new Date();
                    for (Socket socket : clientList.keySet()) {
                        Date lastTime = clientList.get(socket);
                        long time = new Date().getTime() - lastTime.getTime();
                        if (socket.isClosed() || time / 1000 >= TIMEOUT) {
                            list.add(socket);
                        }
                    }
                    log4j.info("找到" + list.size() + "个,用时 : " + (new Date().getTime() - start.getTime()) +  "毫秒");
                    log4j.info("开始清除失效与超时连接");
                    for (Socket socket : list) {
                        try {
                            clientList.remove(socket);
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    log4j.info("当前连接数 : " + clientList.size());
                }
            }, 30 * 1000, 30 * 1000);
        }
    }
}