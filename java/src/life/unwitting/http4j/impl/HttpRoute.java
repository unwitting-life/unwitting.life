package life.unwitting.http4j.impl;

import life.unwitting.string4j;
import life.unwitting.lib;
import life.unwitting.log4j;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class HttpRoute {
    public String userName = null;
    public String password = null;

    public HttpRoute() {
    }

    public HttpRoute(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public boolean authorization(HttpRequest request) {
        boolean b = true;
        if (lib.notNullOrEmpty(this.userName) || lib.notNullOrEmpty(this.password)) {
            b = false;
            try {
                if (request != null) {
                    String author = request.header(httplib.Authorization);
                    if (lib.notNullOrEmpty(author)) {
                        String prefix = httplib.Basic + string4j.Space;
                        int p = author.indexOf(prefix);
                        if (p >= 0) {
                            author = lib.of(author.substring(p + prefix.length())).asBase64ToString();
                            p = author.indexOf(string4j.Colon);
                            if (p >= 0) {
                                String userName = author.substring(0, p);
                                String password = author.substring(p + 1);
                                if (userName.equals(this.userName) && password.equals(this.password)) {
                                    b = true;
                                }
                            }
                        }
                    }
                    if (!b) {
                        request.output.write(httplib.Unauthorized);
                        request.output.write(httplib.BasicRealm);
                        request.output.write(HttpServer.makeHeader(httplib.ContentType, HttpMime.getMimeType(".txt")));
                        request.output.write(httplib.EOF);
                    }
                }
            } catch (Exception e) {
                log4j.err(e);
            }
        }
        return b;
    }

    public abstract boolean invoke(HttpRequest request, byte[] raw);

    protected boolean response(HttpRequest request, String contentType, byte[] raw) {
        return this.response(request, null, contentType, raw);
    }

    protected boolean response(HttpRequest request, LinkedHashMap<String, String> headers, String contentType, byte[] raw) {
        boolean b = false;
        try {
            if (request != null) {
                request.output.write(httplib.Ok);
                if (headers != null) {
                    for (Map.Entry<String, String> e : headers.entrySet()) {
                        request.output.write(HttpServer.makeHeader(e.getKey(), e.getValue()));
                    }
                }
                // https://www.cnblogs.com/nxlhero/p/11670942.html
                request.output.write(HttpServer.makeHeader(httplib.ContentType, lib.isNullOrEmpty((contentType)) ? HttpMime.MIME_APPLICATION_OCTET_STREAM : contentType));
                if (lib.isNull(raw)) {
                    request.output.write(HttpServer.makeHeader(httplib.TransferEncoding, "null"));
                } else {
                    request.output.write(HttpServer.makeHeader(httplib.ContentLength, raw.length));
                }
                request.output.write(httplib.EOF);
                if (!lib.isNullOrZeroLength(raw)) {
                    request.output.write(raw);
                }
                b = true;
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return b;
    }

    public boolean text(HttpRequest request, byte[] raw) {
        return this.response(request, this.makeAccessControlAllowOriginHeader(), HttpMime.getMimeType(".txt"), raw);
    }

    public boolean json(HttpRequest request, byte[] raw) {
        return this.response(request, this.makeAccessControlAllowOriginHeader(), HttpMime.getMimeType(".json"), raw);
    }

    public boolean xml(HttpRequest request, byte[] raw) {
        return this.response(request, this.makeAccessControlAllowOriginHeader(), HttpMime.getMimeType(".xml"), raw);
    }

    public LinkedHashMap<String, String> makeAccessControlAllowOriginHeader() {
        return this.makeAccessControlAllowOriginHeader("*",
                "Accept, Accept-CH, Accept-Charset, Accept-Datetime, Accept-Encoding, Accept-Ext, Accept-Features, Accept-Language, Accept-Params, Accept-Ranges, Access-Control-Allow-Credentials, " +
                        "Access-Control-Allow-Headers, Access-Control-Allow-Methods, Access-Control-Allow-Origin, Access-Control-Expose-Headers, Access-Control-Max-Age, " +
                        "Access-Control-Request-Headers, Access-Control-Request-Method, Age, Allow, Alternates, Authentication-Info, Authorization, C-Ext, C-Man, C-Opt, C-PEP, C-PEP-Info, CONNECT, " +
                        "Cache-Control, Compliance, Connection, Content-Base, Content-Disposition, Content-Encoding, Content-ID, Content-Language, Content-Length, Content-Location, Content-MD5, " +
                        "Content-Range, Content-Script-Type, Content-Security-Policy, Content-Style-Type, Content-Transfer-Encoding, Content-Type, Content-Version, Cookie, Cost, DAV, DELETE, DNT, " +
                        "DPR, Date, Default-Style, Delta-Base, Depth, Derived-From, Destination, Differential-ID, Digest, ETag, Expect, Expires, Ext, From, GET, GetProfile, HEAD, HTTP-date, Host, " +
                        "IM, If, If-Match, If-Modified-Since, If-None-Match, If-Range, If-Unmodified-Since, Keep-Alive, Label, Last-Event-ID, Last-Modified, Link, Location, Lock-Token, " +
                        "MIME-Version, Man, Max-Forwards, Media-Range, Message-ID, Meter, Negotiate, Non-Compliance, OPTION, OPTIONS, OWS, Opt, Optional, Ordering-Type, Origin, Overwrite, P3P, PEP," +
                        " PICS-Label, POST, PUT, Pep-Info, Permanent, Position, Pragma, ProfileObject, Protocol, Protocol-Query, Protocol-Request, Proxy-Authenticate, Proxy-Authentication-Info, " +
                        "Proxy-Authorization, Proxy-Features, Proxy-Instruction, Public, RWS, Range, Referer, Refresh, Resolution-Hint, Resolver-Location, Retry-After, Safe, " +
                        "Sec-Websocket-Extensions, Sec-Websocket-Key, Sec-Websocket-Origin, Sec-Websocket-Protocol, Sec-Websocket-Version, Security-Scheme, Server, Set-Cookie, Set-Cookie2, " +
                        "SetProfile, SoapAction, Status, Status-URI, Strict-Transport-Security, SubOK, Subst, Surrogate-Capability, Surrogate-Control, TCN, TE, TRACE, Timeout, Title, Trailer, " +
                        "Transfer-Encoding, UA-Color, UA-Media, UA-Pixels, UA-Resolution, UA-Windowpixels, URI, Upgrade, User-Agent, Variant-Vary, Vary, Version, Via, Viewport-Width, " +
                        "WWW-Authenticate, Want-Digest, Warning, Width, X-Content-Duration, X-Content-Security-Policy, X-Content-Type-Options, X-CustomHeader, X-DNSPrefetch-Control, " +
                        "X-Forwarded-For, X-Forwarded-Port, X-Forwarded-Proto, X-Frame-Options, X-Modified, X-OTHER, X-PING, X-PINGOTHER, X-Powered-By, X-Requested-With");
    }

    public LinkedHashMap<String, String> makeAccessControlAllowOriginHeader(String domain, String headers) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("Access-Control-Allow-Origin", domain);
        map.put("Access-Control-Allow-Headers", headers);
        map.put("Access-Control-Allow-Methods", "GET, POST, QUERY, OPTIONS");
        return map;
    }

    public boolean allowCrossDomainAccess(HttpRequest request) {
        return this.response(request, this.makeAccessControlAllowOriginHeader(), null, null);
    }
}

