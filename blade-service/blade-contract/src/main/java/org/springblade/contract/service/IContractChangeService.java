package org.springblade.contract.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.contract.entity.ContractChangeEntity;
import org.springblade.contract.vo.ContractChangeResponseVO;
import org.springblade.core.mp.base.BaseService;

/**
 * 合同变更 服务类
 *
 * @author szw
 * @date : 2020-09-23 19:24:50
 */
public interface IContractChangeService extends BaseService<ContractChangeEntity> {

	/**
	 * 分页查询
	 * @param page
	 * @param change
	 * @return
	 */
	IPage<ContractChangeEntity> pageList(IPage<ContractChangeEntity> page, ContractChangeEntity change);

	/**
	 * 根据合同id删除重复信息
	 * @param id
	 */
	void deleteByChangeId(Long id);

	/**
	 * 修改原合同状态
	 * @param contractStatus,id
	 * @return
	 */
	boolean updateExportStatus(String contractStatus,Long id);

	/**
	 * 返回vo
	 * @param id
	 * @return
	 */
	ContractChangeResponseVO getById(Long id);

	/**
	 * 删除上传的文件
	 * @param id
	 */
	boolean deleteByChangeFileId(Long id);
}
