package life.unwitting.httpd4j;

import life.unwitting.*;
import life.unwitting.http4j.http4j;
import life.unwitting.http4j.impl.HttpRoute;
import life.unwitting.http4j.impl.httplib;
import life.unwitting.httpd4j.route.about;
import life.unwitting.httpd4j.route.log;
import jp.co.fujixerox.xcp.plugin.repository.PluginDescriptor;

@SuppressWarnings({"unused", "SameParameterValue", "UnusedReturnValue", "SpellCheckingInspection"})
public class httpd4j {
    private static final http4j server = new http4j();
    public static final int http = pluginEntry.isXCPPluginRuntime() ? 8080 : 80;
    public static final int https = pluginEntry.isXCPPluginRuntime() ? 8443 : 443;
    public static final String UserName = "MOMOLAND";
    public static final String Password = "BAAM";
    public boolean isFirstRunAfterPluginInstalled = false;

    private httpd4j() {
    }

    public static httpd4j newInstance(PluginDescriptor pluginDescriptor) {
        final httpd4j httpd = new httpd4j();
        try {
            if (lib.isNullOrEmpty(log4j.logs())) {
                httpd.isFirstRunAfterPluginInstalled = true;
                runtime4j.printRuntimeProperties();
                log4j.info(String.format("log4j.logFileDirectory: %s", log4j.logFileDirectory));
            }
            httpd.port(httpd4j.http)
                    .securePort(httpd4j.https)
                    .pluginDescriptor(pluginDescriptor)
                    .addRoute(httplib.GET, "/log", new log(httpd4j.UserName, httpd4j.Password))
                    .addRoute(httplib.GET, "/about", new about());
        } catch (Exception e) {
            log4j.err(e);
        }
        return httpd;
    }

    public httpd4j launch() {
        httpd4j.server.launch();
        return this;
    }

    public httpd4j enableProxy() {
        httpd4j.server.isProxyEnabled = true;
        return this;
    }

    public httpd4j disableProxy() {
        httpd4j.server.isProxyEnabled = false;
        return this;
    }

    public httpd4j loopbackMapping() {
        httpd4j.server.isLoopbackMapping = true;
        return this;
    }

    public httpd4j wwwroot(String wwwroot) {
        httpd4j.server.wwwroot = wwwroot;
        return this;
    }

    public httpd4j disableDeepTest() {
        httpd4j.server.isDeepTest = false;
        return this;
    }

    public boolean isDeepTest() {
        return httpd4j.server.isDeepTest;
    }

    public httpd4j port(int port) {
        httpd4j.server.port = port;
        return this;
    }

    public httpd4j securePort(int port) {
        httpd4j.server.securePort = port;
        return this;
    }

    public httpd4j defaultIndex(String defaultIndex) {
        httpd4j.server.defaultIndexHtmlName = defaultIndex;
        return this;
    }

    public httpd4j pluginDescriptor(PluginDescriptor pluginDescriptor) {
        httpd4j.server.pluginDescriptor = pluginDescriptor;
        return this;
    }

    public httpd4j prototype(Class<?> loader) {
        httpd4j.server.prototype = loader;
        return this;
    }

    public httpd4j addRoute(String method, String uri, HttpRoute route) {
        return this.addRoute(method, uri, route, string4j.empty);
    }

    public httpd4j addRoute(String method, String uri, HttpRoute route, String description) {
        httpd4j.server.addRoute(method, uri, route, description);
        return this;
    }

    public http4j server() {
        return httpd4j.server;
    }
}
