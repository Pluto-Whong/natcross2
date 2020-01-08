package person.pluto.natcross2.api.secret;

import java.security.Key;

import lombok.Data;
import person.pluto.natcross2.utils.AESUtil;

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
        return AESUtil.encrypt(aesKey, content, offset, len);
    }

    @Override
    public byte[] decrypt(byte[] result) throws Exception {
        return AESUtil.decrypt(aesKey, result);
    }

    public void setBaseAesKey(String aesKey) {
        this.aesKey = AESUtil.createKeyByBase64(aesKey);
    }

}
