package life.unwitting;

import life.unwitting.http4j.impl.HttpMime;
import life.unwitting.http4j.impl.httplib;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class httpUrl4j {
    public static final String SSL = "SSL";
    public Integer ConnectTimeout = 5000;
    public Integer ReadTimeout = 10000;
    public java.net.HttpURLConnection httpURLConnection;
    public Integer responseCode = null;
    public String uri = null;
    public URL url = null;
    public LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

    public httpUrl4j(String uri) {
        try {
            if (lib.notNullOrEmpty(uri)) {
                this.uri = uri;
                this.url = new URL(uri);
                this.httpURLConnection = (java.net.HttpURLConnection) this.url.openConnection();
            }
        } catch (Exception e) {
            log4j.err(e);
        }
    }

    public httpUrl4j(String uri, String proxyHost, int proxyPort) {
        try {
            if (lib.notNullOrEmpty(uri)) {
                this.uri = uri;
                this.httpURLConnection = (java.net.HttpURLConnection) new URL(uri)
                        .openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
            }
        } catch (Exception e) {
            log4j.err(e);
        }
    }

    public String header(String name) {
        String value = null;
        try {
            value = this.httpURLConnection.getHeaderField(name);
        } catch (Exception e) {
            log4j.err(e);
        }
        return value;
    }

    public byte[] get() {
        return this.invoke(httplib.GET, null);
    }

    public byte[] post() {
        return this.post(null);
    }

    public byte[] post(byte[] body) {
        return this.invoke(httplib.POST, body);
    }

    public int upload(byte[] content) {
        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        try {
            status = this.upload(new ByteArrayRequestEntity(content, HttpMime.MIME_APPLICATION_OCTET_STREAM));
        } catch (Exception e) {
            log4j.err(e);
        }
        return status;
    }

    public int upload(String filePath) {
        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                status = this.upload(new FileRequestEntity(file, HttpMime.MIME_APPLICATION_OCTET_STREAM));
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return status;
    }

    public int upload(RequestEntity entity) {
        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        try {
            if (lib.notNullOrEmpty(this.uri) && entity != null) {
                PutMethod putMethod = new PutMethod(this.uri);
                putMethod.getParams().setVersion(HttpVersion.HTTP_1_0);
                putMethod.setRequestEntity(entity);
                status = new HttpClient().executeMethod(putMethod);
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return status;
    }

    public byte[] invoke(String verb, byte[] body) {
        byte[] response = null;
        InputStream inputStream = null;
        try {
            try {
                if (this.httpURLConnection != null && lib.notNullOrEmpty(verb)) {
                    this.httpURLConnection.setRequestMethod(verb);
                    if (this.ConnectTimeout != null) {
                        this.httpURLConnection.setConnectTimeout(this.ConnectTimeout);
                    }
                    if (this.ReadTimeout != null) {
                        this.httpURLConnection.setReadTimeout(this.ReadTimeout);
                    }
                    if (lib.notNullOrZeroLength(body)) {
                        this.httpURLConnection.setDoOutput(true);
                        this.httpURLConnection.getOutputStream().write(body);
                    }
                    inputStream = this.httpURLConnection.getInputStream();
                }
            } catch (javax.net.ssl.SSLHandshakeException ignored) {
                try {
                    if (this.httpURLConnection instanceof HttpsURLConnection) {
                        HttpsURLConnection httpsURLConnection = this.invokeHttpsURL(this.uri, verb);
                        if (httpsURLConnection != null) {
                            this.httpURLConnection = httpsURLConnection;
                            if (lib.notNullOrZeroLength(body)) {
                                httpsURLConnection.getOutputStream().write(body);
                            }
                            try {
                                inputStream = httpsURLConnection.getInputStream();
                            } catch (IOException e) {
                                inputStream = httpsURLConnection.getErrorStream();
                                log4j.err(e);
                            }
                        }
                    }
                } catch (Exception e) {
                    log4j.err(e);
                }
            } catch (IOException e) {
                try {
                    inputStream = this.httpURLConnection.getErrorStream();
                } catch (Exception unknown) {
                    log4j.err(unknown);
                }
            }
            if (inputStream != null) {
                response = lib.of(inputStream).allBytes();
            }
        } catch (Exception e) {
            log4j.err(e);
        } finally {
            try {
                if (this.httpURLConnection != null) {
                    this.responseCode = this.httpURLConnection.getResponseCode();
                }
            } catch (Exception ignored) {
            }
            if (this.httpURLConnection != null) {
                this.httpURLConnection.disconnect();
            }
        }
        return response;
    }

    public void setRequestProperty(String key, String value) {
        try {
            if (this.httpURLConnection != null) {
                this.map.put(key, value);
                this.httpURLConnection.setRequestProperty(key, value);
            }
        } catch (Exception e) {
            log4j.err(e);
        }
    }

    public HttpsURLConnection invokeHttpsURL(String httpsUrl, String verb) {
        URL url;
        HttpsURLConnection con = null;
        try {
            url = new URL(httpsUrl);
            trustAllHosts();
            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
            if (url.getProtocol().toLowerCase().equals("https")) {
                https.setDoOutput(true);
                https.setDoInput(true);
                https.setRequestMethod(verb);
                for (Map.Entry<String, String> e : map.entrySet()) {
                    https.setRequestProperty(e.getKey(), e.getValue());
                }
                https.setHostnameVerifier(DO_NOT_VERIFY);
                con = https;
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return con;
    }

    public static String httpGet(String httpUrl) {
        BufferedReader input = null;
        StringBuilder sb = null;
        URL url = null;
        java.net.HttpURLConnection con = null;
        try {
            url = new URL(httpUrl);
            try {
                // trust all hosts
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                if (url.getProtocol().toLowerCase().equals("https")) {
                    https.setHostnameVerifier(DO_NOT_VERIFY);
                    con = https;
                } else {
                    con = (java.net.HttpURLConnection) url.openConnection();
                }
                input = new BufferedReader(new InputStreamReader(con.getInputStream()));
                sb = new StringBuilder();
                String s;
                while ((s = input.readLine()) != null) {
                    sb.append(s).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } finally {
            // close buffered
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // disconnecting releases the resources held by a connection so they may be closed or reused
            if (con != null) {
                con.disconnect();
            }
        }
        return sb == null ? null : sb.toString();
    }

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                int a = 0;
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                int b = 0;
            }
        }};
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class TrustAnyHostnameVerifier implements HostnameVerifier {
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}

class TrustAnyX509TrustManager implements X509TrustManager {

    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
    }
}
