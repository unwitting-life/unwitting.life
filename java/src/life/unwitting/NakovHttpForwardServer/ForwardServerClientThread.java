package life.unwitting.NakovHttpForwardServer;
/*
 * ForwardServerClientThread handles the clients of Nakov Forward Server. It
 * finds suitable server from the server pool, connects to it and starts
 * the TCP forwarding between given client and its assigned server. After
 * the forwarding is failed and the two threads are stopped, closes the sockets.
 */

import life.unwitting.log4j;

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ForwardServerClientThread extends Thread {
    public NakovHttpForwardServer nakovForwardServer = null;
    public NakovHttpForwardServer.ServerDescription server = null;
    public Socket clientSocket = null;
    public Socket serverSocket = null;
    public boolean bothConnectionsAreAlive = false;
    public String clientHostPort;
    public String serverHostPort;
    public boolean canBrokeClientConnection = false;
    public boolean isEncryption = false;

    /**
     * Creates a client thread for handling clients of NakovForwardServer.
     * A client socket should be connected and passed to this constructor.
     * A server socket is created later by run() method.
     */
    public ForwardServerClientThread(NakovHttpForwardServer aNakovForwardServer, Socket aClientSocket) {
        this.nakovForwardServer = aNakovForwardServer;
        this.clientSocket = aClientSocket;
    }

    /**
     * Obtains a destination server socket to some of the servers in the list.
     * Starts two threads for forwarding : "client in <--> dest server out" and
     * "dest server in <--> client out", waits until one of these threads stop
     * due to read/write failure or connection closure. Closes opened connections.
     */
    public void run() {
        try {
            this.clientHostPort = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();

            // Create a new socket connection to one of the servers from the list
            this.serverSocket = createServerSocket();
            if (serverSocket == null) {  // If all the servers are down
                System.out.println("Can not establish connection for client " +
                        clientHostPort + ". All the servers are down.");
                try {
                    this.clientSocket.close();
                } catch (IOException e) {
                    log4j.err(e);
                }
                return;
            }

            // Obtain input and output streams of server and client
            InputStream clientIn = this.clientSocket.getInputStream();
            OutputStream clientOut = this.clientSocket.getOutputStream();
            InputStream serverIn = this.serverSocket.getInputStream();
            OutputStream serverOut = this.serverSocket.getOutputStream();

            this.serverHostPort = server.host + ":" + server.port;
            nakovForwardServer.log("TCP Forwarding " + clientHostPort + " <--> " + serverHostPort + " started.");

            // Start forwarding of socket data between server and client
            ForwardThread clientForward = new ForwardThread(this, clientIn, serverOut, false);
            ForwardThread serverForward = new ForwardThread(this, serverIn, clientOut, true);
            this.bothConnectionsAreAlive = true;
            clientForward.start();
            serverForward.start();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * connectionBroken() method is called by forwarding child threads to notify
     * this thread (their parent thread) that one of the connections (server or client)
     * is broken (a read/write failure occured). This method disconnects both server
     * and client sockets causing both threads to stop forwarding.
     */
    public synchronized void connectionBroken() {
        if (bothConnectionsAreAlive) {
            // One of the connections is broken. Close the other connection and stop forwarding
            // Closing these socket connections will close their input/output streams
            // and that way will stop the threads that read from these streams
            try {
                serverSocket.close();
            } catch (IOException e) {
                log4j.err(e);
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                log4j.err(e);
            }

            bothConnectionsAreAlive = false;
            server.clientsConnectedCount--;

            nakovForwardServer.log("TCP Forwarding " + clientHostPort + " <--> " + serverHostPort + " stopped.");
        }
    }

    /**
     * @return a new socket connected to some of the servers in the destination
     * servers list. Sequentially a connection to the least loaded server from
     * the list is tried to be established. If connecting to some alive server
     * fail, this server it marked as dead and next alive server is tried. If all
     * the servers are dead, null is returned. Thus if at least one server is alive,
     * a connection will be established (of course after some delay) and the system
     * will not fail (it is fault tolerant). Dead servers can be marked as alive if
     * revived, but this is done later by check alive thread.
     */
    private Socket createServerSocket() throws IOException {
        while (true) {
            this.server = getServerWithMinimalLoad();
            if (this.server == null)  // All the servers are down
                return null;
            try {
                Socket socket = new Socket(this.server.host, this.server.port);
                this.server.clientsConnectedCount++;
                return socket;
            } catch (IOException ioe) {
                this.server.isAlive = false;
            }
        }
    }

    /**
     * @return the least loaded alive server from the server list if load balancing
     * is enabled or first alive server from the list if load balancing algorithm is
     * disabled or null if all the servers in the list are dead.
     */
    private NakovHttpForwardServer.ServerDescription getServerWithMinimalLoad() {
        NakovHttpForwardServer.ServerDescription minLoadServer = null;
        NakovHttpForwardServer.ServerDescription[] servers = this.nakovForwardServer.getServersList();
        for (NakovHttpForwardServer.ServerDescription serverDescription : servers) {
            if (serverDescription.isAlive) {
                if ((minLoadServer == null) || (serverDescription.clientsConnectedCount < minLoadServer.clientsConnectedCount))
                    minLoadServer = serverDescription;
                // If load balancing is disabled, return first alive server
                if (!this.nakovForwardServer.isLoadBalancingEnabled())
                    break;
            }
        }
        return minLoadServer;
    }

}
