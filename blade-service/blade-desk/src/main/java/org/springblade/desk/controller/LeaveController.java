/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.desk.controller;

import lombok.AllArgsConstructor;
import org.springblade.common.cache.CacheNames;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springblade.desk.dto.StartProcessDTO;
import org.springblade.desk.entity.ProcessLeave;
import org.springblade.desk.service.ILeaveService;
import org.springblade.flow.core.constant.ProcessConstant;
import org.springblade.flow.core.vo.FlowNodeRequest;
import org.springblade.system.user.cache.UserCache;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 控制器
 *
 * @author Chill
 */
@ApiIgnore
@RestController
@RequestMapping("/process/leave")
@AllArgsConstructor
public class LeaveController extends BladeController implements CacheNames {

	private ILeaveService leaveService;

	/**
	 * 详情
	 *
	 * @param businessId 主键
	 */
	@GetMapping("detail")
	public R<ProcessLeave> detail(Long businessId) {
		ProcessLeave detail = leaveService.getById(businessId);
		if(Func.isNotEmpty(detail)){
			detail.getFlow().setAssigneeName(UserCache.getUser(detail.getCreateUser()).getRealName());
		}
		return R.data(detail);
	}

	/**
	 * 发起流程，确认提交
	 *
	 * @param startProcessDTO 流程发起时传输对象
	 */
	@PostMapping("start-process")
	public R startProcess(@RequestBody StartProcessDTO startProcessDTO) {
		return R.status(leaveService.startProcess(startProcessDTO.getProcessLeave(),startProcessDTO.getFlowNodeRequestList()));
	}

	/**
	 * 发起流程，选择节点和人员
	 *
	 * @param leave 业务对象
	 * @return 节点信息List
	 */
	@PostMapping("start-process-before")
	public R<List<FlowNodeRequest>> startProcessBefore(@RequestBody ProcessLeave leave) {
		return R.data(leaveService.startProcessBefore(leave, ProcessConstant.LEAVE_KEY));
	}

}
