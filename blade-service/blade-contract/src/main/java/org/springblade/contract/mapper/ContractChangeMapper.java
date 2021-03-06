package org.springblade.contract.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.contract.entity.ContractChangeEntity;

/**
 * 合同变更 Mapper 接口
 *
 * @author szw
 * @date : 2020-09-23 19:24:50
 */
public interface ContractChangeMapper extends BaseMapper<ContractChangeEntity> {

	/**
	 * 分页查询
	 * @param page
	 * @param change
	 * @return
	 */
	IPage<ContractChangeEntity> pageList(IPage<ContractChangeEntity> page, ContractChangeEntity change);

	/**
	 *
	 * @param id
	 * @return
	 */
	ContractChangeEntity selectById(Long id);


	/**
	 * 通过合同id删除
	 * @param id 合同id
	 * @return
	 */
	void deleteByChangeId(Long id);

	/**
	 * 根据文件变更ID删除文件id
	 * @param id
	 * @return
	 */
	Integer deleteChangeFileById(Long id);

	/**
	 *
	 * @param id
	 * @return
	 */
	ContractChangeEntity selectByFileId(Long id);
}
