package org.springblade.contract.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.contract.entity.ContractFormInfoEntity;
import org.springblade.contract.service.IContractFormInfoService;
import org.springblade.contract.service.IContractPerformanceService;
import org.springblade.contract.vo.ContractFormInfoRequestVO;
import org.springblade.contract.vo.ContractFormInfoResponseVO;
import org.springblade.contract.wrapper.ContractFormInfoWrapper;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 *  控制器
 *
 * @author : 史智伟
 * @date : 2020-09-23 18:04:37
 */
@RestController
@AllArgsConstructor
@RequestMapping("/contractFormInfo")
@Api(value = "", tags = "")
public class ContractFormInfoController extends BladeController {

	private IContractFormInfoService contractFormInfoService;

	private IContractPerformanceService performanceService;
	private static final String ASSESSMENTS_CONTRACT_STATUS="100";
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入contractFormInfo")
	@PreAuth("hasPermission('contractFormInfo:contractFormInfo:detail')")
	public R<ContractFormInfoResponseVO> detail(@RequestParam Long id) {
		ContractFormInfoResponseVO detail = contractFormInfoService.getById(id);
		return R.data(detail);
	}

	/**
	 * 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入contractFormInfo")
	@PreAuth("hasPermission('contractFormInfo:contractFormInfo:list')")
	public R<IPage<ContractFormInfoResponseVO>> list(ContractFormInfoEntity contractFormInfo, Query query) {
		IPage<ContractFormInfoResponseVO> pages = contractFormInfoService.pageList(Condition.getPage(query), contractFormInfo);
		return R.data(pages);
	}

	/**
	 * 用印分页
	 */
	@GetMapping("/listSealInfo")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入ContractFormInfoRequestVO")
	@PreAuth("hasPermission('contractFormInfo:contractFormInfo:listSealInfo')")
	public R<IPage<ContractFormInfoResponseVO>> listSealInfo(ContractFormInfoRequestVO contractFormInfoRequestVO, Query query) {
		IPage<ContractFormInfoEntity> pages = contractFormInfoService.pageListSealInfo(Condition.getPage(query), contractFormInfoRequestVO);
		return R.data(ContractFormInfoWrapper.build().pageVO(pages));
	}

	/**
	 * 新增
	 */
	@PostMapping("/add")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "新增", notes = "传入contractFormInfo")
	@PreAuth("hasPermission('contractFormInfo:contractFormInfo:add')")
	public R<ContractFormInfoEntity> save(@Valid @RequestBody ContractFormInfoRequestVO contractFormInfo) {
        ContractFormInfoEntity entity = new ContractFormInfoEntity();
        BeanUtil.copy(contractFormInfo,entity);
		contractFormInfoService.save(entity);
		contractFormInfo.setId(entity.getId());
		/*保存相对方信息*/
		if(contractFormInfo.getCounterpart().size()>0){
			contractFormInfoService.saveCounterpart(contractFormInfo);
		}
		/*保存依据信息*/
		if(contractFormInfo.getAccording().size()>0){
			contractFormInfoService.saveAccording(contractFormInfo);
		}
		/*保存履约信息*/
		if(contractFormInfo.getPerformanceList().size()>0){
			contractFormInfo.getPerformanceList().forEach(performance->{
				performance.setContractId(contractFormInfo.getId());
				performanceService.save(performance);
			});
		}
		return R.data(entity);
	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入contractFormInfo")
	@PreAuth("hasPermission('contractFormInfo:contractFormInfo:update')")
	public R update(@Valid @RequestBody ContractFormInfoRequestVO contractFormInfo) {
	    if (Func.isEmpty(contractFormInfo.getId())){
            throw new ServiceException("id不能为空");
        }
	    ContractFormInfoEntity entity = new ContractFormInfoEntity();
        BeanUtil.copy(contractFormInfo,entity);
		return R.status(contractFormInfoService.updateById(entity));
	}

	/**
	 * 导出后修改合同状态
	 */
	@PostMapping("/updateExport")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "修改", notes = "传入id")
	@PreAuth("hasPermission('contractFormInfo:contractFormInfo:updateExport')")
	public R updateExport(@RequestParam Long id) {
		String contractStatus = "40";
		if (Func.isEmpty(id)){
			throw new ServiceException("id不能为空");
		}
		return R.status(contractFormInfoService.updateExportStatus(contractStatus,id));
	}


	/**
	 * 同评估后修改合同状态
	 */
	@PostMapping("/updateAssessmentStatus")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "修改", notes = "传入id")
	@PreAuth("hasPermission('contractFormInfo:contractFormInfo:updateAssessmentStatus')")
	public R updateContractStatus(@RequestParam Long id) {
		String contractStatus = ASSESSMENTS_CONTRACT_STATUS;
		if (Func.isEmpty(id)){
			throw new ServiceException("id不能为空");
		}
		return R.status(contractFormInfoService.updateExportStatus(contractStatus,id));
	}


	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@PreAuth("hasPermission('contractFormInfo:contractFormInfo:remove')")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(contractFormInfoService.deleteLogic(Func.toLongList(ids)));
	}

}
