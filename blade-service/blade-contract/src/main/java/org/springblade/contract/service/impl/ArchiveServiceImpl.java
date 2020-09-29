package org.springblade.contract.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.contract.service.IContractFormInfoService;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.contract.entity.ArchiveEntity;
import org.springblade.contract.mapper.ArchiveMapper;
import org.springblade.contract.service.IArchiveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 合同归档管理 服务实现类
 *
 * @author XHB
 * @date : 2020-09-23 18:32:15
 */
@Service
public class ArchiveServiceImpl extends BaseServiceImpl<ArchiveMapper, ArchiveEntity> implements IArchiveService {

	@Resource
	private IContractFormInfoService contractFormInfoService;

	@Override
	public IPage<ArchiveEntity> pageList(IPage<ArchiveEntity> page, ArchiveEntity archive) {
		return baseMapper.pageList(page, archive);
	}

	@Override
	public boolean save(String contractStatus, ArchiveEntity entity) {
		contractFormInfoService.updateExportStatus(contractStatus,entity.getContractId());
		if(baseMapper.insert(entity) == 1) {
			return true;
		}
		return false;
	}
}
