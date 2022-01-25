package life.unwitting;

import org.apache.commons.codec.binary.Base64;
// import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


public class rsaUtils4j {
    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA"; // ALGORITHM ['ælgərɪð(ə)m] 算法的意思

    public static Map<String, String> createKeys(int keySize) {
        // 为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        // 初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        // 生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        // 得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        // 得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        // map装载公钥和私钥
        Map<String, String> keyPairMap = new HashMap<String, String>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);
        // 返回map
        return keyPairMap;
    }

    /**
     * 得到公钥
     * @param publicKey  密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私钥
     * @param privateKey  密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 公钥加密
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            //每个Cipher初始化方法使用一个模式参数opmod，并用此模式初始化Cipher对象。此外还有其他参数，包括密钥key、包含密钥的证书certificate、算法参数params和随机源random。
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    //rsa切割解码  , ENCRYPT_MODE,加密数据   ,DECRYPT_MODE,解密数据
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock = 0;  //最大块
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    //可以调用以下的doFinal（）方法完成加密或解密数据：
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        // IOUtils.closeQuietly(out);
        return resultDatas;
    }


    // 简单测试____________
    public static void main(String[] args) throws Exception {
        // 公钥:
        // MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrOsTFYdFodDcgKAswvT0ImBQDP0dXSQiocPBEKv_VCeGe0xpvy0AxPchmC8JF-6yT6h_1xN7wns1HVjOeTsZYgeIMFINq7rKVYRwhcZuijlvsy3KgcHySEdrVAH0XX_wUO_CTLUCK7zTPK8MTIMlmUru_rEddxjQj1kOyXwkAcQIDAQAB
        // 私钥：
        // MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKs6xMVh0Wh0NyAoCzC9PQiYFAM_R1dJCKhw8EQq_9UJ4Z7TGm_LQDE9yGYLwkX7rJPqH_XE3vCezUdWM55OxliB4gwUg2ruspVhHCFxm6KOW-zLcqBwfJIR2tUAfRdf_BQ78JMtQIrvNM8rwxMgyWZSu7-sR13GNCPWQ7JfCQBxAgMBAAECgYA_JDnBa5PUB974Hawauf3SuHnQxLnjBwyJSLRg_tY-Uhl__uqlxPaT8et_LeDi-66lENXeRE3Nv1GFd9UrEHN1PcekYbGLm59MaEEyzuENzw3_ebCjPwStqXHs0iBvtJqJfVfdfGQ4UatlVe15cBQYGiyRZnutBKp6TGmSJd48AQJBAPJPgM0SeOE2JetqVqIHr3Nl3FQNhrZhn3btHJqcj4dJXUaR0X725r_0lkzEU58-p5WfrtpmhGsRCK_IKlLgBbECQQC05zuoqgRl3TZbm7SLK4nEjnWnU6z-cPSNkI1r-dQSrm8jtegZR5bhiIpLs7mRb7W-EQWRz7GY1fqm_LIuf5bBAkAMe2v3JGx1rYRmjCPKVPsExsqCye-WlZDRX4WJsWbzYSQc7oYMNEvOt5rGeZaVLXUUkBVByuYnXclExNrpSgEBAkAgE-SjSsqO2YP7CIgiKW07eltoeVDyqUrkE17QZ7NwQJgah_YJDYW2pkSHme3s2RcsBc6sBV0jkcXpeGOMFTnBAkARsgWRyAOGnbj2uwMepNqkXoT7NH8wblLy51CBrjk5VuiDndxd1pVuKssmgQknhYkRMhHPG29WcwAZ1iCSDsH2

        Map<String, String> keyMap = rsaUtils4j.createKeys(1024);
        String publicKey = keyMap.get("publicKey");
        String privateKey = keyMap.get("privateKey");
        System.out.println("公钥: \n\r" + publicKey);
        System.out.println("私钥： \n\r" + privateKey);

        System.out.println("公钥加密——私钥解密");
        String str = "站在大明门前守卫的禁卫军，事先没有接到\n" + "有关的命令，但看到大批盛装的官员来临，也就\n" + "以为确系举行大典，因而未加询问。进大明门即\n" + "为皇城。文武百官看到端门午门之前气氛平静，\n" + "城楼上下也无朝会的迹象，既无几案，站队点名\n" + "的御史和御前侍卫“大汉将军”也不见踪影，不免\n"
                + "心中揣测，互相询问：所谓午朝是否讹传？";
        System.out.println("\r明文：\r\n" + str);
        System.out.println("\r明文大小：\r\n" + str.getBytes().length);
        String encodedData = rsaUtils4j.publicEncrypt(str, rsaUtils4j.getPublicKey(publicKey));  //传入明文和公钥加密,得到密文

        // lx-1rVjtKeDKtCQtlByABQI4YfUsEyXiLerWhPeqP8-1xj0hvpKsntGseDdfWUNVSdPO9e-La0NTUeur3K7SI7hmVrYlFH_BSItMegq731Twu892eWNjJgn7LSqc2E4qv3YuEEj8JRYLWxgXg6_h1Vkb0lHEuCB9JQTd3GeEkDxIx1QwVbx_sPxbawJCdm-atfwYvs_qKOH8HAla33Ovz0Fvgn1WTFZCkUOTDQbM3UVOYjkJ9Guf83NWYs_P0B9xC75WG-TUrUkf5LZGCD1tYMtaYy-f6fuAOPxNxnSWNqJ6e5_I-e2sBtgbgnR0J9lp6JxKFhHLtkl-0wCunzKgNngHJe3S1Crbec0yKuNmAJ_AjQb-Wceg9GeKknKnDyBIHFAdDL7C9L9NpX_hCRM8f1VlcJJjDkASZZbZTlmPF8m1WSROVWSzZcYAZi_YOkK0TrHej-iiIsUpY8WzWXsyl4XXxCIJCHmm-7hthE5Qknl3Bfvjm4071T_WwPv2Kkk5pyLUDZrwrAG4BTTszJEx7YTGLdwMlYctINxQhVIMn5BqCRHzs8ihlDeCley1yLbPniXRx1SBuDx3lAi_m5F3L-0LZD5HRVbXwrifMPiI-ULsGuT32c_AkhUG0oQJzHpcv3m9n4aVTrhy05l-t8Ea3-QYfzxFQsfGdhtmrwzLOBo
        System.out.println("密文：\r\n" + encodedData);
        String decodedData = rsaUtils4j.privateDecrypt(encodedData, rsaUtils4j.getPrivateKey(privateKey)); //传入密文和私钥,得到明文
        System.out.println("解密后文字: \r\n" + decodedData);

    }

}