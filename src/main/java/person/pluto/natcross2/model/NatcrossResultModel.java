package person.pluto.natcross2.model;

import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import person.pluto.natcross2.model.enumeration.NatcrossResultEnum;

import java.lang.reflect.Field;

/**
 * <p>
 * 常规类型的前后端返回model
 * </p>
 *
 * @author Pluto
 * @since 2019-03-28 10:45:41
 */
@Data
@Slf4j
public class NatcrossResultModel implements JSONAware {

    public static NatcrossResultModel of(String retCode, String retMsg, Object object) {
        return new NatcrossResultModel(retCode, retMsg, object);
    }

    public static NatcrossResultModel of(NatcrossResultEnum resultEnum, Object data) {
        return new NatcrossResultModel(resultEnum.getCode(), resultEnum.getName(), data);
    }

    public static NatcrossResultModel of(NatcrossResultEnum resultEnum) {
        return new NatcrossResultModel(resultEnum.getCode(), resultEnum.getName(), null);
    }

    public static NatcrossResultModel ofFail(Object data) {
        return new NatcrossResultModel(NatcrossResultEnum.FAIL.getCode(), NatcrossResultEnum.FAIL.getName(), data);
    }

    public static NatcrossResultModel ofFail() {
        return new NatcrossResultModel(NatcrossResultEnum.FAIL.getCode(), NatcrossResultEnum.FAIL.getName(), null);
    }

    public static NatcrossResultModel ofSuccess(Object data) {
        return new NatcrossResultModel(NatcrossResultEnum.SUCCESS.getCode(), NatcrossResultEnum.SUCCESS.getName(),
                data);
    }

    public static NatcrossResultModel ofSuccess() {
        return new NatcrossResultModel(NatcrossResultEnum.SUCCESS.getCode(), NatcrossResultEnum.SUCCESS.getName(),
                null);
    }

    private String retCod;
    private String retMsg;
    private Object data;

    public NatcrossResultModel(String retCod, String retMsg, Object data) {
        this.retCod = retCod;
        this.retMsg = retMsg;
        this.data = data;
    }

    /**
     * 反射方式修改值
     *
     * @author Pluto
     * @since 2019-05-10 14:04:48
     * @param fieldStr
     * @param object
     * @return
     */
    public NatcrossResultModel set(String fieldStr, Object object) {
        Field field = null;
        try {
            field = this.getClass().getDeclaredField(fieldStr);
        } catch (NoSuchFieldException | SecurityException e) {
            log.warn("ResultModel get field faild!", e);
            return this;
        }

        field.setAccessible(true);
        try {
            field.set(this, object);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.warn("ResultModel set field faild!", e);
            return this;
        }

        return this;
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("retCod", this.retCod);
        jsonObject.put("retMsg", this.retMsg);
        jsonObject.put("data", this.data);
        return jsonObject.toJSONString();
    }
}
