package org.springblade.flow.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.tool.api.ServiceCode;
import org.springblade.core.tool.utils.Func;
import org.springblade.flow.business.mapper.ProcessMapper;
import org.springblade.flow.business.service.IProcessService;
import org.springblade.flow.core.entity.ProcessEntity;
import org.springblade.flow.core.utils.FlowUtil;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程定义信息表 服务实现类
 *
 * @author tianah
 * @date 2020-8-27
 */
@Slf4j
@Service
public class ProcessServiceImpl extends BaseServiceImpl<ProcessMapper, ProcessEntity> implements IProcessService {

	@Override
	public IPage<ProcessEntity> pageList(IPage<ProcessEntity> page, ProcessEntity process) {
		return baseMapper.pageList(page, process);
	}

	@Override
	public List<ProcessEntity> getProcessByBusinessType(String businessType) {
		return baseMapper.selectList(Wrappers.<ProcessEntity>query().lambda().eq(ProcessEntity::getBusinessType, businessType));
	}

	@Override
	public List<Map<String, String>> getBeanFields(String businessType) {
		List<Map<String, String>> list = new ArrayList<>(16);
		try {
			Class<?> clazz = Class.forName(FlowUtil.getBusinessFullClassName(businessType));
			Field[] fields = clazz.getDeclaredFields();
			/*遍历出所有属性返回前台用于下拉选展示*/
			for (int i = 0; i < fields.length; i++) {
				Map<String, String> map = new HashMap<>(16);
				Field field = fields[i];
				/*获取私有属性*/
				field.setAccessible(true);
				map.put("dictKey", field.getName());
				map.put("dictValue", field.getName());
				list.add(map);
			}
		} catch (ClassNotFoundException e) {
			log.error("【错误码{}】：反射找不到类，businessType={}", ServiceCode.FLOW_BUSINESS_TYPE_NOT_FOUND.getCode(), businessType,e);
			throw new ServiceException(ServiceCode.FLOW_BUSINESS_TYPE_NOT_FOUND);
		}
		return list;
	}

	@Override
	public boolean save(ProcessEntity processEntity) {
		decode(processEntity);
		return super.save(processEntity);
	}

	@Override
	public boolean updateById(ProcessEntity processEntity) {
		decode(processEntity);
		return super.updateById(processEntity);
	}

	/**
	 * 对前台传过来的启动条件中特殊字符进行转义后存入数据库
	 *
	 * @param processEntity 流程定义实体类
	 * @return 转以后的实体
	 */
	private ProcessEntity decode(ProcessEntity processEntity) {
		/*如果启动条件不为空，进行转义，否则直接返回原对象*/
		if (Func.isNotEmpty(processEntity.getStartCondition())) {
			String startCondition = processEntity.getStartCondition();
			try {
				startCondition = URLDecoder.decode(startCondition, "UTF-8").replace("&amp;", "&");
			} catch (UnsupportedEncodingException e) {
				log.error("【错误码{}】：流程定义保存，启动条件保存或修改时字符转码发生错误", ServiceCode.CHARACTER_DECODE_FAIL.getCode(), e);
				throw new ServiceException(ServiceCode.CHARACTER_DECODE_FAIL);
			}
			processEntity.setStartCondition(startCondition);
		}
		return processEntity;
	}
}
