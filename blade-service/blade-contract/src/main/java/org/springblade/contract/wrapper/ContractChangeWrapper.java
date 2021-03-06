package org.springblade.contract.wrapper;

import org.springblade.contract.entity.ContractChangeEntity;
import org.springblade.contract.vo.ContractChangeResponseVO;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;

/**
 * 合同变更 包装类,返回视图层所需的字段
 *
 * @author szw
 * @date : 2020-09-23 19:24:50
 */
public class ContractChangeWrapper extends BaseEntityWrapper<ContractChangeEntity, ContractChangeResponseVO> {

	public static ContractChangeWrapper build() {
		return new ContractChangeWrapper();
 	}

	@Override
	public ContractChangeResponseVO entityVO(ContractChangeEntity change) {
		ContractChangeResponseVO contractChangeResponseVO = BeanUtil.copy(change, ContractChangeResponseVO.class);


		return contractChangeResponseVO;
	}

}
