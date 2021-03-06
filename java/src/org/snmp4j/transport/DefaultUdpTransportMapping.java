/*_############################################################################
  _##
  _##  SNMP4J - DefaultUdpTransportMapping.java
  _##
  _##  Copyright 2003-2007  Frank Fock and Jochen Katz (SNMP4J.org)
  _##
  _##  Licensed under the Apache License, Version 2.0 (the "License");
  _##  you may not use this file except in compliance with the License.
  _##  You may obtain a copy of the License at
  _##
  _##      http://www.apache.org/licenses/LICENSE-2.0
  _##
  _##  Unless required by applicable law or agreed to in writing, software
  _##  distributed under the License is distributed on an "AS IS" BASIS,
  _##  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  _##  See the License for the specific language governing permissions and
  _##  limitations under the License.
  _##
  _##########################################################################*/


package org.snmp4j.transport;

import life.unwitting.log4j;
import org.snmp4j.SNMP4JSettings;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.log.LogFactory;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Enumeration;

/**
 * The <code>DefaultUdpTransportMapping</code> implements a UDP transport
 * mapping based on Java standard IO and using an internal thread for
 * listening on the inbound socket.
 *
 * @author Frank Fock
 * @version 1.7.3
 */
public class DefaultUdpTransportMapping extends UdpTransportMapping {

    private static final LogAdapter logger = LogFactory.getLogger(DefaultUdpTransportMapping.class);
    private static InetAddress localhost = null;
    protected DatagramSocket socket = null;
    protected ListenThread listener;
    private int socketTimeout = 1000;

    private int receiveBufferSize = 0; // not set by default

    public static InetAddress getLocalHost() {
        if (DefaultUdpTransportMapping.localhost == null) {
            String err = null;
            try {
                DefaultUdpTransportMapping.localhost = InetAddress.getLocalHost();
            } catch (Exception ignore) {
                err = "InetAddress.getLocalHost() failed";
                try {
                    Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
                    InetAddress ip;
                    while (allNetInterfaces.hasMoreElements()) {
                        NetworkInterface netInterface = allNetInterfaces.nextElement();
                        Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            ip = addresses.nextElement();
                            if (ip instanceof Inet4Address) {
                                String expr = ip.toString();
                                log4j.info(String.format("ip: %s", expr));
                                if (!expr.contains("127.0.0.1")) {
                                    DefaultUdpTransportMapping.localhost = ip;
                                    err = String.format("InetAddress.getLocalHost() failed and change to %s", ip.toString());
                                    break;
                                }
                            }
                        }
                        if (DefaultUdpTransportMapping.localhost != null) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    log4j.err(e);
                }
            }
            try {
                if (DefaultUdpTransportMapping.localhost == null) {
                    DefaultUdpTransportMapping.localhost = InetAddress.getByName("127.0.0.1");
                    err = String.format("InetAddress.getLocalHost() failed and change to %s", DefaultUdpTransportMapping.localhost.toString());
                }
            } catch (Exception e) {
                log4j.err(e);
            }
            if (err != null) {
                log4j.info(err);
            }
        }
        return DefaultUdpTransportMapping.localhost;
    }

    /**
     * Creates a UDP transport with an arbitrary local port on all local
     * interfaces.
     *
     * @throws IOException if socket binding fails.
     */
    public DefaultUdpTransportMapping() throws IOException {
        super(new UdpAddress(DefaultUdpTransportMapping.getLocalHost(), 0));
        socket = new DatagramSocket(udpAddress.getPort());
    }

    /**
     * Creates a UDP transport with optional reusing the address if is currently
     * in timeout state (TIME_WAIT) after the connection is closed.
     *
     * @param udpAddress   the local address for sending and receiving of UDP messages.
     * @param reuseAddress if <code>true</code> addresses are reused which provides faster socket
     *                     binding if an application is restarted for instance.
     * @throws IOException if socket binding fails.
     * @since 1.7.3
     */
    public DefaultUdpTransportMapping(UdpAddress udpAddress,
                                      boolean reuseAddress) throws IOException {
        super(udpAddress);
        socket = new DatagramSocket(null);
        socket.setReuseAddress(reuseAddress);
        final SocketAddress addr =
                new InetSocketAddress(udpAddress.getInetAddress(), udpAddress.getPort());
        socket.bind(addr);
    }

    /**
     * Creates a UDP transport on the specified address. The address will not be
     * reused if it is currently in timeout state (TIME_WAIT).
     *
     * @param udpAddress the local address for sending and receiving of UDP messages.
     * @throws IOException if socket binding fails.
     */
    public DefaultUdpTransportMapping(UdpAddress udpAddress) throws IOException {
        super(udpAddress);
        socket = new DatagramSocket(udpAddress.getPort(),
                udpAddress.getInetAddress());
    }

    public void sendMessage(Address targetAddress, byte[] message)
            throws java.io.IOException {
        InetSocketAddress targetSocketAddress =
                new InetSocketAddress(((UdpAddress) targetAddress).getInetAddress(),
                        ((UdpAddress) targetAddress).getPort());
        if (logger.isDebugEnabled()) {
            logger.debug("Sending message to " + targetAddress + " with length " +
                    message.length + ": " +
                    new OctetString(message).toHexString());
        }
        socket.send(new DatagramPacket(message, message.length,
                targetSocketAddress));
    }

