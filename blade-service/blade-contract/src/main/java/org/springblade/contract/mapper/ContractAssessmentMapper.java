package org.springblade.contract.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springblade.contract.dto.ContractAssessmentDTO;
import org.springblade.contract.entity.ContractAssessmentEntity;
import org.springblade.contract.entity.ContractCounterpartEntity;
import org.springblade.contract.entity.ContractFormInfoEntity;
import org.springblade.contract.vo.ContractAssessmentRequestVO;
import org.springblade.contract.vo.ContractAssessmentResponseVO;
import org.springblade.contract.vo.ContractFormInfoResponseVO;

import java.io.Serializable;

/**
 * 合同评估表 Mapper 接口
 *
 * @author liyj
 * @date : 2020-09-24 10:41:34
 */
public interface ContractAssessmentMapper extends BaseMapper<ContractAssessmentEntity> {

	/**
	 * 评估信息分页查询
	 *
	 * @param page
	 * @param assessment
	 * @return
	 */
	IPage<ContractAssessmentEntity> pageList(IPage<ContractAssessmentEntity> page, ContractAssessmentRequestVO assessment);


	/**
	 * 一对一
	 * 根据id合同信息关联评估信息查询
	 * @param id
	 * @return
	 */
	ContractAssessmentEntity selectByAssessmentId(Long id);

	/**
	 * 一对多
	 * 根据id合同信息关联评估信息查询
	 * @param id
	 * @return
	 */
	ContractAssessmentEntity selectByAssessmentIds(Long id);
}
