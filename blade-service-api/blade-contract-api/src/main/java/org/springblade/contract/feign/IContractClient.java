package org.springblade.contract.feign;

import com.sun.xml.internal.bind.v2.model.core.ID;
import org.springblade.contract.entity.ContractFormInfoEntity;
import org.springblade.contract.vo.ContractFormInfoResponseVO;
import org.springblade.contract.vo.ContractTemplateResponseVO;
import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.tool.api.R;
import org.springblade.system.entity.TemplateEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign接口类
 *
 * @author xhb
 */
@FeignClient(
	value = AppConstant.APPLICATION_CONTRACT_NAME
	//fallback = IContractFallback.class
)
public interface IContractClient {

	String API_PREFIX = "/client";
	String CONTRACT = API_PREFIX + "/contractFormInfo";
	String TEMPLATE_UPDATE = API_PREFIX + "/template_update";
	String CHOOSE=API_PREFIX + "/getChooseList";
	String CONTRACT_SAVE = API_PREFIX + "/contractSave";
	String TEMPLATE_GET_ID = API_PREFIX + "/template_getId";
	/**
	 * 获取合同信息
	 * @param id
	 * @return
	 */
	@GetMapping(CONTRACT)
	R<ContractFormInfoResponseVO> getById(@RequestParam("id") Long id);
	/**
	 * 動態返回合同選擇的下拉選
	 * @return list
	 */
	@GetMapping(CHOOSE)
	R<List<ContractFormInfoEntity>> getChooseList();
	/**
	 * 更新模板里json
	 * @para @return
	 */
	@PostMapping(TEMPLATE_UPDATE)
	R<ContractTemplateResponseVO> templateUpdate(@RequestBody TemplateEntity entity);

	/**
	 * 更新模板里json
	 * @para @return
	 * @return
	 */
	@GetMapping(CONTRACT_SAVE)
	R saveContractFormInfo(@RequestParam("id") Long id, @RequestParam("status") String status);

	/**
	 * 根据模板查询id
	 * @para @return
	 * @return
	 */
	@GetMapping(TEMPLATE_GET_ID)
	R<String> getByTemplateId(@RequestParam("id") Long id);
}