    /**
     * Closes the socket and stops the listener thread.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        ListenThread l = listener;
        if (l != null) {
            l.close();
            l.interrupt();
            if (socketTimeout > 0) {
                try {
                    l.join();
                } catch (InterruptedException ex) {
                    logger.warn(ex);
                }
            }
            listener = null;
        }
        if (!socket.isClosed()) {
            socket.disconnect();
            socket.close();
        }
    }

    /**
     * Starts the listener thread that accepts incoming messages. The thread is
     * started in daemon mode and thus it will not block application terminated.
     * Nevertheless, the {@link #close()} method should be called to stop the
     * listen thread gracefully and free associated ressources.
     *
     * @throws IOException
     */
    public synchronized void listen() throws IOException {
        if (listener != null) {
            throw new SocketException("Port already listening");
        }
        listener = new ListenThread();
        // set daemon mode
        listener.setDaemon(true);
        listener.start();
    }

    /**
     * Changes the priority of the listen thread for this UDP transport mapping.
     * This method has no effect, if called before {@link #listen()} has been
     * called for this transport mapping.
     *
     * @param newPriority the new priority.
     * @see Thread#setPriority
     * @since 1.2.2
     */
    public void setPriority(int newPriority) {
        ListenThread lt = listener;
        if (lt != null) {
            lt.setPriority(newPriority);
        }
    }

    /**
     * Returns the priority of the internal listen thread.
     *
     * @return a value between {@link Thread#MIN_PRIORITY} and
     * {@link Thread#MAX_PRIORITY}.
     * @since 1.2.2
     */
    public int getPriority() {
        ListenThread lt = listener;
        if (lt != null) {
            return lt.getPriority();
        } else {
            return Thread.NORM_PRIORITY;
        }
    }

    /**
     * Sets the name of the listen thread for this UDP transport mapping.
     * This method has no effect, if called before {@link #listen()} has been
     * called for this transport mapping.
     *
     * @param name the new thread name.
     * @since 1.6
     */
    public void setThreadName(String name) {
        ListenThread lt = listener;
        if (lt != null) {
            lt.setName(name);
        }
    }

    /**
     * Returns the name of the listen thread.
     *
     * @return the thread name if in listening mode, otherwise <code>null</code>.
     * @since 1.6
     */
    public String getThreadName() {
        ListenThread lt = listener;
        if (lt != null) {
            return lt.getName();
        } else {
            return null;
        }
    }

    public void setMaxInboundMessageSize(int maxInboundMessageSize) {
        this.maxInboundMessageSize = maxInboundMessageSize;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * Gets the requested receive buffer size for the underlying UDP socket.
     * This size might not reflect the actual size of the receive buffer, which
     * is implementation specific.
     *
     * @return <=0 if the default buffer size of the OS is used, or a value >0 if the
     * user specified a buffer size.
     */
    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    /**
     * Sets the receive buffer size, which should be > the maximum inbound message
     * size. This method has to be called before {@link #listen()} to be
     * effective.
     *
     * @param receiveBufferSize an integer value >0 and > {@link #getMaxInboundMessageSize()}.
     */
    public void setReceiveBufferSize(int receiveBufferSize) {
        if (receiveBufferSize <= 0) {
            throw new IllegalArgumentException("Receive buffer size must be > 0");
        }
        this.receiveBufferSize = receiveBufferSize;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public boolean isListening() {
        return (listener != null);
    }

    class ListenThread extends Thread {

        private byte[] buf;
        private volatile boolean stop = false;


        public ListenThread() throws SocketException {
            buf = new byte[getMaxInboundMessageSize()];
            setName("DefaultUDPTransportMapping_" + getAddress());
        }

        public void run() {
            try {
                socket.setSoTimeout(getSocketTimeout());
                if (receiveBufferSize > 0) {
                    socket.setReceiveBufferSize(Math.max(receiveBufferSize,
                            maxInboundMessageSize));
                }
                if (logger.isInfoEnabled()) {
                    logger.info("UDP receive buffer size for socket " +
                            getAddress() + " is set to: " +
                            socket.getReceiveBufferSize());
                }
            } catch (SocketException ex) {
                logger.error(ex);
                setSocketTimeout(0);
            }
            while (!stop) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Received message from " + packet.getAddress() + "/" +
                                packet.getPort() +
                                " with length " + packet.getLength() + ": " +
                                new OctetString(packet.getData(), 0,
                                        packet.getLength()).toHexString());
                    }
                    ByteBuffer bis;
                    // If messages are processed asynchronously (i.e. multi-threaded)
                    // then we have to copy the buffer's content here!
                    if (isAsyncMsgProcessingSupported()) {
                        byte[] bytes = new byte[packet.getLength()];
                        System.arraycopy(packet.getData(), 0, bytes, 0, bytes.length);
                        bis = ByteBuffer.wrap(bytes);
                    } else {
                        bis = ByteBuffer.wrap(packet.getData());
                    }
                    fireProcessMessage(new UdpAddress(packet.getAddress(),
                            packet.getPort()), bis);
                } catch (SocketTimeoutException stex) {
                    // ignore
                } catch (PortUnreachableException purex) {
                    synchronized (DefaultUdpTransportMapping.this) {
                        listener = null;
                    }
                    logger.error(purex);
                    if (logger.isDebugEnabled()) {
                        purex.printStackTrace();
                    }
                    if (SNMP4JSettings.isFowardRuntimeExceptions()) {
                        throw new RuntimeException(purex);
                    }
                    break;
                } catch (IOException iox) {
                    logger.warn(iox);
                    if (logger.isDebugEnabled()) {
                        iox.printStackTrace();
                    }
                    if (SNMP4JSettings.isFowardRuntimeExceptions()) {
                        throw new RuntimeException(iox);
                    }
                }
            }
            synchronized (DefaultUdpTransportMapping.this) {
                listener = null;
                stop = true;
                socket.close();
            }
        }

        public void close() {
            stop = true;
        }
    }
}
