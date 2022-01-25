package life.unwitting.NakovHttpForwardServer;
/*
 * Nakov TCP Socket Forward Server - freeware
 * Version 1.0 - March, 2002
 * (c) 2001 by Svetlin Nakov - http://www.nakov.com
 *
 * Short decription: Nakov Forward Server is designed to forward (redirect) TCP
 * connections from a client to a server choosen from a servers list. When a client
 * is connected to Nakov Forward Server, a new connection is opened to some of the
 * specified destination servers and all the traffic from destination server to
 * Nakov Forward Server is redirected to the client and also all the traffic from
 * the client to Nakov Forward Server is redirected to destination server. That
 * way Nakov Forward Server makes transparent redirection of TCP connections.
 * The data transfer schema is the following:
 *
 *     CLIENT <--> NAKOV_FORWARD_SERVER <--> DESTINATION_SERVER
 *
 * Clients and Destination Servers communicate only with Nakov Forward Server.
 *
 * Nakov Forward Server supports failt tolerance. When some of the servers in the
 * list fail to respond to TCP connect request (dead server), Nakov Forward Server
 * tries the next server in the list until it finds alive server. All dead servers
 * are checked if they are alive through some time interval and when some server
 * becomes available, it is added to alive list. When no server is alive, no
 * connection will be established.
 *
 * Nakov Forward Server supports also load balancing features. If load balancing
 * is enabled, when a client connection is accepted, Nakov Forward Server will
 * redirect the client to the least loaded server from the servers list. We consider
 * the server which hast minimal alive connections established by Nakov Forward
 * Server is least loaded.
 *
 * What we gain when we use Nakov Proxy Server?
 *     - Destination server does not know the real IP of the client. It thinks
 * that the IP of Nakov Forward Server is its client. Thus we can use a server
 * licensed for one IP address on several machines simultaneously.
 *     - Nakov Forward Server can run on a port number that is allowed by the
 * firewall and forward to a port number that is not allowed by firewall. Thus,
 * started on a server in a local network, it can give access to some disabled
 * by the firewall services.
 *     - Nakov Forward Server can give access to multiple clients to some service
 * that is allowed only for some fixed IP address when started on the machine
 * with this IP.
 *     - Fault Tolerance (failover) of Nakov Forward Server helps to avoid faults
 * when some of the servers die. Of course there is special hardware for this, but
 * it has high price. Instead you can use Nakov Forward Server (that is free).
 * If you setup several Nakov Forward Servers configured to use the same set of
 * destination servers and if you configure your routers to use redirect traffic
 * to both servers, you will obtain a stable fault tolerance system. In such a
 * system you have guarantee that crash of any of the servers (including some of
 * the Nakov Forward Servers) will not stop the service that these servers provide.
 * Of course the destination servers should run in a cluster and replicate their
 * sessions.
 *     - Load balancing helps to avoid overloading of the servers by distributing
 * the clients between them. Of course this should be done by special hardware
 * called "load balancer", but if we don't have such hardware, we can still use
 * this technology. When we use load balancing, all the servers in the list should
 * be running in a cluster and there should be no matter what of the servers the
 * client is connected to. The servers should communicate each other and replicate
 * their session data.
 *
 * NakovForwardServer.properties configuration file contains all the settings of
 * Nakov Forward Server. The only mandatory field is "Servers"
 * Destination servers should be in following format:
 *     Servers = server1:port1, server2:port2, server3:port3, ...
 * For example:
 *     Servers = 192.168.0.22:80, rakiya:80, 192.168.0.23:80, www.nakov.com:80
 * Nakov Forward Server listening port should be in format:
 *     ListeningPort = some_port (in range 1-65535)
 * Using load balancing algorithm is specified by following line:
 *     LoadBalancing = Yes/No
 * Check alive interval through which all dead threads should be re-checked if
 * they are alive is specified by following line:
 *     CheckAliveInterval = time_interval (in milliseconds)
 */

import life.unwitting.http4j.impl.HttpsServer;
import life.unwitting.log4j;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import life.unwitting.runtime4j;

public class NakovHttpForwardServer {
    private static final boolean ENABLE_LOGGING = true;
    public static final String SETTINGS_FILE_NAME = "NakovForwardServer.properties";
    private ServerDescription[] serversList = null; // Read from NakovForwardServer.properties
    private int listeningTcpPort = 0;               // Read from NakovForwardServer.properties
    private boolean useLoadBalancingAlgorithm = true;
    private long checkAliveIntervalMs = 10 * 1000;

    public static class ServerDescription {
        public String host;
        public int port;
        public int clientsConnectedCount = 0;
        public boolean isAlive = true;

