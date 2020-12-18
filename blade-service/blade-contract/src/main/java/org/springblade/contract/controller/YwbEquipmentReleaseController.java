package org.springblade.contract.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.contract.entity.YwbEquipmentReleaseEntity;
import org.springblade.contract.service.IYwbEquipmentReleaseService;
import org.springblade.contract.vo.YwbEquipmentReleaseRequestVO;
import org.springblade.contract.vo.YwbEquipmentReleaseResponseVO;
import org.springblade.contract.wrapper.YwbEquipmentReleaseWrapper;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * 业务类：19.设备投放使用协议 控制器
 *
 * @author : 业务类：19.设备投放使用协议
 * @date : 2020-12-18 16:07:44
 */
@RestController
@AllArgsConstructor
@RequestMapping("/ywbEquipmentRelease")
@Api(value = "业务类：19.设备投放使用协议", tags = "业务类：19.设备投放使用协议")
public class YwbEquipmentReleaseController extends BladeController {

	private IYwbEquipmentReleaseService ywbEquipmentReleaseService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入ywbEquipmentRelease")
	@PreAuth("hasPermission('ywbEquipmentRelease:ywbEquipmentRelease:detail')")
	public R<YwbEquipmentReleaseResponseVO> detail(@RequestParam Long id) {
		YwbEquipmentReleaseEntity detail = ywbEquipmentReleaseService.getById(id);
		return R.data(YwbEquipmentReleaseWrapper.build().entityPV(detail));
	}

	/**
	 * 分页
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入ywbEquipmentRelease")
	@PreAuth("hasPermission('ywbEquipmentRelease:ywbEquipmentRelease:page')")
	public R<IPage<YwbEquipmentReleaseResponseVO>> list(YwbEquipmentReleaseRequestVO ywbEquipmentRelease, Query query) {
		IPage<YwbEquipmentReleaseEntity> pages = ywbEquipmentReleaseService.pageList(Condition.getPage(query), ywbEquipmentRelease);
		return R.data(YwbEquipmentReleaseWrapper.build().entityPVPage(pages));
	}

	/**
	 * 新增
	 */
	@PostMapping("/add")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "新增", notes = "传入ywbEquipmentRelease")
	@PreAuth("hasPermission('ywbEquipmentRelease:ywbEquipmentRelease:add')")
	public R save(@Valid @RequestBody YwbEquipmentReleaseResponseVO ywbEquipmentRelease) {
		return R.status(ywbEquipmentReleaseService.save(YwbEquipmentReleaseWrapper.build().PVEntity(ywbEquipmentRelease)));
	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "修改", notes = "传入ywbEquipmentRelease")
	@PreAuth("hasPermission('ywbEquipmentRelease:ywbEquipmentRelease:update')")
	public R update(@Valid @RequestBody YwbEquipmentReleaseResponseVO ywbEquipmentRelease) {
	    if (Func.isEmpty(ywbEquipmentRelease.getId())){
            throw new ServiceException("id不能为空");
        }
		return R.status(ywbEquipmentReleaseService.updateById(YwbEquipmentReleaseWrapper.build().PVEntity(ywbEquipmentRelease)));
	}

	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	@PreAuth("hasPermission('ywbEquipmentRelease:ywbEquipmentRelease:remove')")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(ywbEquipmentReleaseService.deleteLogic(Func.toLongList(ids)));
	}

}
