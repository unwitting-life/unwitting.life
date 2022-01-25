package life.unwitting.http4j.impl;

import life.unwitting.string4j;
import life.unwitting.lib;
import life.unwitting.log4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class HttpRequest extends httplib {
    public static final String Method = "Method";
    public static final String RequestUri = "Request-URI";
    public static final String HTTPVersion = "HTTP-Version";
    public HttpServer client;
    public ArrayList<String> lines = new ArrayList<String>();
    public boolean isSSLProxy = false;
    public boolean isProxy = false;
    public boolean isSecureRequest = false;
    public JSONObject headers = null;
    public ArrayList<Byte> accumulatedBytes = new ArrayList<Byte>();

    public HttpRequest(HttpServer client) {
        super(client.socket);
        this.client = client;
        this.init();
    }

    protected void init() {
        try {
            String uri = this.readLine();
            this.lines.add(uri);
            if (this.client != null && this.client.httpServer != null) {
                try {
                    if (lib.notNullOrEmpty(uri)) {
                        JSONObject header = org.json.HTTP.toJSONObject(uri);
                        String method = header.optString(HttpRequest.Method);
                        if (CONNECT.equalsIgnoreCase(method)) {
                            this.isSSLProxy = true;
                        } else {
                            uri = header.optString(HttpRequest.RequestUri);
                            if (lib.notNullOrEmpty(uri) && (uri.startsWith(httplib.http))) {
                                this.isProxy = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    log4j.err(e);
                }
                this.readHeaders();
            }
        } catch (Exception e) {
            log4j.err(e);
        }
    }

    protected String readLine() {
        StringBuilder line = new StringBuilder();
        this.accumulatedBytes.clear();
        if (this.input != null) {
            int datum;
            int previous = 0;
            try {
                while ((datum = this.input.read()) != -1) {
                    this.accumulatedBytes.add((byte) datum);
                    if (datum == 0) {
                        this.isSecureRequest = true;
                        line = null;
                        break;
                    }
                    if (previous == string4j.Cr.charAt(0) && datum == string4j.Lf.charAt(0)) {
                        line.setLength(line.length() - 1);
                        this.accumulatedBytes.clear();
                        break;
                    }
                    line.append((char) datum);
                    previous = datum;
                }
            } catch (Exception e) {
                log4j.err(e);
            }
        }
        return line == null ? null : line.toString();
    }

    public String header(String header) {
        String value = null;
        try {
            if (this.headers != null && lib.notNullOrEmpty(header)) {
                value = this.headers.getString(header);
            }
        } catch (JSONException e) {
            try {
                JSONArray jArray = this.headers.names();
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        if (header.equalsIgnoreCase(jArray.getString(i))) {
                            value = this.headers.getString(jArray.getString(i));
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return value;
    }

    public String host() {
        String host = this.header(Host);
        if (lib.notNullOrEmpty(host)) {
            int colon = host.lastIndexOf(string4j.Colon);
            if (colon >= 0) {
                host = host.substring(0, colon);
            }
        }
        return host;
    }

    public int port() {
        int port = HttpPort;
        String host = this.header(Host);
        if (lib.notNullOrEmpty(host)) {
            int colon = host.lastIndexOf(string4j.Colon);
            if (colon >= 0) {
                port = lib.of(host.substring(colon + 1)).toInt();
            }
        }
        return port;
    }

    public String method() {
        return this.header(HttpRequest.Method);
    }

    public String uri() {
        return this.header(HttpRequest.RequestUri);
    }

    public String httpVersion() {
        return this.header(HttpRequest.HTTPVersion);
    }

    public int contentLength() {
        return lib.of(this.header(ContentLength)).toInt();
    }

    public boolean isKeepAlive() {
        return lib.of(this.header(Connection)).equalsIgnoreCase(KeepAlive);
    }

//    protected boolean doSSLRequest() {
//        if (this.isSecureRequest &&
//                this.client != null &&
//                this.client.server != null &&
//                this.client.server.serverSocket != null &&
//                t.on(this.client.server.securePort).asPort().valid()) {
//            new iHttpProxy(this,
//                    ((InetSocketAddress) this.client.server.serverSocket.getLocalSocketAddress()).getHostName(),
//                    this.client.server.securePort).launch(t.on(this.accumulatedBytes).allBytes());
//        }
//        return this.isSecureRequest;
//    }

    protected void readHeaders() {
        try {
            String line = this.readLine();
            while (lib.notNullOrEmpty(line)) {
                this.lines.add(line);
                line = this.readLine();
            }
            lines.add(string4j.empty);
            StringBuilder headers = new StringBuilder();
            for (String l : this.lines) {
                if (headers.length() > 0) {
                    headers.append(string4j.Lf);
                }
                headers.append(l);
            }
            this.headers = org.json.HTTP.toJSONObject(headers.toString());
        } catch (Exception e) {
            log4j.err(e);
        }
    }
}
