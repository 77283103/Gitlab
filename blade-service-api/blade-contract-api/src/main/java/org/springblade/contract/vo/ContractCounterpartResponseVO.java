package org.springblade.contract.vo;

import lombok.*;
import org.springblade.contract.entity.ContractCounterpartEntity;
import io.swagger.annotations.ApiModel;

/**
 * 相对方管理 返回模型VO
 *
 * @author XHB
 * @date : 2020-09-23 19:35:09
 */
@Getter
@Setter
@NoArgsConstructor
@ApiModel(description = "相对方管理返回对象")
@EqualsAndHashCode(callSuper = true)
public class ContractCounterpartResponseVO extends ContractCounterpartEntity {

	private static final long serialVersionUID = 1L;

}