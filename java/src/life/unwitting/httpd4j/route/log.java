package life.unwitting.httpd4j.route;

import life.unwitting.http4j.impl.HttpRequest;
import life.unwitting.http4j.impl.HttpRoute;
import life.unwitting.log4j;

public class log extends HttpRoute {
    public log(String userName, String password) {
        super(userName, password);
    }

    public boolean invoke(HttpRequest request, byte[] raw) {
        boolean handled = false;
        try {
            handled = this.text(request, log4j.logs().getBytes());
        } catch (Exception e) {
            log4j.err(e);
        }
        return handled;
    }
}
