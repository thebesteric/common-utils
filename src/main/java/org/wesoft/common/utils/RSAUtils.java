package org.wesoft.common.utils;

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSAUtils
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2020-06-05 14:06
 * @since 1.0
 */
public class RSAUtils {

    public static String extractFromPem(String rawKey) {
        return rawKey.replaceAll("\\n", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "");
    }

    /**
     * 生成密钥对
     */
    public static Map<String, Object> genKeyPair(int keySize) throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(keySize);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<>(2);
        keyMap.put("RSAPublicKey", publicKey);
        keyMap.put("RSAPrivateKey", privateKey);
        return keyMap;
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       已加密数据
     * @param privateKey 私钥
     */
    public static String signature(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance("SHA1withRSA"); // SIGNATURE_ALGORITHM
        signature.initSign(privateK);
        signature.update(data);
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data      已加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("SHA1withRSA");    //SIGNATURE_ALGORITHM
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.getDecoder().decode(sign));
    }

    /**
     * 私钥解密
     *
     * @param encryptedData   已加密数据
     * @param privateKey      私钥
     * @param maxDecryptBlock RSA最大解密密文大小，注意：这个和密钥长度有关系， 公式 = 密钥长度 / 8
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey, int maxDecryptBlock) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        return getDecryptedData(encryptedData, maxDecryptBlock, keyFactory, privateK);
    }

    /**
     * 公钥解密
     *
     * @param encryptedData   已加密数据
     * @param publicKey       公钥
     * @param maxDecryptBlock RSA最大解密密文大小，注意：这个和密钥长度有关系， 公式 = 密钥长度 / 8
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey, int maxDecryptBlock) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        return getDecryptedData(encryptedData, maxDecryptBlock, keyFactory, publicK);
    }

    private static byte[] getDecryptedData(byte[] encryptedData, int maxDecryptBlock, KeyFactory keyFactory, Key publicK) throws Exception {
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxDecryptBlock) {
                cache = cipher.doFinal(encryptedData, offSet, maxDecryptBlock);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxDecryptBlock;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > 117) {
                cache = cipher.doFinal(data, offSet, 117);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * 117;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > 117) {
                cache = cipher.doFinal(data, offSet, 117);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * 117;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    public static String getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get("RSAPrivateKey");
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get("RSAPublicKey");
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    private static String decryptRSA1(String str, String privateKey, String charsetName) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        rsa.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
        byte[] bytes = rsa.doFinal(Base64.getDecoder().decode(str));
        String result = new String(bytes, charsetName);
        return result;
    }

    private static PrivateKey getPrivateKey(String privateKey) throws Exception {
        Reader privateKeyReader = new StringReader(privateKey);
        PEMParser privatePemParser = new PEMParser(privateKeyReader);
        Object privateObject = privatePemParser.readObject();
        if (privateObject instanceof PEMKeyPair) {
            PEMKeyPair pemKeyPair = (PEMKeyPair) privateObject;
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            return converter.getPrivateKey(pemKeyPair.getPrivateKeyInfo());
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        // Map<String, Object> keyMap = genKeyPair(2048);
        // String publicKey = getPublicKey(keyMap);
        // byte[] encryptData = encryptByPublicKey("这是加密数据".getBytes(), publicKey);

        String encryptData = "Mn9t7hz7fnjefyz7wTT68ahd0K4GvT3bE8XwoFywqa2XJUQ3WW1raE4DE5UanJvpvC20jwmWJnJCVa6BSQTz4QgHMNz/MLUbjHrXGhR5RW8TES3+9cidxL0niHoU6j2EwB7hoP0kjzWH/42fb8JvSuhzoghvE8bcXy78xUZ7nGWOFFfFdyHKm9QApdzCUpVewul2Wl7xoSaabwzb84rfsX/fOMqIChlbpu3/c/9XHhQqdnY4+9k4OKcSY/YgXSlNtAqaI6H2i7hMDorrOVm7dBe8SQA0/JR/jZYyaXbFrz6IO9cQeK58TMEM7k50U3HjElcnzdHFwXfqYzdK+y+Slg==";
        String priKey = "-----BEGIN PRIVATE KEY-----\n" +
                        "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC6lAPbXe5r8gC0\n" +
                        "wzLHeI0AoQkkGqozH6mhld7t/m0M/e4pP2HdlznA5GvvtVYa+/cPa3LWeOVV9A3F\n" +
                        "3mzER2og2wWko/qaPLiHlLE98My/rglV9ZLnX77d8ADqUkrE34IwfKLXESBrZYXd\n" +
                        "881fnLiH+4o4uaLs6v/P6cpA+XwM1gbEQ0kyz2FxTK+e2J9hpFGpYlsMUv1Js77h\n" +
                        "tLeYjYRDyBVjxrWmjEG11obvvz6hjbKellm5zsT+31jXiUHByJsZxhP4k0OYAaoO\n" +
                        "hVBp8BWgwrmbDhiIbFiZbJe4ecCvAjr8HQdPdDr5qZkGbjrfTE/LS4T1RDmy75Lc\n" +
                        "y+/03zeXAgMBAAECggEANpkdz9Ha9UeqITuF3o1OZcK6FBChgbhgQWLEC80KSGT+\n" +
                        "jp93mgqo4eN2Gn1sJxVYpPCCeHSEV/TFGwQNvBpR2i99pUI/EWd3cGV4wHF42Raq\n" +
                        "sLbeOEryZavX+LoAG0B0ro+qb1kZrZPV6q9vh+0qqtBXEGZyYPYbydHWV9EqFe8D\n" +
                        "eV36ddD/BoO8q0kIBe957MHKju5BlBPiTnKtJMkDOydpgcUtr5wPILFZsodIDKTZ\n" +
                        "+NjlCsbOQty/KyF7FNF/V27s1VGID/lbcwFIxnzs9b6tzfgxSPEGYvzpD66m22y9\n" +
                        "rUKQ7+OYDP3ZHY2I7icuSdTQrGCcIHb+zfPCXpyvAQKBgQDpRk29c/MuRPkU7CwP\n" +
                        "UoBc0S2CfZv771aDDftZJZlD+so7BWx0Aaejd0oEkaZmylxa4sFSMZBXdg40yVbN\n" +
                        "AtpephnTAT1zNCZNC9An22MUBz8GXEAvyGXBqMdMCjXJ3OCVuqx0PLHCuTYMbGYi\n" +
                        "ogh061dRo1u3CRLjICW3t0AP1wKBgQDMwSMDfEjR3kDtVsk+rEmFdeJcTH6q0p2m\n" +
                        "KGUJqm3NssHvfzwEBC3P6vrlrtfasr0sunxXtV0/1UqIaX44U3CR7I3y5m5UkyU+\n" +
                        "CXPHn0RsfI2BC7uGVDD+ax2KF0vb/TMQaxxlOl8MyZi122C5HwP9P1QXHJp3tAuh\n" +
                        "R/Kz+kAeQQKBgQDchz4PEIKhB0uCYBL3GUOIe6fOaPkBny3mD13C1Syje3+bdHEm\n" +
                        "6jmU67zpvNfW7Q+KTDv+fCS8yjp75KtOfJf8SoK3W8DAHFjpFXXfeCOeV4tZc/Sq\n" +
                        "N4UbI3flhvF6uHkIk8VczdQvBfD2pbOYoh3kimeuIgUecFGOrmF54J+LWQKBgQCj\n" +
                        "JUT70vzG1DuRoPQLDwYp5Csv9fd7rwPUUB1pnTWOj999MDkmzhmesJGnjzAI3yBi\n" +
                        "0lifHjCl2xJfLQdYZAuvCfh00WyZ0QD8tFVfeqTveDLb3vhVaSH8pmbMEBHuwIn+\n" +
                        "jAn9EoyHyN34EUu5VWA9WkdrfuwJcxlNMeHNzBlowQKBgQCRkEW4fZV2FFtHJMXb\n" +
                        "lMK3EJxdZlQYC/kLTNVWfdzvh6vNRtOd6DokRJrYKfRQDVYtflLdZ54bUoT8ZTru\n" +
                        "eHvXBckrUg0wVJ4N7Jy1kJRYHe0v0BwQvymw39evJSgoYz58P7ID5s6J18b7DJHd\n" +
                        "IhbdQ6y/AcObc/9rGHk4IzQ1PA==\n" +
                        "-----END PRIVATE KEY-----\n";

        byte[] decryptData = decryptByPrivateKey(Base64.getDecoder().decode(encryptData), extractFromPem(priKey), 256);
        System.out.println(new String(decryptData, "utf-8"));

    }

}
