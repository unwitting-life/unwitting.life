package life.unwitting.http4j.impl;

import life.unwitting.http4j.model.DataArrival;
import life.unwitting.http4j.http4j;
import life.unwitting.file4j;
import life.unwitting.string4j;
import life.unwitting.lib;
import life.unwitting.log4j;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class HttpServer {
    protected Socket socket;
    protected http4j httpServer;
    protected ArrayList<Byte> raw = new ArrayList<Byte>();
    protected HttpRequest request;

    public HttpServer(http4j server, Socket socket) {
        this.httpServer = server;
        this.socket = socket;
    }

    public void launch() {
        final HttpServer original = this;
        new Thread() {
            @Override
            public void run() {
                try {
                    if (!original.doProxy()) {
                        original.doRequest();
                    }
                } catch (Exception e) {
                    log4j.err(e);
                }
            }
        }.start();
    }

    public void close() {
        if (this.socket != null) {
            try {
                this.socket.close();
                this.socket = null;
            } catch (Exception e) {
                log4j.err(e);
            }
        }
    }

    protected boolean doProxy() {
        boolean b = false;
        try {
            if (this.httpServer != null) {
                if (this.request == null) {
                    this.request = new HttpRequest(this);
                }
                if ((this.request.isProxy || this.request.isSSLProxy) &&
                        lib.of(this.request.lines).isJsonString() &&
                        lib.of(this.request.host()).isJsonString() &&
                        lib.of(this.request.port()).ToPort().isJsonString()) {
                    if (this.httpServer.isProxyEnabled) {
                        new HttpProxy(request).launch();
                    } else {
                        this.close();
                    }
                }
            }
            b = this.request.isProxy || this.request.isSSLProxy;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    protected void doRequest() {
        if (this.request == null) {
            this.request = new HttpRequest(this);
        }
        if (this.httpServer != null) {
            this.raw.clear();
            this.request.dataArrival = new DataArrival(this) {
                @Override
                public boolean arrived(byte[] data) {
                    HttpServer _this = (HttpServer) this.parameter;
                    int handled = 0;
                    if (lib.of(data).isJsonString()) {
                        _this.raw.addAll(lib.of(data).toList());
                    }
                    if (_this.raw.size() >= _this.request.contentLength()) {
                        handled = _this.invokeCustomerUriRequest();
                    }
                    try {
                        if (handled == 0) {
                            _this.wwwroot();
                        }
                        _this.close();
                    } catch (Exception ignored) {
                    }
                    return true;
                }

                @Override
                public void closed() {
                    HttpServer _this = (HttpServer) this.parameter;
                    System.out.printf("-> %s socket closed%n",
                            ((InetSocketAddress) _this.request.socket.getRemoteSocketAddress()).getHostName());
                }
            };
            int contentLength = this.request.contentLength();
            if (contentLength == 0) {
                this.request.dataArrival.arrived(null);
            } else if (contentLength > 0) {
                this.request.receive();
            }
        }
    }

    protected int invokeCustomerUriRequest() {
        int handled = 0;
        HttpRoute[] routes = this.httpServer.findRoutes(this.request.method(), this.request.uri());
        if (lib.notNullOrZeroLength(routes)) {
            for (HttpRoute route : routes) {
                if (route != null) {
                    if (route.authorization(this.request)) {
                        if (route.invoke(this.request, lib.of(raw).allBytes())) {
                            handled++;
                        }
                    } else {
                        handled++;
                    }
                }
            }
        }
        return handled;
    }

    public static byte[] makeHeader(String key, int value) {
        return HttpServer.makeHeader(key, String.valueOf(value));
    }

    public static byte[] makeHeader(String key, String value) {
        return (key + string4j.Colon + value + string4j.CrLf).getBytes();
    }

    @SuppressWarnings("SpellCheckingInspection")
    protected void wwwroot() {
        try {
            String uri = this.request.uri();
            int statusCode = HttpServletResponse.SC_NOT_FOUND;
            if (lib.isNullOrEmpty(this.httpServer.wwwroot)) {
                String pathName = new File(uri).getName();
                int p = pathName.lastIndexOf(string4j.QuestionMark);
                if (p >= 0) {
                    uri = uri.substring(0, uri.length() - (pathName.length() - p));
                }
                if (uri.equalsIgnoreCase(string4j.Splash)) {
                    uri += this.httpServer.defaultIndexHtmlName;
                }
                InputStream file = lib
                        .nm(this.httpServer.prototype, Object.class)
                        .getResourceAsStream(this.httpServer.wwwroot + uri);
                if (file != null) {
                    this.request.output.write(httplib.Ok);
                    this.request.output.write(makeHeader(httplib.ContentType, HttpMime.getMimeType(new file4j(new File(uri)).extensionName())));
                    byte[] content = lib.of(file).allBytes();
                    if (lib.notNullOrZeroLength(content)) {
                        this.request.output.write(makeHeader(httplib.ContentLength, content.length));
                    }
                    this.request.output.write(httplib.EOF);
                    if (lib.notNullOrZeroLength(content)) {
                        this.request.output.write(content);
                    }
                    file.close();
                    statusCode = HttpServletResponse.SC_OK;
                }
            }
            try {
                if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
                    this.request.output.write(httplib.NotFound);
                    this.request.output.write(httplib.EOF);
                }
            } catch (Exception e) {
                log4j.err(e);
            }
        } catch (Exception e) {
            log4j.err(e);
        }
    }
}
