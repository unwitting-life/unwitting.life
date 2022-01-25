package life.unwitting.http4j.impl;

import life.unwitting.http4j.http4j;
import life.unwitting.base64j;
import life.unwitting.lib;
import life.unwitting.log4j;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

public class HttpsServer {
    public static final String PKCS12 = "PKCS12";
    public static final String TLS = "TLS";
    public static final String SecureCode = "1234";
    public http4j server;
    public SSLServerSocket sslServerSocket;
    public int securePort = httplib.HttpsPort;

    public HttpsServer(http4j server) {
        this.server = server;
        if (lib.notNull(this.server) && lib.of(this.server.securePort).ToPort().isJsonString()) {
            this.securePort = this.server.securePort;
        }
    }

    public static SSLContext createSSLContext() {
        try {
            KeyStore keyStore = KeyStore.getInstance(HttpsServer.PKCS12);
            String pfx = "MIIRPwIBAzCCEPgGCSqGSIb3DQEHAaCCEOkEghDlMIIQ4TCCBXUGCSqGSIb3DQEHAaCCBWYEggVi" +
                    "MIIFXjCCBVoGCyqGSIb3DQEMCgECoIIE+zCCBPcwKQYKKoZIhvcNAQwBAzAbBBSjYqg/Q/nArpDE" +
                    "lP9uyeZlsMfreAIDAMgABIIEyLYA2L/Mctg/4K4ioUbzbAfwZELUZHDdjK6ZJaOr4ekTF8fesXRb" +
                    "ZtiE9Xg0ASCmqpmcMp7ZNWDXJULp71FdufxwVrjyrc4EC4DEuV/kUj+0jPmiPcTCTIH6qfzMji2P" +
                    "JXHvgTpZcQ/J9HVdzDZeW0Rowq2LXxAYiW9Y3sJDS2dubpI9fDni8ZcxgnIJREf3siq2d5Lkh0+h" +
                    "ilRHxI70W6Mfryzp6RDx8MMGGHjkAJ2STBl663HLbKOMo3zKN19vQKrCDZreCEAZWZERJ3GDWBi3" +
                    "nv/fDwi6VRgk01yKXI5QQhzd3NU6lfDBaXQmgB/befJYX/RJQoi3brf1QYv2K8/qsNzRGa87jIzS" +
                    "8cZcaM4s23wDxa6kTLV5AVr9XZXPVcrbDGKuzvZhtiLmyCrQMg1znKTqn5hBNAkCv4ySSoWHv6ci" +
                    "hukod+IVgwydnGgsPnMO4Gp+49VZJTlW4qy2j1RWIK+L7qrEffByF/6WP+3e03n9XBULA+asOSHk" +
                    "Axl8E+rE2ryc2RcTgItGPB9x6RwydYtMWszqbwRT4/WBn0XaGDbFjlLvqnWaypvzgPnwWsqFmi+o" +
                    "IZAmpZ2u1uT4PArpTSbjYoNKaw6BG/27WYb3IeOTB2pknJgWhib3riWiHmfURCXIDKmuhUEkLr+Q" +
                    "GS3iDyDpsPswZUb5Op88J9GphTuQdCiWeMG5dxU/Ji6A3x/ygXt2IX14HBMbH3KAyBHp/9hNVG9O" +
                    "Uq0JG7WUcq7ri/4Mq1WgiVy6Kc9iT97sYF6F3KUkUH9jcrxZeHucQW8a4NaMd4TJFnQPvLj5guPs" +
                    "uK+jsaCsVdjUWpdLSz1fwLi6+0QTYGADj9VJoG2KYZwimIsIgRcpo0onglE2dcME6htWkyDEKDHg" +
                    "gnT7AoYqV1zIPZEw90a3P0tWxAuF9FunlY0Gaw8pW95+qNtmObXR50ogP+qp4pOnRQz4Eekw/a1G" +
                    "AvTHYOvUz8sM+3Y+IchRrGfTWf9SmDITSJEa5d1szfettC5ud0qSIplvOxjxC+SE0rH9QCNS/43X" +
                    "bgpn/GqY9Q6c5M7DX7VwZxWK6FP4tmxiNZDCyXnZyeYUAtcfRAKlBS8VO0v6JfgojPtITKOUr76w" +
                    "+bHdx6OmcqazEpersWYF+jH6ZQ6STfvmjiX26F0ORwrKu1VuRd48CfKaUv/laDTOZjSPUuOOg3UK" +
                    "ZCcN25FaoS9A2+uGbBjcfggdsFbFj7ntk3a2SgRicCrvCcoeRsmUOWbJQ02W2W91/GPgSUcBcraJ" +
                    "Qo1TRcaO5/YP21OI2Ytk06670g4CiorEMN/OT3GhQfob5GQkOXaVCJaPZXAlc794oA8zKEkbD/4X" +
                    "hzkXR7YpnFAXTwy7tz1E2sAacuYngOjEAa8AMIB9eY5QBbGlx2TcpyFSywzCQ9QL9mqUEHMJXuc2" +
                    "24y/yaJy7re1VaVahsYIfDRiNZNjsLyhu6CAXbOnQ7+lEZkHd0HtxZjYUosHk4Eg0L9aMcaVfDT6" +
                    "XNfpapMMZLrEigl+ckO9HPHhaM7cekEfQdhquHZAncqYboN7qLLDmxxSEU0WdKIIolkt1gvcw59z" +
                    "Gn2gTYR7TRnqzCq6SlMJuyPBH/72FKOtxFpSqT+WBI07Re6UB+DfjCrDtFLdZTFMMCMGCSqGSIb3" +
                    "DQEJFTEWBBT6nE1mMuOg7uUJPJT/a71XYKUpMzAlBgkqhkiG9w0BCRQxGB4WAG0AeQBiAGEAYgB5" +
                    "AC4AYwBvAG8AbDCCC2QGCSqGSIb3DQEHBqCCC1UwggtRAgEAMIILSgYJKoZIhvcNAQcBMCkGCiqG" +
                    "SIb3DQEMAQYwGwQUedFTCopfGe1kw8Fm4M7ohhCFKMICAwDIAICCCxD5RbCnTVLaPEyL8zD6tuwB" +
                    "wVutJqKcm/LZ2H/FxEfmyxL8MEPPQ5ErqtQmnIjgvHYddvwxgeRB8Pj2pQmkNoHG1g6Ouc1/BvpD" +
                    "8qltWHlHwGpan4ju4X2NRESdZM2bu9Y2lhcXI+nGt8ud+qwWCwEunJg3OM15LtcuT9jFFwVkc+Pp" +
                    "mDuxzkqCk4CwgNgtSUiyXhOWaeKv6t/6tWuxJZteS0oalSDYQSquuao0oJJPhVSI4Ti85CPMtgrm" +
                    "eeidhdESyU9ZSZugLCcjNzq2w7jsXYHHera61WwglsD0Z28xPCgPnF7VMJHvfpZm1bCvU3DaHn1I" +
                    "Tdw7g3YKuD3TuQHm9Hyvh1YB4gV/4VYFpBAeUZLCGp7dxqnTBTG9Y8het83TfM3KxqzJa3bJDJrY" +
                    "eJ+hJyUYHdgp6nI6xIY91sKEnAH42rraPFo72TJZYSlCheD6LmQOhtXekD33gNXx0xb0eVt50+mx" +
                    "HornLqRkvL687hGHNGbAscSWbpI7WzZQ0wKrqsMAhMmKcyD2z8kVhF/y1FMV6szmnU2/roe+c6s4" +
                    "SG4+FyGo8eBlWliH6aJz/01m9rpd4+MO7U22HmfuvSda2rV2RzPOxwchjbHWEBOAx8fnyRQkcmza" +
                    "42pIVqwC/9+B/qgO2l1Kwx0iR9B+sZe0BcYDwzjXugAeoXeOzNQThDzQBTxMYEu2FTHzPlzyLdIu" +
                    "vWyBexE9vsg4Hg/H+BkQUzGkcBCSM9IUdpsLvGx0rwDviAkmZTFc/yQfrlnbK8CKr74NwvpHb0RD" +
                    "2Q/v+5IAq2Pt6uo9SAIUAPkzHNt6sSX5oebwk+yVpEKDr5engethcCBsj5VOdnNOeEtfl+6o1kqL" +
                    "+1hZt0JOkxBE4AlE+GQi6cQ0tpjk0gYZdUoEAetT+fXlcb6g8diiaF7w/wKKgWnAJKmQnje3/LYx" +
                    "ILV/WNi4KcX9ncj8zR6Rqz2EP8fPtgTx/dsLiu1V9MCryHQdTLxxW/Rqtq+MxQSrwpFMVHGIwQYY" +
                    "ZF5WKT9izfnN3+BvaSXBJK1vsO1tAZOMQ3IpILZaXFvznHUgCocOvSSIIj/BD2qGMZR+MKTKhXKd" +
                    "mkMi5BqmWQ9EVBK3hUQs4Q1vJanJtcFAgCQhRe9KuTgGx3cNiF/pxi3rgZZ33V90NCSVzw4sG6Y+" +
                    "dWxOIAgXXA12pxpMeNn8F8p34wsTQlsQ7Bp9kv7asq2W7b6Bp4mDRtWLUiS/U44Ae9wFIsypAiM/" +
                    "rYzqXw8qU6XdcQdf0P69UPc/TWWHg2Ewp5AESZFqDsiIjM9AXp+DnZssG5cdX8WPTnbeJcJyBwe7" +
                    "l4mcf+TZoiscdc1ObtJgwsx6/xHuV4WQZ0aKxD8uYl73mgqpDNZ9mSiyUmc5fbHGc12bl9GQF0OJ" +
                    "FE8+BsAdYZaHnnJIB1eh5vtCowodz3eKrwoyaMdRIQQGAxVJwTTpSoTUq0KLS2XAqNuEGWIdfc+x" +
                    "pGWo8MusgKfcL2xXDVTOjSxXG1gYcL1E/kBH57+4QK92GfZLcAxOlbb8KyS1cr78XG5bTnKAALfv" +
                    "S2k9pteRFdADQpUPCnyxxiR0IbH7dFeEWq8CRHggHkfq0Nt6rPoC7osIf/gjY8ywB4GSyqT7lBfu" +
                    "qWy00hyCEiGxrOaqmS1MFJ7vpScvVn8dvGE+rLsEk/wC8Y5h/VjuC++BggvCqc0/HUqZDcA8LiEp" +
                    "o8x1RwhKHYcYbSFV4icAGfohpfWkFL3Vcdcf8Hs3AL8xTEwceDWqUKmw4/pL302VQ9YJ/27Lgp87" +
                    "KmII9SPbricdOqSbwEmEogUQoNUrlaFPdeMsz5s7enMWlIQZ8USGThL1rKtjK9rA0QcL6Zj7OZ8q" +
                    "wuRK7zXzA8XHz5zDSXpRe/Jag5cqJsBthJfWSq0nVYwHlrsuMXSWidGyIZVIBYKkXWO2JUzOmg3k" +
                    "4FfF3625LqZZ9zE7KDlWUbAxSz1lkmDDqeKMJ0nMooB1M7G56UOIipvyAM9IadrNBixfv14Ps3pi" +
                    "TF6Juv4P8dkctT7GRbZ6bGyLPiJ/21pxqlZVkynM9PTqe9Rc3hTVbyu1CSP0bCKqg3H6eTmA3aNM" +
                    "LlpAyBTesGDkRC9giEELl+Wc1Mp0Vn/vcP+aCb/KK/KK9nQBNOMr1MXh/qLISNG0FZZtz1s5ku1H" +
                    "zN3MrWzYRVTfD4DY8h2TKm3xOASQ4tuoCHT66DHT0dBMRAXYg0khjGC0Ke8xIAeSuNwdWi9O+5qR" +
                    "VDv6+vaFtRtOiPyQ/0Sy5d2mSt31nAFAmDJTDvJh5/S+1CdNfexfTx07uz9HHq5hG6NV9ZXtOKWy" +
                    "qqSVWwjE6wHu9UuhbXJMDloR/oJEZCxjqp3IDzuYm/vwW0PNjEYE2c7VTOxY/pOPBLN1ljmLdtuo" +
                    "Wh2unuqsHtQ+c535aPv4O+/4rikGPsQPByXbFl+AvkVI7ETIcOdW1dzNbSS3502e4GiKTNKnuKPM" +
                    "ySwMUIgiMZjucMtDiD9rgBnvPxYdFt1fbmBFFX8yX/1r/UpaTZcE/LBDZsFmwTkzEYn+MLorMLRe" +
                    "4kMnXEKGMLW0ft8ASJ9OfXexlpbDE4f/IuL1R/QwnYdGEyUuFZ1JbwE6uhulRLiMXutxy5441rJo" +
                    "E4+VySFwiyn+Pl1yekaGfT7xgXhh3I3IFqyfp/1MhjQsiHe6MnnukI5qRQ526K0BOgjQYxjK7GoP" +
                    "FhjlgKQqlUCblN4Lha2qlWMdbWn8Rqf1kXIE6Ve5IULacXlTh4wutPewji1AJY7dasFeR6o0I5Ln" +
                    "qERSb5AOgssH7xoqRSBnEZQ25aiOC3mODcV4+dnXLaIf2Tr5o2pM8jp1Hvo6zsQXX5a4rr+my64X" +
                    "RINsOfpPasPQ84zZb0i0b65cQHa6KLiZBQd/5ZWRnzC1zUYuzsnIaVVl1Fnb+Ff8joh+flwFO5Yn" +
                    "SUaTbai3+9S2GbGAHrANLpWpE0dwo56dv2sZPRxwwWYoLhmnVMg6UaKyq11Etw6dLdC/CXa9d1Q3" +
                    "SgGRQX83wcpyjagdDRKV3L82O/krFMGXH1qZuqCdPG79iC4JAfB3sruhld2Yo/MYt3S/x1HPgyQk" +
                    "MBFuIFLRM7CuxsSPqPyfx6nHPCz+zNL6DYwJ/7EOEtz89FcRUNcZTWgoZoRXb9Gk5VA5l1vM3T2X" +
                    "nrYzWBkrqyfy0jYFBtdvXp/7GnzZmPhMAHBhESwWkaqxsLbWcC4Z/A/oYOAFZXazXnqylmhM4s2T" +
                    "g1Uj9yzsiYJ7Wz4FB1RKeuwsmWhMta7x7tEwHY8ZXy3ScIT2pG6lQzWHs5ZKJ4zNw4dT5/dYHSJ4" +
                    "0KvwObBztN+dKLJ/BUchOjOHHIXBnObK2rhtIgjp3dSnF+3IFFWDqnGqLbKJVnRN+V/J/Htlnkdq" +
                    "ESum/i6k82DPpHnyUjHSIFtjlqnOjh8xoKiPAMmfubSuHZIHcTOMo1plGKpU5TknkT3jmPfpW0kJ" +
                    "3cYCPyrb49RKauWdD81oQphmWSbpzszBnXHRgP9fe/80y3Wd1loxsu53+ifn/cl7BRu0WssGS8Ui" +
                    "2n3YnEXwjAciXc7OPa77Y9zvz8cY5WnbaZ5FpW6K6/4R+fSEAXyu9hwZoIBaBw3CFoNvht2iITvz" +
                    "mSCH7m8O876S66k6/91sXVZOz6wKgykD7d3JwpSklFgHAkfKQm7dObc9Q3Q0M0cIXbMO5+WnLKDK" +
                    "yl7tm/P8scOA7E3l149eVDmcflCm4HxO7KPmalHGWkEuR+JIV1MtsGAvAZbQ/JGZOWv8nNvuV1ce" +
                    "0gSugKIGOdS8JALi8/5yPY7f7t1CyKowPjAhMAkGBSsOAwIaBQAEFEKvbnwNCPwpWlpcsstyB1Si" +
                    "IZtLBBRcbvLDhYzjVTFz/rGr7bCySsa0RQIDAZAA";
            byte[] decode = base64j.decode(pfx);
            InputStream input = new ByteArrayInputStream(decode);
            if (lib.notNull(input)) {
                keyStore.load(input, HttpsServer.SecureCode.toCharArray());
            }
            return HttpsServer.createSSLContext(keyStore, null, HttpsServer.SecureCode);
        } catch (Exception e) {
            log4j.err(e);
        }
        return null;
    }

