package person.pluto.natcross2.serverside.listen.config;

import java.io.IOException;
import java.security.Key;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import person.pluto.natcross2.channel.SecretInteractiveChannel;
import person.pluto.natcross2.channel.SocketChannel;
import person.pluto.natcross2.serverside.listen.control.ControlSocket;
import person.pluto.natcross2.serverside.listen.control.IControlSocket;
import person.pluto.natcross2.utils.AESUtil;

/**
 * 
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
    public IControlSocket newControlSocket(SocketChannel<?, ?> socketChannel, JSONObject config) {
        SecretInteractiveChannel channel = new SecretInteractiveChannel();
        channel.setCharset(this.getCharset());
        channel.setTokenKey(tokenKey);
        channel.setAesKey(aesKey);
        try {
            channel.setSocket(socketChannel.getSocket());
        } catch (IOException e) {
            // do no thing
        }
        return new ControlSocket(channel);
    }

    /**
     * BASE64格式设置交互加密密钥
     * 
     * @author Pluto
     * @since 2020-01-08 16:52:25
     * @param key
     */
    public void setBaseAesKey(String aesKey) {
        this.aesKey = AESUtil.createKeyByBase64(aesKey);
    }

}
