package life.unwitting.NakovHttpForwardServer;
/*
 * ForwardThread handles the TCP forwarding between a socket input stream (source)
 * and a socket output stream (destination). It reads the input stream and forwards
 * everything to the output stream. If some of the streams fails, the forwarding
 * is stopped and the parent thread is notified to close all its connections.
 */

import life.unwitting.log4j;

import java.io.InputStream;
import java.io.OutputStream;

public class ForwardThread extends Thread {
    private static final int READ_BUFFER_SIZE = 8192;
    private static final String ContentLengthHeader = "CONTENT-LENGTH:";
    private static final String EncryptionHeader = "Encryption:";
    private static final String httpHeader = "HTTP/1.1";
    private static final String crlf = "\r\n";
    private static final String eofHead = "\r\n\r\n";
    private static final String eofHttp = "\r\n0\r\n\r\n";
    private static final String eofBase64 = "\r\n1\r\n\r\n";
    InputStream mInputStream = null;
    OutputStream mOutputStream = null;
    ForwardServerClientThread mParent = null;
    Boolean isServerToClient = false;
    Boolean isClientToServer = false;

    /**
     * Creates a new traffic forward thread specifying its input stream,
     * output stream and parent thread
     */
    public ForwardThread(ForwardServerClientThread aParent,
                         InputStream aInputStream,
                         OutputStream aOutputStream,
                         Boolean isServerToClient) {
        mInputStream = aInputStream;
        mOutputStream = aOutputStream;
        mParent = aParent;
        this.isServerToClient = isServerToClient;
        this.isClientToServer = !isServerToClient;
    }

    /**
     * Runs the thread. Until it is possible, reads the input stream and puts read
     * data in the output stream. If reading can not be done (due to exception or
     * when the stream is at his end) or writing is failed, exits the thread.
     */
    public void run() {
        byte[] buffer = new byte[READ_BUFFER_SIZE];
        try {
            int receivedBytes = mInputStream.read(buffer);
            while (receivedBytes >= 0) {
                mOutputStream.write(buffer, 0, receivedBytes);
                if (this.isClientToServer) {
                    this.mParent.canBrokeClientConnection = true;
                    receivedBytes = mInputStream.read(buffer);
                    this.mParent.canBrokeClientConnection = false;
                } else {
                    receivedBytes = mInputStream.read(buffer);
                }
            }
        } catch (Exception e) {
            log4j.err(e);
        }

        // Notify parent thread that the connection is broken and forwarding should stop
        // mParent.connectionBroken();
        if (this.isClientToServer) {
            this.mParent.connectionBroken();
        } else if (this.isServerToClient && this.mParent.canBrokeClientConnection) {
            this.mParent.connectionBroken();
        }
    }
}