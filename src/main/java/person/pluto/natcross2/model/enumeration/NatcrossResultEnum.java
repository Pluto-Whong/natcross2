package person.pluto.natcross2.model.enumeration;

import org.apache.commons.lang3.StringUtils;

import person.pluto.natcross2.model.NatcrossResultModel;

/**
 * <p>
 * 客户端服务端返回码
 * </p>
 *
 * @author Pluto
 * @since 2019-03-28 10:59:53
 */
public enum NatcrossResultEnum {
	// 成功
	SUCCESS("1000", "成功"),
	//
	UNKNOW_INTERACTIVE_TYPE("3001", "未知的通信类型"),
	//
	NO_HAS_SERVER_LISTEN("3002", "不存在请求的监听接口"),
	// 未知错误
	FAIL("9999", "未知错误");

	private String code;
	private String name;

	private NatcrossResultEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static NatcrossResultEnum getEnumByCode(String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		for (NatcrossResultEnum e : NatcrossResultEnum.values()) {
			if (e.code.equals(code)) {
				return e;
			}
		}
		return null;
	}

	public NatcrossResultModel toResultModel() {
		return NatcrossResultModel.of(this);
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
