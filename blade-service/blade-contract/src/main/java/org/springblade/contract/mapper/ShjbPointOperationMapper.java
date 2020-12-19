package org.springblade.contract.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.contract.entity.ShjbPointOperationEntity;
import org.springblade.contract.vo.ShjbPointOperationRequestVO;

/**
 * 售货机类：0428修-（点位+运营）UP售货机合作合同（线上线下款相抵付款条款，20191014） Mapper 接口
 *
 * @author 售货机类：0428修-（点位+运营）UP售货机合作合同（线上线下款相抵付款条款，20191014）
 * @date : 2020-12-18 16:05:26
 */
public interface ShjbPointOperationMapper extends BaseMapper<ShjbPointOperationEntity> {

	/**
	 * 分页查询
	 * @param page
	 * @param shjbPointOperation
	 * @return
	 */
	IPage<ShjbPointOperationEntity> pageList(IPage<ShjbPointOperationEntity> page, ShjbPointOperationRequestVO shjbPointOperation);

}
