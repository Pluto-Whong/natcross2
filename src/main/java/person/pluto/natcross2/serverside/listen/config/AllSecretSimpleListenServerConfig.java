package person.pluto.natcross2.serverside.listen.config;

import lombok.Getter;
import lombok.Setter;
import person.pluto.natcross2.api.secret.AESSecret;
import person.pluto.natcross2.api.socketpart.AbsSocketPart;
import person.pluto.natcross2.api.socketpart.SecretSocketPart;
import person.pluto.natcross2.serverside.listen.ServerListenThread;
import person.pluto.natcross2.utils.AESUtil;

import java.security.Key;

/**
 * <p>
 * 交互及隧道都加密
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 15:05:55
 */
public class AllSecretSimpleListenServerConfig extends SecretSimpleListenServerConfig {

    @Setter
    @Getter
    private Key passwayKey;

    public AllSecretSimpleListenServerConfig(Integer listenPort) {
        super(listenPort);
    }

    @Override
    public AbsSocketPart newSocketPart(ServerListenThread serverListenThread) {
        AESSecret secret = new AESSecret();
        secret.setAesKey(this.passwayKey);
        SecretSocketPart secretSocketPart = new SecretSocketPart(serverListenThread);
        secretSocketPart.setSecret(secret);
        return secretSocketPart;
    }

    /**
     * BASE64格式设置隧道加密密钥
     *
     * @param key
     */
    public void setBasePasswayKey(String key) {
        this.passwayKey = AESUtil.createKeyByBase64(key);
    }

}
