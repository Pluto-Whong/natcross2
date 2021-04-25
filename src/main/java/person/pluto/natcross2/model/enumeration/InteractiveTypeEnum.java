package person.pluto.natcross2.model.enumeration;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * <p>
 * 交互类型enum
 * </p>
 *
 * @author Pluto
 * @since 2019-07-17 09:50:33
 */
public enum InteractiveTypeEnum {
	//
	UNKNOW("未知"),
	//
	COMMON_REPLY("通用回复标签"),
	//
	HEART_TEST("发送心跳"),
	//
	SERVER_WAIT_CLIENT("需求客户端建立连接"),
	//
	CLIENT_CONNECT("客户端建立通道连接"),
	//
	CLIENT_CONTROL("客户端控制端口建立连接"),
	//
	;

	private String describe;

	InteractiveTypeEnum(String describe) {
		this.describe = describe;
	}

	public static InteractiveTypeEnum getEnumByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		for (InteractiveTypeEnum e : InteractiveTypeEnum.values()) {
			if (e.name().equals(name)) {
				return e;
			}
		}
		return null;
	}

	public String getDescribe() {
		return describe;
	}
}
