package person.pluto;

import java.security.Key;
import java.util.Base64;

import person.pluto.natcross2.utils.AESUtil;

public class TestMain {

    public static void main(String[] args) throws Exception {
        Key createKey = AESUtil.createKey(128);
        System.out.println(Base64.getEncoder().encodeToString(createKey.getEncoded()));
    }

}
