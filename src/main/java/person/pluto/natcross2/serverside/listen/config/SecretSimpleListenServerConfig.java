package person.pluto.natcross2.serverside.listen.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import person.pluto.natcross2.channel.SecretInteractiveChannel;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.utils.AESUtil;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;

/**
 * <p>
 * 交互加密配置
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:52:51
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SecretSimpleListenServerConfig extends SimpleListenServerConfig {

    private String tokenKey;
    private Key aesKey;

    public SecretSimpleListenServerConfig(Integer listenPort) {
        super(listenPort);
    }

    @Override
    protected SocketChannel<? extends InteractiveModel, ? super InteractiveModel> newControlSocketChannel(
            Socket socket) {
        SecretInteractiveChannel channel = new SecretInteractiveChannel();
        channel.setCharset(this.getCharset());
        channel.setTokenKey(this.tokenKey);
        channel.setAesKey(this.aesKey);
        try {
            channel.setSocket(socket);
        } catch (IOException e) {
            return null;
        }
        return channel;
    }

    /**
     * BASE64格式设置交互加密密钥
     *
     * @param aesKey
     */
    public void setBaseAesKey(String aesKey) {
        this.aesKey = AESUtil.createKeyByBase64(aesKey);
    }

}
