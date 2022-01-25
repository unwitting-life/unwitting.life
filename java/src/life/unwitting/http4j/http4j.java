package life.unwitting.http4j;

import life.unwitting.http4j.impl.HttpRequest;
import life.unwitting.http4j.impl.HttpRoute;
import life.unwitting.http4j.impl.HttpServer;
import life.unwitting.http4j.impl.HttpsServer;
import life.unwitting.NakovHttpForwardServer.NakovHttpForwardServer;
import life.unwitting.string4j;
import life.unwitting.lib;
import life.unwitting.log4j;
import jp.co.fujixerox.xcp.plugin.repository.PluginDescriptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class http4j {
    public static class route {
        public String method;
        public String uri;
        public String description;

        public route(String method, String uri, String description) {
            this.method = method;
            this.uri = uri;
            this.description = description;
        }

        public boolean valid() {
            return lib.notNullOrEmpty(this.method) && lib.notNullOrEmpty(this.uri);
        }

        @Override
        public String toString() {
            return String.format("%s %s", this.method, this.uri);
        }
    }

    public ServerSocket serverSocket;
    public HttpsServer secureServer;
    public LinkedHashMap<route, HttpRoute> routes = new LinkedHashMap<route, HttpRoute>();
    public int port;
    public int securePort;
    public boolean isProxyEnabled = true;
    public boolean isLoopbackMapping = false;
    public boolean isDeepTest = false;
    @SuppressWarnings("SpellCheckingInspection")
    public String wwwroot = null;
    public String defaultIndexHtmlName = "index.htm";
    public Class<?> prototype = null;
    public PluginDescriptor pluginDescriptor = null;

    public http4j() {
        this.init(HttpRequest.HttpPort, HttpRequest.HttpsPort);
    }

    public http4j(int port, int securePort) {
        this.init(port, securePort);
    }

    protected void init(int port, int securePort) {
        try {
            this.port = port;
            this.securePort = securePort;
        } catch (Exception e) {
            log4j.err(e);
        }
    }

    public void addRoute(String method, String uri, HttpRoute route, String description) {
        if (lib.notNullOrEmpty(method) &&
                lib.notNullOrEmpty(uri) &&
                lib.notNull(route)) {
            this.routes.put(new route(method, uri, description), route);
        }
    }

    protected String routeKey(String method, String uri) {
        return String.format("%s %s", method, uri);
    }

    @SuppressWarnings("deprecation")
    public HttpRoute[] findRoutes(String method, String uri) {
        ArrayList<HttpRoute> routes = new ArrayList<HttpRoute>();
        try {
            for (Map.Entry<route, HttpRoute> entry : this.routes.entrySet()) {
                route key = entry.getKey();
                String decodedUri = URLDecoder.decode(uri);
                if (key != null &&
                        key.valid() &&
                        key.method.equalsIgnoreCase(method) &&
                        (Pattern.matches(key.uri, uri) || Pattern.matches(key.uri, decodedUri))) {
                    HttpRoute route = this.routes.get(key);
                    if (route != null) {
                        if (key.uri.equals(uri)) {
                            routes.clear();
                            routes.add(route);
                            break;
                        } else {
                            routes.add(route);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return routes.size() == 0 ? null : lib.of(routes).toArray(HttpRoute.class);
    }

    public void launch() {
        final http4j inst = this;
        new Thread() {
            @SuppressWarnings({"StringConcatenationInLoop"})
            @Override
            public void run() {
                try {
                    if (inst.serverSocket != null) {
                        inst.serverSocket.close();
                    }
                    if (inst.secureServer != null) {
                        inst.secureServer.close();
                    }
                    if (inst.isLoopbackMapping) {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    NakovHttpForwardServer.main(null);
                                } catch (Exception e) {
                                    log4j.err(e);
                                }
                            }
                        }.start();
                    }
                    try {
                        inst.serverSocket = new ServerSocket(port);
                        log4j.info(String.format("MyKid Server started on %s:%s",
                                ((InetSocketAddress) inst.serverSocket.getLocalSocketAddress()).getHostName(),
                                ((InetSocketAddress) inst.serverSocket.getLocalSocketAddress()).getPort()));
                        String routeTables = string4j.empty;
                        routeTables += "---------- ROUTE TABLES ----------" + string4j.Lf;
                        for (Map.Entry<route, HttpRoute> e : inst.routes.entrySet()) {
                            route key = e.getKey();
                            if (key.valid()) {
                                routeTables += key.method + string4j.Space + key.uri + string4j.Lf;
                            }
                        }
                        routeTables += "----------------------------------";
                        log4j.info(routeTables);
                        inst.secureServer = new HttpsServer(inst);
                        inst.secureServer.securePort = inst.securePort;
                        inst.secureServer.launch();
                        while (true) {
                            try {
                                new HttpServer(inst, serverSocket.accept()).launch();
                            } catch (IOException e) {
                                log4j.err(e);
                            }
                        }
                    } catch (Exception e) {
                        log4j.err(e);
                    }
                } catch (Exception e) {
                    log4j.err(e);
                }
            }
        }.start();
    }
}