        public ServerDescription(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    public ServerDescription[] getServersList() {
        return serversList;
    }

    public long getCheckAliveIntervalMs() {
        return checkAliveIntervalMs;
    }

    public boolean isLoadBalancingEnabled() {
        return useLoadBalancingAlgorithm;
    }

    public void readSettings() throws Exception {
        Properties props = new Properties();
        props.load(NakovHttpForwardServer.class.getResourceAsStream(SETTINGS_FILE_NAME));
        String serversProperty = props.getProperty("Servers");
        if (!runtime4j.isXCPJvm()) {
            serversProperty = props.getProperty("ServersNonXCPJvm");
        }
        if (serversProperty == null)
            throw new Exception("The server list can not be empty.");
        try {
            ArrayList<ServerDescription> servers = new ArrayList<ServerDescription>();
            StringTokenizer stServers = new StringTokenizer(serversProperty, ",");
            while (stServers.hasMoreTokens()) {
                String serverAndPort = stServers.nextToken().trim();
                StringTokenizer stServerPort = new StringTokenizer(serverAndPort, ": ");
                String host = stServerPort.nextToken();
                int port = Integer.parseInt(stServerPort.nextToken());
                servers.add(new ServerDescription(host, port));
            }
            serversList = servers.toArray(new ServerDescription[]{});
        } catch (Exception e) {
            throw new Exception("Invalid server list format : " + serversProperty);
        }
        if (serversList.length == 0) {
            throw new Exception("The server list can not be empty.");
        }
        try {
            this.listeningTcpPort = Integer.parseInt(props.getProperty("ListeningPort"));
        } catch (Exception e) {
            log("Server listening port not specified. Using default port : " + this.listeningTcpPort);
        }
        try {
            String loadBalancing = props.getProperty("LoadBalancing").toLowerCase();
            useLoadBalancingAlgorithm = (loadBalancing.equals("yes") ||
                    loadBalancing.equals("true") || loadBalancing.equals("1") ||
                    loadBalancing.equals("enable") || loadBalancing.equals("enabled"));
        } catch (Exception e) {
            log("LoadBalancing property is not specified. Using default value : " + useLoadBalancingAlgorithm);
        }
        try {
            checkAliveIntervalMs = Integer.parseInt(props.getProperty("CheckAliveInterval"));
        } catch (Exception e) {
            log("Check alive interval is not specified. Using default value : " + checkAliveIntervalMs + " ms.");
        }
    }

    public void startCheckAliveThread() {
        CheckAliveThread checkAliveThread = new CheckAliveThread(this);
        checkAliveThread.setDaemon(true);
        checkAliveThread.start();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void startForwardServer(Integer localPort) {
        final Integer port = localPort;
        final NakovHttpForwardServer _this = this;
        new Thread() {
            @Override
            public void run() {
                try {
                    int listenPort = port == null ? _this.listeningTcpPort : port;
                    ServerSocket serverSocket = new ServerSocket(listenPort);
                    log("Nakov Forward Server start on TCP port " + listenPort + ".");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        ForwardServerClientThread forwardThread = new ForwardServerClientThread(_this, clientSocket);
                        forwardThread.start();
                    }
                } catch (Exception e) {
                    log4j.err(e);
                }
            }
        }.start();

    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void startSSLForwardServer(Integer localPort) {
        final Integer port = localPort;
        final NakovHttpForwardServer _this = this;
        new Thread() {
            @Override
            public void run() {
                try {
                    int listenPort = port == null ? _this.listeningTcpPort : port;
                    SSLServerSocket sslServerSocket = HttpsServer.createSSLServerSocket(listenPort);
                    if (sslServerSocket != null) {
                        log("Nakov SSL Forward Server start on TCP port " + listenPort + ".");
                        while (true) {
                            SSLSocket clientSocket = (SSLSocket) sslServerSocket.accept();
                            HttpsServer.handshake(clientSocket);
                            ForwardServerClientThread forwardThread = new ForwardServerClientThread(_this, clientSocket);
                            forwardThread.start();
                        }
                    }
                } catch (Exception e) {
                    log4j.err(e);
                }
            }
        }.start();

    }

    public void log(String aMessage) {
        if (NakovHttpForwardServer.ENABLE_LOGGING) {
            log4j.info(aMessage);
        }
    }

    public static void main(String[] aArgs) {
        try {
            NakovHttpForwardServer srv = new NakovHttpForwardServer();
            srv.readSettings();
            srv.startCheckAliveThread();
            srv.startForwardServer(srv.listeningTcpPort);
            srv.startSSLForwardServer(srv.listeningTcpPort + 1);
        } catch (Exception e) {
            log4j.err(e);
        }
    }
}
