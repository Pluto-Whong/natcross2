package person.pluto.natcross2.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * <p>
 * AES加解密工具
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:56:05
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AESUtil {

    /**
     * 根据base64获取aes密钥
     *
     * @param baseKey
     * @return
     */
    public static Key createKeyByBase64(String baseKey) {
        byte[] keyBytes = Base64.getDecoder().decode(baseKey);
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * 生成随机密钥
     *
     * @param length
     * @return
     */
    public static Key createKey(int length) {
        try {
            // 生成key
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            // 生成一个length位的随机源,根据传入的字节数组
            keyGenerator.init(length);
            // 产生原始对称密钥
            SecretKey secretKey = keyGenerator.generateKey();
            // 获得原始对称密钥的字节数组
            byte[] keyBytes = secretKey.getEncoded();
            // key转换,根据字节数组生成AES密钥
            return new SecretKeySpec(keyBytes, "AES");
        } catch (NoSuchAlgorithmException e) {
            log.error("create aes key exception", e);
            return null;
        }
    }

    /**
     * 加密并转换为base64，默认加密工作方式 ECB/PKCS5Padding
     *
     * @param key
     * @param content
     * @return
     * @throws Exception
     */
    public static String encryptBase64(Key key, byte[] content) throws Exception {
        byte[] encrypt = encrypt(key, content);
        return Base64.getEncoder().encodeToString(encrypt);
    }

    /**
     * 加密，默认加密工作方式 ECB/PKCS5Padding
     *
     * @param key
     * @param content
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(Key key, byte[] content) throws Exception {
        return encrypt(key, content, 0, content.length);
    }

    /**
     * 截断，默认加密
     *
     * @param key
     * @param content
     * @param inputOffset
     * @param inputLen
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(Key key, byte[] content, int inputOffset, int inputLen) throws Exception {
        return encrypt(key, content, "ECB/PKCS5Padding", inputOffset, inputLen);
    }

    /**
     * 无截断加密
     *
     * @param key
     * @param content
     * @param encryptMethod
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(Key key, byte[] content, String encryptMethod) throws Exception {
        return encrypt(key, content, encryptMethod, 0, content.length);
    }

    /**
     * 对数据进行加密
     *
     * @param key
     * @param content
     * @param encryptMethod 加解密工作方式，如 AES/ECB/PKCS5Padding
     *                      只写ECB/PKCS5Padding，前面的AES/已被写死
     * @param inputOffset
     * @param inputLen
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(Key key, byte[] content, String encryptMethod, int inputOffset, int inputLen)
            throws Exception {
        Cipher cipher = Cipher.getInstance("AES/" + encryptMethod);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(content, inputOffset, inputLen);
    }

    /**
     * 解密BASE64，默认解密工作方式 ECB/PKCS5Padding
     *
     * @param key
     * @param result
     * @return
     * @throws Exception
     */
    public static byte[] decryptBase64(Key key, String result) throws Exception {
        byte[] decode = Base64.getDecoder().decode(result);
        return decrypt(key, decode);
    }

    /**
     * 解密，默认解密工作方式 ECB/PKCS5Padding
     *
     * @param key
     * @param result
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(Key key, byte[] result) throws Exception {
        return decrypt(key, result, "ECB/PKCS5Padding");
    }

    /**
     * 对数据进行解密
     *
     * @param key
     * @param result
     * @param decryptMethod 加解密工作方式，如 AES/ECB/PKCS5Padding
     *                      只写ECB/PKCS5Padding，前面的AES已被写死
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(Key key, byte[] result, String decryptMethod) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/" + decryptMethod);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(result);
    }
}