    public static SSLContext createSSLContext(String pfx_file_path, String secureCode) {
        try {
            KeyStore keyStore = KeyStore.getInstance(HttpsServer.PKCS12);
            keyStore.load(new FileInputStream(pfx_file_path), secureCode.toCharArray());
            return HttpsServer.createSSLContext(keyStore, null, secureCode);
        } catch (Exception e) {
            log4j.err(e);
        }
        return null;
    }

    public static SSLContext createSSLContext(String x509, String pem, String secureCode) {
        try {
            X509Certificate publicKey = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(x509.getBytes()));
            {
                BufferedInputStream bis = null;
                String keyPath = "/Users/huang/svn/143.94.16.21/svn/ProjectSvn/UI/05 Development/086 カスタムサービス/05 開発/Solution/Qt/products/spdpd/printingQuantityReport/cmake-build-debug/out/bin/x64/macOS/private.pem";
                File privKeyFile = new File(keyPath);
                try {
                    bis = new BufferedInputStream(new FileInputStream(privKeyFile));
                } catch (FileNotFoundException e) {
                    throw new Exception("Could not locate keyfile at '" + keyPath + "'", e);
                }
                byte[] privKeyBytes = new byte[(int) privKeyFile.length()];
                bis.read(privKeyBytes);
                bis.close();
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                KeySpec ks = new PKCS8EncodedKeySpec(privKeyBytes);
                RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(ks);
                int a = 0;
            }
            // byte[] encoded = base64j.decode(pem.replaceAll("\n", ""));
            RSAPrivateKey privateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(pem.getBytes()));
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null);
            keyStore.setCertificateEntry("cert-alias", publicKey);
            keyStore.setKeyEntry("key-alias", privateKey, secureCode.toCharArray(), new Certificate[]{publicKey});
            return HttpsServer.createSSLContext(keyStore, "SunX509", secureCode);
        } catch (Exception e) {
            log4j.err(e);
        }
        return null;
    }

    public static SSLContext createSSLContext(KeyStore keyStore, String keyManagerAlgorithmName, String secureCode) {
        SSLContext sslContext = null;
        try {
            KeyManagerFactory keyManagerFactory;
            if (lib.notNullOrEmpty(keyManagerAlgorithmName)) {
                keyManagerFactory = KeyManagerFactory.getInstance(keyManagerAlgorithmName);
            } else {
                keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            }
            keyManagerFactory.init(keyStore, secureCode.toCharArray());
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManager = trustManagerFactory.getTrustManagers();
            sslContext = SSLContext.getInstance(HttpsServer.TLS);
            sslContext.init(keyManagers, trustManager, null);
        } catch (Exception e) {
            sslContext = null;
            log4j.err(e);
        }
        return sslContext;
    }

    public static SSLServerSocket createSSLServerSocket(int port) {
        SSLServerSocket sslServerSocket = null;
        try {
            SSLContext sslContext = HttpsServer.createSSLContext();
            if (sslContext != null) {
                SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
                sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return sslServerSocket;
    }

    public static boolean handshake(SSLSocket sslSocket) {
        boolean b = false;
        if (sslSocket != null) {
            sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
            try {
                sslSocket.startHandshake();
                SSLSession sslSession = sslSocket.getSession();
                log4j.info("SSLSession :");
                log4j.info("\tProtocol : " + sslSession.getProtocol());
                log4j.info("\tCipher suite : " + sslSession.getCipherSuite());
                b = true;
            } catch (Exception ex) {
                log4j.err(ex);
            }
        }
        return b;
    }

    public void launch() {
        final HttpsServer original = this;
        new Thread() {
            @Override
            public void run() {
                try {
                    original.sslServerSocket = HttpsServer.createSSLServerSocket(original.securePort);
                    if (original.sslServerSocket != null) {
                        log4j.info(String.format("MyKid SSLServer started on %s:%s",
                                ((InetSocketAddress) original.sslServerSocket.getLocalSocketAddress()).getHostName(),
                                original.securePort));
                        while (true) {
                            SSLSocket sslSocket = (SSLSocket) original.sslServerSocket.accept();
                            new ServerThread(original.server, sslSocket).start();
                        }
                    }
                } catch (Exception e) {
                    log4j.err(e);
                }
            }
        }.start();
    }

    public void close() {
        if (this.sslServerSocket != null) {
            try {
                this.sslServerSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    static class ServerThread extends Thread {
        protected final http4j server;
        protected final SSLSocket sslSocket;

        ServerThread(http4j server, SSLSocket sslSocket) {
            this.server = server;
            this.sslSocket = sslSocket;
        }

        public void run() {
            if (HttpsServer.handshake(this.sslSocket)) {
                HttpServer server = new HttpServer(this.server, this.sslSocket);
                server.launch();
            }
        }
    }
}