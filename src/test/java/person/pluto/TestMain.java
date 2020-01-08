package person.pluto;

import java.security.Key;

import com.alibaba.fastjson.JSON;

import person.pluto.natcross2.common.CommonFormat;
import person.pluto.natcross2.model.InteractiveModel;
import person.pluto.natcross2.model.SecretInteractiveModel;
import person.pluto.natcross2.model.enumeration.InteractiveTypeEnum;
import person.pluto.natcross2.model.enumeration.NatcrossResultEnum;
import person.pluto.natcross2.utils.AESUtil;

public class TestMain {

    public static void main(String[] args) throws Exception {
        InteractiveModel interactiveModel = InteractiveModel.of(CommonFormat.getInteractiveSeq(),
                InteractiveTypeEnum.COMMON_REPLY, NatcrossResultEnum.NO_HAS_SERVER_LISTEN.toResultModel());
        SecretInteractiveModel secretInteractiveModel = new SecretInteractiveModel(interactiveModel);
        System.out.println(secretInteractiveModel);

        Key createKey = AESUtil.createKeyByBase64("0PMudFSqJ9WsQrTC60sva9sJAV4PF5iOBjKZW17NeF4=");

        secretInteractiveModel.fullMessage(createKey, "tokenKey");

        System.out.println(secretInteractiveModel);
        String jsonString = secretInteractiveModel.toJSONString();

        SecretInteractiveModel parseObject = JSON.parseObject(jsonString, SecretInteractiveModel.class);
        System.out.println(parseObject);

        System.out.println(parseObject.checkAutograph("tokenKey"));

        parseObject.decryptMsg(createKey);
        System.out.println(parseObject);
    }

}
