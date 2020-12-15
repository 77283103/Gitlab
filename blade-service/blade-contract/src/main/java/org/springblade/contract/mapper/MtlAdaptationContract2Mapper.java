package org.springblade.contract.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.contract.entity.MtlAdaptationContract2Entity;
import org.springblade.contract.vo.MtlAdaptationContract2RequestVO;

/**
 * 媒体类：视频广告改编合同关联表2 Mapper 接口
 *
 * @author 媒体类：视频广告改编合同关联表2
 * @date : 2020-12-11 08:36:47
 */
public interface MtlAdaptationContract2Mapper extends BaseMapper<MtlAdaptationContract2Entity> {

	/**
	 * 分页查询
	 * @param page
	 * @param mtlAdaptationContract2
	 * @return
	 */
	IPage<MtlAdaptationContract2Entity> pageList(IPage<MtlAdaptationContract2Entity> page, MtlAdaptationContract2RequestVO mtlAdaptationContract2);

}
