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
package org.springblade.desk.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.api.ServiceCode;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.desk.entity.ProcessLeave;
import org.springblade.desk.mapper.LeaveMapper;
import org.springblade.desk.service.ILeaveService;
import org.springblade.flow.core.constant.ProcessConstant;
import org.springblade.flow.core.entity.BladeFlow;
import org.springblade.flow.core.feign.IFlowClient;
import org.springblade.flow.core.utils.FlowUtil;
import org.springblade.flow.core.vo.FlowNodeRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 服务实现类
 *
 * @author Chill
 */
@Slf4j
@Service
@AllArgsConstructor
public class LeaveServiceImpl extends BaseServiceImpl<LeaveMapper, ProcessLeave> implements ILeaveService {

	private IFlowClient flowClient;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean startProcess(ProcessLeave leave, List<FlowNodeRequest> flowNodeRequestList) {
		String businessTable = FlowUtil.getBusinessTable(ProcessConstant.LEAVE_KEY);
		/* 调用flow获取流程定义信息 */
		if (Func.isEmpty(leave.getId())) {
			/*保存leave*/
			leave.setApplyTime(DateUtil.now());
			save(leave);
			/* 启动流程 */
			R<BladeFlow> result = flowClient.startProcessInstanceById(flowNodeRequestList.get(0), FlowUtil.getBusinessKey(businessTable, String.valueOf(leave.getId())));
			if (result.isSuccess()) {
				log.debug("流程已启动,流程ID:" + result.getData().getProcessInstanceId());
				/* 返回流程id写入leave */
				/*leave.setProcessInstanceId(result.getData().getProcessInstanceId());*/
				/*updateById(leave);*/
			} else {
				log.error("【错误码{}】：开启流程失败，desk调用flow模块发生异常，请检查flow模块日志",ServiceCode.FEIGN_FAIL.getCode());
				throw new ServiceException(ServiceCode.FEIGN_FAIL);
			}
		} else {
			updateById(leave);
		}
		return true;
	}

	@Override
	public List<FlowNodeRequest> startProcessBefore(ProcessLeave leave, String businessType) {
		/* 模块间调用时，对象传输后会自动转成数组，此处将对象转成map，用于在flow模块获取属性值 */
		Map<String, Object> map = BeanUtil.toMap(leave);
		R<List<FlowNodeRequest>> listR = flowClient.startProcessBefore(map, businessType);
		if (listR.isSuccess()) {
			return listR.getData();
		}else{
			log.error("【错误码{}】:{}",ServiceCode.FEIGN_FAIL.getCode(),"blade-desk调用blade-flow失败，请查看flow模块异常信息");
			throw new ServiceException(ServiceCode.FEIGN_FAIL);
		}
	}
}
