package person.pluto.natcross2.serverside.client.config;

import java.net.Socket;
import java.security.Key;

import lombok.Data;
import lombok.EqualsAndHashCode;
import person.pluto.natcross2.channel.SecretInteractiveChannel;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.utils.AESUtil;

/**
 * 
 * <p>
 * 隧道过程加密的配置类
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:44:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SecretSimpleClientServiceConfig extends SimpleClientServiceConfig {

    /**
     * 签名混淆key
     */
    private String tokenKey;
    /**
     * 隧道过程加密key AES
     */
    private Key aesKey;

    public SecretSimpleClientServiceConfig(Integer listenPort) {
        super(listenPort);
    }

    @Override
    public SocketChannel<? extends InteractiveModel, ? super InteractiveModel> newSocketChannel(Socket listenSocket)
            throws Exception {
        SecretInteractiveChannel channel = new SecretInteractiveChannel();
        channel.setCharset(this.getCharset());
        channel.setTokenKey(tokenKey);
        channel.setAesKey(aesKey);
        channel.setSocket(listenSocket);
        return channel;
    }

    /**
     * BASE64格式设置隧道加密密钥
     * 
     * @author Pluto
     * @since 2020-01-08 16:45:25
     * @param aesKey
     */
    public void setBaseAesKey(String aesKey) {
        this.aesKey = AESUtil.createKeyByBase64(aesKey);
    }

}
