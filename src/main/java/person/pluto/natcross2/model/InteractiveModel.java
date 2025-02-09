package person.pluto.natcross2.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import person.pluto.natcross2.common.CommonFormat;
import person.pluto.natcross2.model.enumeration.InteractiveTypeEnum;

/**
 * <p>
 * 交互基础类型
 * </p>
 *
 * @author Pluto
 * @since 2019-07-18 18:16:34
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class InteractiveModel implements JSONAware {

    public static InteractiveModel of(InteractiveTypeEnum interactiveTypeEnum, String key, String value) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, value);

        return new InteractiveModel(CommonFormat.generateInteractiveSeq(), interactiveTypeEnum.name(), jsonObject);
    }

    public static InteractiveModel of(String interactiveSeq, InteractiveTypeEnum interactiveTypeEnum, Object data) {
        return new InteractiveModel(interactiveSeq, interactiveTypeEnum.name(),
                data == null ? null : JSON.parseObject(JSON.toJSONString(data)));
    }

    public static InteractiveModel of(InteractiveTypeEnum interactiveTypeEnum, Object data) {
        return new InteractiveModel(CommonFormat.generateInteractiveSeq(), interactiveTypeEnum.name(),
                data == null ? null : JSON.parseObject(JSON.toJSONString(data)));
    }

    public static InteractiveModel of(String interactiveType, Object data) {
        return new InteractiveModel(CommonFormat.generateInteractiveSeq(), interactiveType,
                JSON.parseObject(JSON.toJSONString(data)));
    }

    public InteractiveModel(InteractiveModel model) {
        this.fullValue(model);
    }

    public void fullValue(InteractiveModel model) {
        this.setInteractiveSeq(model.getInteractiveSeq());
        this.setInteractiveType(model.getInteractiveType());
        this.setData(model.getData());
    }

    /**
     * 交互序列，用于异步通信
     */
    private String interactiveSeq;
    /**
     * 交互类型
     */
    private String interactiveType;
    /**
     * 交互实体内容
     */
    private JSONObject data;

    @Override
    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("interactiveSeq", this.interactiveSeq);
        jsonObject.put("interactiveType", this.interactiveType);
        jsonObject.put("data", this.data);
        return jsonObject.toJSONString();
    }

}
