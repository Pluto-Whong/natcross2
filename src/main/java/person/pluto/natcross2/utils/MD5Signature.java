package person.pluto.natcross2.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

/**
 * <p>
 * MD5散列签名
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 10:13:50
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MD5Signature {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final String RANDOMBASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    private static final String HEXBASE = "0123456789abcdef";

    /**
     * 转换为16进制字符
     *
     * @param bytes
     * @return
     * @author Pluto
     * @since 2019-12-05 12:34:48
     */
    public static String toHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder(bytes.length << 1);

        for (byte tmp : bytes) {
            stringBuilder.append(HEXBASE.charAt(tmp >> 4 & 0xf));
            stringBuilder.append(HEXBASE.charAt(tmp & 0xf));
        }
        return stringBuilder.toString();
    }

    /**
     * 获取随机数
     *
     * @param count
     * @return
     * @author Pluto
     * @since 2019-12-05 11:20:35
     */
    public static String getRandomStr(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            sb.append(RANDOMBASE.charAt(RANDOM.nextInt(RANDOMBASE.length())));
        }
        return sb.toString();
    }

    /**
     * integer 转换为 byte[]
     *
     * @param source
     * @return
     * @author Pluto
     * @since 2019-12-05 11:20:53
     */
    public static byte[] intToBytes(int source) {
        return new byte[]{(byte) ((source >> 24) & 0xFF), (byte) ((source >> 16) & 0xFF), (byte) ((source >> 8) & 0xFF),
                (byte) (source & 0xFF)};
    }

    /**
     * byte[] 转 integer
     *
     * @param byteArr
     * @return
     * @author Pluto
     * @since 2019-12-05 11:21:07
     */
    public static int bytes2int(byte[] byteArr) {
        int count = 0;

        for (int i = 0; i < 4; ++i) {
            count <<= 8;
            count |= byteArr[i] & 255;
        }

        return count;
    }

    public static String getSignature(String... params) {
        return getSignature(CHARSET, params);
    }

    /**
     * 对参数进行MD5散列
     *
     * @param params
     * @return
     * @throws NoSuchAlgorithmException
     * @author Pluto
     * @since 2019-12-05 12:35:52
     */
    public static String getSignature(Charset charset, String... params) {
        Arrays.sort(params);
        StringBuilder stringBuffer = new StringBuilder();

        for (int i = 0; i < 4; ++i) {
            stringBuffer.append(params[i]);
        }

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        byte[] digest = md.digest(stringBuffer.toString().getBytes(charset));

        return toHexString(digest);
    }

}
