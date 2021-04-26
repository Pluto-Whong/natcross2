package person.pluto.natcross2.api.secret;

/**
 * 
 * <p>
 * 加密方法
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:01:28
 */
public interface ISecret {

	/**
	 * 加密数据
	 *
	 * @param content
	 * @param offset
	 * @param len
	 * @return
	 * @throws Exception
	 * @author Pluto
	 * @since 2021-04-26 16:38:46
	 */
	byte[] encrypt(byte[] content, int offset, int len) throws Exception;

	/**
	 * 解密数据
	 *
	 * @param result
	 * @return
	 * @throws Exception
	 * @author Pluto
	 * @since 2021-04-26 16:39:07
	 */
	byte[] decrypt(byte[] result) throws Exception;

}
