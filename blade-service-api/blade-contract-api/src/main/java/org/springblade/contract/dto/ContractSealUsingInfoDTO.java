package org.springblade.contract.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springblade.contract.entity.ContractSealUsingInfoEntity;
import java.util.Date;

/**
 * 合同用印 模型DTO
 *
 * @author 合同用印
 * @date : 2020-11-05 09:29:26
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ContractSealUsingInfoDTO extends ContractSealUsingInfoEntity {

	private static final long serialVersionUID = 1L;

}
