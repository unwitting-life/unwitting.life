package life.unwitting;

import java.util.ArrayList;
import java.util.Random;

public class rsaLite4j {
    public static class refValue {
        public int value;

        public refValue() {
        }

        public refValue(int value) {
            this.value = value;
        }
    }

    public static final int MAX_N = 1000;
    public Integer product;
    public Integer publicKey;
    public Integer privateKey;

    int exec(int a, int b, refValue x, refValue y) {
        if (b == 0) {
            x.value = 1;
            y.value = 0;
            return a;
        }
        int ans = exec(b, a % b, x, y);
        int tmp = x.value;
        x.value = y.value;
        y.value = tmp - a / b * y.value;
        return ans;
    }

    int GetPrimeNumber(int[] prime) {
        int count = 0;
        for (int i = 201; i < MAX_N; i += 2) {
            boolean flag = false;
            for (int j = 2; (j * j) <= i; j++) {
                if (i % j == 0) {
                    flag = true;
                    break;
                }
            }
            if (!flag) prime[count++] = i;
        }
        return count;
    }

    void initialize() {
        int[] prime = new int[MAX_N];
        int count = this.GetPrimeNumber(prime);
        int r1 = new Random().nextInt() % count;
        int r2 = new Random().nextInt() % count;
        int p1 = prime[r1], p2 = prime[r2];
        product = p1 * p2;
        int m = (p1 - 1) * (p2 - 1);

        refValue x = new refValue(privateKey);
        refValue y = new refValue();

        for (int i = 3; i < m; i += 1331) {
            int gcd = exec(i, m, x, y);
            if (gcd == 1 && x.value > 0) {
                this.publicKey = i;
                this.privateKey = x.value;
                this.product = y.value;
                break;
            }
        }
    }

    long fastPow(long a, int key, int product) {
        long total = 1;
        while (key != 0) {
            if ((key & 1) != 0) total = (total * a) % product;
            a = (a * a) % product;
            key >>= 1;
        }
        return total;
    }

//    ArrayList<Integer> fastRSAEncrypt(ArrayList<Integer> raw) {
//        ArrayList<Integer> encrypted = new ArrayList<Integer>();
//        for (Integer integer : raw) {
//            encrypted.add((int) fastPow(integer, this.publicKey, product));
//        }
//        return encrypted;
//    }
//
//    ArrayList<Integer> fastRSADecrypt( ArrayList<Integer> encrypted) {
//        ArrayList<Integer> decrypted = new ArrayList<Integer>();
//        for (Integer integer : encrypted) {
//            encrypted.add((int) fastPow(integer, this.privateKey, product));
//        }
//        return decrypted;
//    }

    String encrypt(ArrayList<Byte> raw) {
        byte[] encrypted = new byte[raw.size() * 4];
        int i = 0;
        for (Byte d : raw) {
            long value = fastPow(d, publicKey, product);
            encrypted[i++] = (byte) (value & 0xFF);
            encrypted[i++] = (byte) ((value & 0xFF00) >> 8);
            encrypted[i++] = (byte) ((value & 0xFF0000) >> 16);
            encrypted[i++] = (byte) ((value & 0xFF000000) >> 24);
        }
        return new String(base64j.encode(encrypted));
    }

    byte[] decrypt(String base64) {
        byte[] raw = base64j.decode(base64);
        byte[] decrypted = new byte[raw.length / 4];
        int j = 0;
        for (int i = 0; i < raw.length; i += 4) {
            long value = (raw[i] & 0xFF) |
                    (raw[i + 1] & 0xFF) << 8 |
                    (raw[i + 2] & 0xFF) << 16 |
                    (raw[i + 3] & 0xFF) << 24;
            decrypted[j++] = (byte) fastPow(value, privateKey, product);
        }
        return decrypted;
    }

    public static int rsaMain() {
        rsaLite4j rsa = new rsaLite4j();
        rsa.publicKey = 5327;
        rsa.privateKey = 43763;
        rsa.product = 110189;
        System.out.printf("Public Key (publicKey, product) : (%s, %s).\n", rsa.publicKey.toString(), rsa.product.toString());
        System.out.printf("Private Key (privateKey, product) : (%s, %s).\n\n", rsa.privateKey.toString(), rsa.product.toString());

        ArrayList<Byte> raw = new ArrayList<Byte>();
        raw.add((byte) 's');
        raw.add((byte) 't');
        raw.add((byte) 'r');
        String encrypted = rsa.encrypt(raw);
        byte[] decrypted = rsa.decrypt(encrypted);

        String s = "";
        for (byte b : decrypted) {
            s += (char) (b & 0xFF);
        }
        return 0;
    }


}
