package person.pluto.natcross2.model.interactive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * <p>
 * 请求建立控制器模型
 * </p>
 *
 * @author Pluto
 * @since 2020-01-08 16:37:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientControlModel {

    private Integer listenPort;

}
