package org.springblade.contract.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springblade.contract.entity.MtbMarketResearchContractEntity;

/**
 * 媒体类：市调合同（定性+定量) 模型DTO
 *
 * @author 刘是罕
 * @date : 2021-01-21 11:07:22
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class MtbMarketResearchContractDTO extends MtbMarketResearchContractEntity {

	private static final long serialVersionUID = 1L;

}
