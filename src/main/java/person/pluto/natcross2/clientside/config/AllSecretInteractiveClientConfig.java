package person.pluto.natcross2.clientside.config;

import lombok.Getter;
import lombok.Setter;
import person.pluto.natcross2.api.secret.AESSecret;
import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.api.socketpart.SecretSocketPart;
import person.pluto.natcross2.clientside.ClientControlThread;
import person.pluto.natcross2.utils.AESUtil;

import java.security.Key;

/**
 * <p>
 * 交互及隧道都加密
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 15:01:44
 */
public class AllSecretInteractiveClientConfig extends SecretInteractiveClientConfig {

    @Setter
    @Getter
    private Key passwayKey;

    @Override
    public AbsSocketPart newSocketPart(ClientControlThread clientControlThread) {
        AESSecret secret = new AESSecret();
        secret.setAesKey(this.passwayKey);
        SecretSocketPart secretSocketPart = new SecretSocketPart(clientControlThread);
        secretSocketPart.setSecret(secret);
        return secretSocketPart;
    }

    /**
     * base64格式设置密钥
     *
     * @param key
     */
    public void setBasePasswayKey(String key) {
        this.passwayKey = AESUtil.createKeyByBase64(key);
    }

}
