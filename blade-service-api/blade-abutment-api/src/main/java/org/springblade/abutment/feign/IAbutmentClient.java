package org.springblade.abutment.feign;

import org.springblade.abutment.entity.*;
import org.springblade.abutment.vo.*;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Feign接口类
 *
 * @author xhb
 */
@FeignClient(
        value = "blade-abutment"
)
public interface IAbutmentClient {

    String API_PREFIX = "/client/abutment";
	String EKP = "/ekp";
	String DOC = "/doc";
	String E_SEAL = "/eSeal";
	String EKP_SEND_FORM = API_PREFIX + EKP + "/sendForm";
	String DOC_QUERY_INFO = API_PREFIX + DOC + "/queryInfo";
	String E_SEAL_UPLOAD_FILE = API_PREFIX + E_SEAL + "/uploadFiles";
	String E_SEAL_SINGLE_SIGN = API_PREFIX + E_SEAL + "/singleSign";
	String E_SEAL_SINGLE_SIGN_POST= API_PREFIX + E_SEAL + "/singleSignPost";
	String E_SEAL_SEND_SMS = API_PREFIX + E_SEAL + "/sendSms";
	String E_SEAL_SINGLE_SIGN_MULTI = API_PREFIX + E_SEAL + "/multiSign";
	String E_SEAL_READ_SIGNED = API_PREFIX + E_SEAL + "/readSigned";
	String E_SEAL_COMPANY_INFO = API_PREFIX + E_SEAL + "/queryCompanyInfo";

	/**
	 * 推送EKP信息
	 * @param entity
	 * @return
	 */
	@GetMapping(EKP_SEND_FORM)
	R<EkpVo> sendEkpForm(EkpEntity entity);

	/**
	 * 获取依据信息
	 * @param entity
	 * @return
	 */
	@GetMapping(DOC_QUERY_INFO)
	R<List<DocVo>> queryDocInfo(DocEntity entity);

	/**
	 * 上传合同
	 * @param entity
	 * @return
	 */
	@GetMapping(E_SEAL_UPLOAD_FILE)
	R<List<UploadFileVo>> uploadFiles(UploadFileEntity entity);

	/**
	 * 合同盖章 单个的
	 * @param entity
	 * @return
	 */
	@PostMapping(E_SEAL_SINGLE_SIGN)
	R<SingleSignVo> singleSign(@RequestBody SingleSignEntity entity);

	/**
	 * 合同盖章 单个的Post
	 * @param entity
	 * @return
	 */
	@PostMapping(E_SEAL_SINGLE_SIGN_POST)
	R<SingleSignVo> singleSignPost(@RequestBody SingleSignEntity entity);

	/**
	 * 发验证码短信
	 * @param entity
	 * @return
	 */
	@GetMapping(E_SEAL_SEND_SMS)
	R sendSms(SendSmsEntity entity);

	/**
	 * 合同盖章 批量的
	 * @param entity
	 * @return
	 */
	@GetMapping(E_SEAL_SINGLE_SIGN_MULTI)
	R<MultiSignVo> multiSign(MultiSignEntity entity);

	/**
	 * 获取合同地址
	 * @param entity
	 * @return
	 */
	@GetMapping(E_SEAL_READ_SIGNED)
	R<String> readSigned(ReadSignedEntity entity);

	/**
	 * 获取企业信息
	 * @param entity
	 * @return
	 */
    @GetMapping(E_SEAL_COMPANY_INFO)
	R<CompanyInfoVo> queryCompanyInfo(CompanyInfoEntity entity);
}
