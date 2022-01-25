package life.unwitting.http4j.impl;

import life.unwitting.http4j.model.DataArrival;
import life.unwitting.thread4j;
import life.unwitting.string4j;
import life.unwitting.lib;
import life.unwitting.log4j;

import java.net.InetSocketAddress;
import java.net.Socket;

@SuppressWarnings("unused")
public class HttpProxy extends httplib {
    public HttpRequest from = null;

    public HttpProxy(HttpRequest request) {
        super(null);
        this.init(request, null, 0);
    }

    public HttpProxy(HttpRequest request, String redirectHost, int redirectPort) {
        super(null);
        this.init(request, redirectHost, redirectPort);
    }

    public void init(HttpRequest request, String host, int port) {
        try {
            final HttpProxy original = this;
            if (request != null) {
                this.from = request;
                String toHost = request.host();
                int toPort = request.port();
                if (lib.of(host).isJsonString() && lib.of(port).ToPort().isJsonString()) {
                    toHost = host;
                    toPort = port;
                }
                Socket to = new Socket();
                if (lib.notNullOrEmpty(toHost) && lib.of(toPort).ToPort().isJsonString()) {
                    to.connect(new InetSocketAddress(toHost, toPort));
                    this.setSocket(to);
                    super.dataArrival = new DataArrival(this) {
                        @Override
                        public boolean arrived(byte[] data) {
                            try {
                                if (original.from != null) {
                                    original.from.write(data);
                                }
                            } catch (Exception e) {
                                log4j.err(e);
                            }
                            return true;
                        }

                        @Override
                        public void closed() {
                            if (original.from != null) {
                                original.from.close();
                            }
                        }
                    };
                    new Thread(new thread4j(this) {
                        @Override
                        public void run() {
                            original.receive();
                        }
                    }).start();
                }
            }
        } catch (Exception e) {
            log4j.err(e);
        }
    }

    public void launch() {
        this.launch(null);
    }

    public void launch(byte[] body) {
        try {
            final HttpProxy original = this;
            if (this.from != null &&
                    this.input != null &&
                    this.output != null) {
                this.from.dataArrival = new DataArrival(this) {
                    @Override
                    public boolean arrived(byte[] data) {
                        try {
                            original.write(data);
                        } catch (Exception e) {
                            log4j.err(e);
                        }
                        return true;
                    }

                    @Override
                    public void closed() {
                        original.close();
                    }
                };
                if (this.from.isProxy &&
                        lib.of(this.from.lines).isJsonString() &&
                        lib.notNullOrEmpty(this.from.method()) &&
                        lib.notNullOrEmpty(this.from.uri())) {
                    StringBuilder lines = new StringBuilder();
                    String uri = this.from.uri().substring(httplib.http.length());
                    int splash = uri.indexOf(string4j.Splash);
                    if (splash >= 0) {
                        uri = uri.substring(splash);
                    } else {
                        uri = string4j.Splash;
                    }
                    String header = string4j.empty +
                            this.from.method() + string4j.Space +
                            uri + string4j.Space +
                            this.from.httpVersion() + string4j.CrLf;
                    lines.append(header);
                    String host = this.from.header(Host);
                    for (int i = 1; i < this.from.lines.size(); i++) {
                        header = this.from.lines.get(i) + string4j.CrLf;
                        if (header.contains(host)) {
                            InetSocketAddress ip = (InetSocketAddress) this.socket.getRemoteSocketAddress();
                            String port = string4j.empty;
                            if (ip.getPort() != HttpPort && ip.getPort() != HttpsPort) {
                                port = string4j.Colon + ip.getPort();
                            }
                            header = Host +
                                    string4j.Colon +
                                    string4j.Space +
                                    ip.getHostName() +
                                    port +
                                    string4j.CrLf;
                        }
                        lines.append(header);
                    }
                    this.output.write(lines.toString().getBytes());
                    this.output.flush();
                } else if (this.from.isSSLProxy) {
                    this.from.output.write(ConnectionEstablished);
                    this.from.output.write(EOF);
                    this.from.output.flush();
                }
                if (lib.of(body).isJsonString()) {
                    this.output.write(body);
                    this.output.flush();
                }
                this.from.receive();
            }
        } catch (Exception e) {
            log4j.err(e);
        }
    }
}
