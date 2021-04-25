package person.pluto.natcross2.clientside.config;

import java.io.IOException;
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
 * 交互加密的配置方案（AES加密）
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:32:18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SecretInteractiveClientConfig extends InteractiveClientConfig {

    private String tokenKey;
    private Key aesKey;

    @Override
    public SocketChannel<? extends InteractiveModel, ? super InteractiveModel> newClientChannel() {
        SecretInteractiveChannel channel = new SecretInteractiveChannel();

        channel.setCharset(this.getCharset());
        channel.setTokenKey(this.tokenKey);
        channel.setAesKey(this.aesKey);

        try {
            Socket socket = new Socket(this.getClientServiceIp(), this.getClientServicePort());
            channel.setSocket(socket);
        } catch (IOException e) {
            return null;
        }
        return channel;
    }

    /**
     * 设置交互密钥
     * 
     * @author Pluto
     * @since 2020-01-08 16:32:37
     * @param aesKey
     */
    public void setBaseAesKey(String aesKey) {
        this.aesKey = AESUtil.createKeyByBase64(aesKey);
    }

}
