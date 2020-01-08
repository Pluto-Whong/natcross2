package person.pluto.natcross2.utils;

/**
 * 
 * <p>
 * 无归类的工具集
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:56:35
 */
public final class Tools {

    /**
     * integer 转换为 byte[]
     *
     * @param source
     * @return
     * @author Pluto
     * @since 2019-12-05 11:20:53
     */
    public static byte[] intToBytes(int source) {
        return new byte[] { (byte) ((source >> 24) & 0xFF), (byte) ((source >> 16) & 0xFF),
                (byte) ((source >> 8) & 0xFF), (byte) (source & 0xFF) };
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

}
