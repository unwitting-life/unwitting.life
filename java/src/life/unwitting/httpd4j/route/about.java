package life.unwitting.httpd4j.route;

import life.unwitting.http4j.impl.HttpRequest;
import life.unwitting.http4j.impl.HttpRoute;
import life.unwitting.log4j;

public class about extends HttpRoute {
    public boolean invoke(HttpRequest request, byte[] raw) {
        boolean handled = false;
        try {
            handled = this.text(request, "Hello! Build on 2021/12/28 22:38:31".getBytes());
        } catch (Exception e) {
            log4j.err(e);
        }
        return handled;
    }
}
