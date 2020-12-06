package org.springblade.contract.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springblade.contract.entity.ContractCounterpartEntity;
import org.springblade.contract.entity.ContractFormInfoEntity;
import org.springblade.contract.mapper.ContractCounterpartMapper;
import org.springblade.contract.mapper.ContractFormInfoMapper;
import org.springblade.contract.service.IContractFormInfoService;
import org.springblade.contract.vo.ContractBondRequestVO;
import org.springblade.contract.vo.ContractBondResponseVO;
import org.springblade.contract.vo.ContractFormInfoResponseVO;
import org.springblade.contract.vo.ContractPerformanceResponseVO;
import org.springblade.contract.wrapper.ContractBondWrapper;
import org.springblade.contract.wrapper.ContractFormInfoWrapper;
import org.springblade.contract.wrapper.ContractPerformanceWrapper;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.contract.entity.ContractBondEntity;
import org.springblade.contract.mapper.ContractBondMapper;
import org.springblade.contract.service.IContractBondService;
import org.springblade.core.tool.utils.Func;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 保证金 服务实现类
 *
 * @author szw
 * @date : 2020-11-04 18:28:11
 */
@Service
@AllArgsConstructor
public class ContractBondServiceImpl extends BaseServiceImpl<ContractBondMapper, ContractBondEntity> implements IContractBondService {
	private ContractFormInfoMapper formInfoMapper;
	private IContractFormInfoService formInfoService;
	private ContractCounterpartMapper counterpartMapper;
	@Override
	public IPage<ContractBondResponseVO> pageList(IPage<ContractBondEntity> page, ContractBondRequestVO contractBond) {
		page=baseMapper.pageList(page, contractBond);
		IPage<ContractBondResponseVO> pages= ContractBondWrapper.build().entityPVPage(page);
		List<ContractBondResponseVO> records = pages.getRecords();
		List<ContractBondResponseVO> recordList = new ArrayList<>();
		for(ContractBondResponseVO v : records) {
			ContractFormInfoEntity formInfoEntity = formInfoMapper.selectById(v.getContractId());
			if (Func.isNotEmpty(formInfoEntity)) {
				ContractFormInfoResponseVO formInfoResponseVO= ContractFormInfoWrapper.build().entityPV(formInfoEntity);
				v.setContractFormInfoEntity(formInfoResponseVO);
			}
			recordList.add(v);
		}
		pages.setRecords(recordList);
		return pages;
	}

	@Override
	public IPage<ContractBondEntity> pageListSerious(IPage<ContractBondEntity> page, ContractBondRequestVO contractBond) {
		page=baseMapper.pageListSerious(page, contractBond);
		return page;
	}

	@Override
	public IPage<ContractBondEntity> pageListLong(IPage<ContractBondEntity> page, ContractBondRequestVO contractBond) {
		page = baseMapper.pageListLong(page, contractBond);
		return page;
	}

	@Override
	public void saveBond(List<Long> ids, Long id) {
		baseMapper.deleteBond(id);
		baseMapper.saveBond(ids,id);
	}

	@Override
	public void deleteByContractId(Long id) {
		List<ContractBondEntity> list=baseMapper.selectByIds(id);
		list.forEach(bond ->{
			baseMapper.deleteById(bond.getId());
		});
	}
}
