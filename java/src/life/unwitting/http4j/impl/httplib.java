package life.unwitting.http4j.impl;

import life.unwitting.http4j.model.DataArrival;
import life.unwitting.http4j.model.KeyValuePair;
import life.unwitting.array4j;
import life.unwitting.string4j;
import life.unwitting.lib;
import life.unwitting.log4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@SuppressWarnings({"unused", "HttpUrlsUsage"})
public class httplib {
    public static final int HttpPort = 80;
    public static final int HttpsPort = 443;
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String QUERY = "QUERY";
    public static final String OPTIONS = "OPTIONS";
    public static final String http = "http://";
    public static final String https = "https://";
    public static final String localhost = "localhost";
    public static final String loopback = "127.0.0.1";
    public static final String Authorization = "Authorization";
    public static final String ContentLength = "Content-Length";
    public static final String ContentType = "Content-Type";
    public static final String Connection = "Connection";
    public static final String TransferEncoding = "Transfer-Encoding";
    public static final String Location = "Location";
    public static final String Host = "Host";
    public static final String CONNECT = "CONNECT";
    public static final String KeepAlive = "Keep-Alive";
    public static final String Basic = "Basic";
    public static final byte[] Ok = ("HTTP/1.1 200 OK" + string4j.CrLf).getBytes();
    public static final byte[] Unauthorized = ("HTTP/1.0 401 Unauthorized" + string4j.CrLf).getBytes();
    public static final byte[] BasicRealm = ("WWW-Authenticate: Basic realm=\"想太多的后台调试\"" + string4j.CrLf).getBytes();
    public static final byte[] NotFound = ("HTTP/1.1 404 NOT FOUND" + string4j.CrLf).getBytes();
    public static final byte[] ConnectionEstablished = ("HTTP/1.1 200 Connection established" + string4j.CrLf).getBytes();
    public static final byte[] EOF = string4j.CrLf.getBytes();

    private boolean receiving = false;
    public Socket socket;
    public InputStream input = null;
    public OutputStream output = null;
    public DataArrival dataArrival = null;

    public httplib(Socket socket) {
        this.setSocket(socket);
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            if (this.socket != null) {
                this.socket.setSoTimeout(60000);
                this.input = this.socket.getInputStream();
                this.output = this.socket.getOutputStream();
            }
        } catch (Exception e) {
            log4j.err(e);
        }
    }

    protected KeyValuePair split(String content) {
        String key = null;
        String value = null;
        if (content != null) {
            int p = content.indexOf(0x20);
            if (p >= 0) {
                key = content.substring(0, p);
                if (lib.notNullOrEmpty(key) && key.charAt(key.length() - 1) == ':') {
                    key = key.substring(0, key.length() - 1);
                }
                value = content.substring(p + 1);
            }
        }
        return new KeyValuePair(key, value);
    }

    public synchronized void receive() {
        try {
            if (!this.receiving) {
                this.receiving = true;
                if (this.dataArrival == null) {
                    this.close();
                } else {
                    int size;
                    byte[] buffer = new byte[8192];
                    while ((size = this.input.read(buffer)) != -1) {
                        byte[] raw = array4j.resize(buffer, size);
                        if (!this.dataArrival.arrived(raw)) {
                            break;
                        }
                    }
                    this.dataArrival.closed();
                }
            }
        } catch (java.net.SocketException e) {
            if (this.dataArrival != null) {
                this.dataArrival.closed();
            }
        } catch (IOException e) {
            if (this.dataArrival != null) {
                this.dataArrival.closed();
            }
        } catch (Exception e) {
            log4j.err(e);
        } finally {
            this.close();
        }
    }

    public boolean write(byte[] data) {
        boolean b = false;
        try {
            if (this.output != null) {
                this.output.write(data);
                this.output.flush();
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return b;
    }

    public void close() {
        try {
            if (this.socket != null) {
                this.socket.close();
            }
            this.input = null;
            this.output = null;
        } catch (Exception e) {
            log4j.err(e);
        }
    }
}
