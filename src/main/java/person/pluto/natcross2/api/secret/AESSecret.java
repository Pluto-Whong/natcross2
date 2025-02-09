package person.pluto.natcross2.api.secret;

import lombok.Data;
import person.pluto.natcross2.utils.AESUtil;

import java.security.Key;

/**
 * <p>
 * AES加密方式
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:01:40
 */
@Data
public class AESSecret implements ISecret {

    private Key aesKey;

    @Override
    public byte[] encrypt(byte[] content, int offset, int len) throws Exception {
        return AESUtil.encrypt(this.aesKey, content, offset, len);
    }

    @Override
    public byte[] decrypt(byte[] result) throws Exception {
        return AESUtil.decrypt(this.aesKey, result);
    }

    /**
     * 设置密钥
     *
     * @param aesKey
     */
    public void setBaseAesKey(String aesKey) {
        this.aesKey = AESUtil.createKeyByBase64(aesKey);
    }

}
