package org.springblade.contract.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import feign.form.ContentType;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springblade.abutment.entity.CompanyInfoEntity;
import org.springblade.abutment.entity.UploadFileEntity;
import org.springblade.abutment.feign.IAbutmentClient;
import org.springblade.abutment.vo.CompanyInfoVo;
import org.springblade.abutment.vo.EkpVo;
import org.springblade.abutment.vo.UploadFileVo;
import org.springblade.contract.constant.ContractFormInfoTemplateContract;
import org.springblade.contract.entity.*;
import org.springblade.contract.excel.ContractFormInfoImporter;
import org.springblade.contract.excel.ContractFormInfoImporterEx;
import org.springblade.contract.mapper.*;
import org.springblade.contract.service.*;
import org.springblade.contract.util.*;
import org.springblade.contract.vo.*;
import org.springblade.contract.wrapper.*;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.resource.feign.IFileClient;
import org.springblade.resource.vo.FileVO;
import org.springblade.system.cache.SysCache;
import org.springblade.system.entity.DictBiz;
import org.springblade.system.entity.TemplateFieldJsonEntity;
import org.springblade.system.feign.IDictBizClient;
import org.springblade.system.feign.ISysClient;
import org.springblade.system.user.cache.UserCache;
import org.springblade.system.user.entity.User;
import org.springblade.system.user.feign.IUserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ???????????????
 *
 * @author ?????????//
 * @date : 2020-09-23 18:04:38
 */
@Service
public class ContractFormInfoServiceImpl extends BaseServiceImpl<ContractFormInfoMapper, ContractFormInfoEntity> implements IContractFormInfoService {
	@Value("${api.file.ftlPath}")
	private String ftlPath;
	//????????????
	//private String ftlPath;
	//private static final String ftlPath="D:/ftl/";
	//private static final String ftlPath="/ftl/";
	@Autowired
	private IFileClient fileClient;
	@Autowired
	private ISysClient sysClient;
	@Autowired
	private IUserClient userClient;
	@Autowired
	private IDictBizClient bizClient;
	@Autowired
	private IAbutmentClient abutmentClient;
	@Autowired
	private ContractFormInfoMapper contractFormInfoMapper;
	@Autowired
	private ContractCounterpartMapper contractCounterpartMapper;
	@Autowired
	private ContractBondMapper contractBondMapper;
	@Autowired
	private ContractAccordingMapper contractAccordingMapper;
	@Autowired
	private ContractPerformanceMapper contractPerformanceMapper;
	@Autowired
	private ContractPerformanceColPayMapper contractPerformanceColPayMapper;
	@Autowired
	private ContractAssessmentMapper contractAssessmentMapper;
	@Autowired
	private ContractArchiveMapper contractArchiveMapper;
	@Autowired
	private ContractArchiveNotMapper contractArchiveNotMapper;
	@Autowired
	private ContractSealUsingInfoMapper sealUsingInfoMapper;
	@Autowired
	private ContractSigningMapper signingMapper;
	@Autowired
	private ContractArchiveNotMapper archiveNotMapper;
	@Autowired
	private ContractRelieveMapper relieveMapper;
	@Autowired
	private ContractTemplateMapper contractTemplateMapper;
	@Resource
	private RedisCacheUtil redisCacheUtil;
	@Autowired
	private ContractChangeMapper changeMapper;
	@Autowired
	private IYwlANewDisplay1Service ywlANewDisplay1Service;
	@Autowired
	private ICglCategorySalesContracts1Service cglCategorySalesContracts1Service;
	@Autowired
	private ICglTheSalesContract1Service cglTheSalesContract1Service;
	@Autowired
	private ICglRawMaterials1Service cglRawMaterials1Service;
	@Autowired
	private IMtlAdaptationContract1Service mtlAdaptationContract1Service;
	@Autowired
	private IMtlAdaptationContract2Service mtlAdaptationContract2Service;
	@Autowired
	private IMtlShootingAndProductionContract1Service mtlShootingAndProductionContract1Service;
	@Autowired
	private IMtlShootingAndProductionContract2Service mtlShootingAndProductionContract2Service;
	@Autowired
	private IMtlShootingAndProductionContract3Service mtlShootingAndProductionContract3Service;
	@Autowired
	private ISclProjectOutsourcing1Service sclProjectOutsourcing1Service;
	@Autowired
	private ISclEquipmentMaintenance1Service sclEquipmentMaintenance1Service;
	@Autowired
	private IMtbProductionContract1Service mtbProductionContract1Service;
	@Autowired
	private IMtbProductionContract2Service mtbProductionContract2Service;
	@Autowired
	private IMtbProductionContract3Service mtbProductionContract3Service;
	@Autowired
	private IMtlVideoProductionContract1Service mtlVideoProductionContract1Service;
	@Autowired
	private IMtlVideoProductionContract2Service mtlVideoProductionContract2Service;
	@Autowired
	private IMtlEditedTheContract1Service mtlEditedTheContract1Service;
	@Autowired
	private IMtlAudioProductionContract1Service mtlAudioProductionContract1Service;
	@Autowired
	private IMtlAudioProductionContract2Service mtlAudioProductionContract2Service;
	@Autowired
	private ISclConstructionProject1Service sclConstructionProject1Service;
	@Autowired
	private ISclConstructionProject2Service sclConstructionProject2Service;
	@Autowired
	private ISclConstructionProject3Service sclConstructionProject3Service;
	@Autowired
	private ICglSalesContract1Service cglSalesContract1Service;
	@Autowired
	private ICglProofingContract1Service cglProofingContract1Service;
	@Autowired
	private IProductOutServiceContract1Service productOutServiceContract1Service;
	@Autowired
	private IProductOutServiceContract2Service productOutServiceContract2Service;
	@Autowired
	private IProductOutServiceContract3Service productOutServiceContract3Service;
	@Autowired
	private IMtbMarketResearchContract1Service iMtbMarketResearchContract1Service;
	private static final String DICT_BIZ_FINAL_VALUE_CONTRACT_BIG_CATEGORY = "1332307279915393025";
	private static final String DICT_BIZ_FINAL_VALUE_CONTRACT_STATUS = "1332307106157961217";
	private static final String DICT_BIZ_FINAL_VALUE_CONTRACT_COL_PAY_TYPE = "1332307534161518593";
	private static final Long DICT_BIZ_FINAL_VALUE_CONTRACT_PAY_TYPE = 1323239541401841666L;
	private static final Long DICT_BIZ_FINAL_VALUE_CONTRACT_RECEIVE_TYPE = 1323239469884764161L;
	private static final Integer AMOUNT_RATIO_VALUE = 100;
	private static final String CONTRACT_CHANGE_REVIEW = "10";

	@Override
	public IPage<ContractFormInfoResponseVO> pageList(IPage<ContractFormInfoEntity> page, ContractFormInfoRequestVO contractFormInfo) {
		if (!Func.isEmpty(contractFormInfo.getContractStatus())) {
			String[] code = contractFormInfo.getContractStatus().split(",");
			contractFormInfo.setCode(Arrays.asList(code));
		}
		page = baseMapper.pageList(page, contractFormInfo);
		IPage<ContractFormInfoResponseVO> pages = ContractFormInfoWrapper.build().entityPVPage(page);
		List<ContractFormInfoResponseVO> records = pages.getRecords();
		List<ContractFormInfoResponseVO> recordList = new ArrayList<>();

		for (ContractFormInfoResponseVO v : records) {
			/*??????????????????????????????????????????????????????*/
			v.setUserRealName(userClient.userInfoById(v.getCreateUser()).getData().getRealName());
			v.setUserDepartName(sysClient.getDept(v.getCreateDept()).getData().getDeptName());
			//????????????????????????   ????????????????????????????????? ?????????????????????
			List<ContractCounterpartEntity> counterpartEntityList = contractCounterpartMapper.selectByIds(v.getId());
			if (Func.isNotEmpty(counterpartEntityList)) {
				v.setCounterpart(counterpartEntityList);
				StringBuilder name = new StringBuilder();
				for (ContractCounterpartEntity counterpartEntity : counterpartEntityList) {
					name.append(counterpartEntity.getName());
					name.append(",");
				}
				name.substring(0, name.length());
				v.setCounterpartName(name.toString());
			}
			//????????????????????????????????? ??????????????????
			ContractSealUsingInfoEntity sealUsingInfoEntity = sealUsingInfoMapper.selectUsingById(v.getId());
			if (Func.isNotEmpty(sealUsingInfoEntity)) {
				v.setSignTime(sealUsingInfoEntity.getSignTime());
				v.setSealInfoEntity(sealUsingInfoEntity);
			}
			//?????????????????????????????????  ????????????????????????????????????????????????????????????????????????,????????????
			ContractArchiveEntity archiveEntity = contractArchiveMapper.selectArchiveById(v.getId());
			if (Func.isNotEmpty(archiveEntity)) {
				v.setArchiveMonth(archiveEntity.getArchiveMonth());
				v.setPrintApplicant(archiveEntity.getPrintApplicant());
				v.setPrintCompany(archiveEntity.getPrintCompany());
				v.setContractPrintInitDept(archiveEntity.getContractPrintInitDept());
				v.setArchiveEntity(archiveEntity);
			}
			//????????????????????????????????? ??????????????????
			ContractSigningEntity signingEntity = signingMapper.selectSigningById(v.getId());
			if (Func.isNotEmpty(signingEntity)) {
				v.setSignDate(signingEntity.getSignDate());
				v.setContractStartingTime(signingEntity.getContractStartTime());
				v.setContractEndTime(signingEntity.getContractEndTime());
				v.setSigningEntity(signingEntity);
			}
			//????????????????????????????????????  ?????????????????????
			List<ContractArchiveNotEntity> archiveNotEntity = contractArchiveNotMapper.selectArchiveNotById(v.getId());
			if (archiveNotEntity.size() > 0) {
				v.setArchiveNotEntity(archiveNotEntity);
			}
			//??????????????????,????????????????????????????????????
//				List<ContractBondPlanEntity> bondPlanEntityList=bondPlanMapper.selectByIds(v.getId());
//				v.setBondPlanEntityList(bondPlanEntityList);
//				List<ContractPerformanceEntity> performanceEntityList=contractPerformanceMapper.selectByIds(v.getId());
//				v.setPerformanceList(performanceEntityList);
//				List<ContractPerformanceColPayEntity> performanceColPayEntityList=contractPerformanceColPayMapper.selectByIds(v.getId());
//				v.setPerformanceColPayList(performanceColPayEntityList);
//				if (bondPlanEntityList.size()>0 || performanceEntityList.size()>0 || performanceColPayEntityList.size()>0) {
//					String planSchedule=
//							bondPlanMapper.countOKById(v.getId())+contractPerformanceColPayMapper.countOKById(v.getId())+contractPerformanceMapper.countOKById(v.getId())+
//							"/"+
//							Integer.valueOf(bondPlanEntityList.size()+performanceEntityList.size()+performanceColPayEntityList.size());
//					v.setPlanSchedule(planSchedule);
//				}
			//??????????????????
			if (Func.isNoneBlank(v.getTextFile())) {
				R<List<FileVO>> result = fileClient.getByIds(v.getTextFile());
				if (result.isSuccess()) {
					v.setTestFileVOList(result.getData());
				}
			}
			//??????????????????
			if (Func.isNoneBlank(v.getAttachedFiles())) {
				R<List<FileVO>> result = fileClient.getByIds(v.getAttachedFiles());
				if (result.isSuccess()) {
					v.setAttachedFileVOList(result.getData());
				}
			}
			recordList.add(v);
		}
		pages.setRecords(recordList);
		return pages;
	}

	/**
	 * ????????????????????????
	 * statisticsList
	 *
	 * @param page
	 * @param contractFormInfo
	 * @return
	 */
	@Override
	public IPage<ContractFormInfoEntity> statisticsList(IPage<ContractFormInfoEntity> page, ContractFormInfoRequestVO contractFormInfo) {
		//????????????????????????????????????
		if ("deptType".equals(contractFormInfo.getStatisticsType())) {
			page = baseMapper.deptType(page, contractFormInfo);
			List<ContractFormInfoEntity> records = page.getRecords();
			List<ContractFormInfoEntity> recordList = new ArrayList<>();
			for (ContractFormInfoEntity v : records) {
				/*??????????????????????????????????????????????????????*/
				v.setCreateUserName(userClient.userInfoById(v.getCreateUser()).getData().getRealName());
				v.setCreateDeptName(sysClient.getDept(v.getCreateDept()).getData().getDeptName());
				//????????????????????????   ????????????????????????????????? ?????????????????????
				List<ContractCounterpartEntity> counterpartEntityList = contractCounterpartMapper.selectByIds(v.getId());
				if (Func.isNotEmpty(counterpartEntityList)) {
					v.setCounterpart(counterpartEntityList);
					StringBuilder name = new StringBuilder();
					for (ContractCounterpartEntity counterpartEntity : counterpartEntityList) {
						name.append(counterpartEntity.getName());
						name.append(",");
					}
					name.substring(0, name.length());
					v.setCounterpartName(name.toString());
					BigDecimal payAmountVoidData = contractFormInfoMapper.payTypeAmount(Long.valueOf(v.getContractBigCategory()), DICT_BIZ_FINAL_VALUE_CONTRACT_PAY_TYPE, v.getCreateDept(), contractFormInfo.getYearStart(), contractFormInfo.getYearEnd());
					BigDecimal receiveAmountVoidData = contractFormInfoMapper.payTypeAmount(Long.valueOf(v.getContractBigCategory()), DICT_BIZ_FINAL_VALUE_CONTRACT_RECEIVE_TYPE, v.getCreateDept(), contractFormInfo.getYearStart(), contractFormInfo.getYearEnd());
					v.setPayAmountVoidData(payAmountVoidData);
					v.setReceiveAmountVoidData(receiveAmountVoidData);
				}
				recordList.add(v);
				page.setRecords(recordList);
			}
		}
		//???????????????????????????
		if ("monthType".equals(contractFormInfo.getStatisticsType())) {
			page = baseMapper.monthTypeFirm(page, contractFormInfo);
			List<ContractFormInfoEntity> records = page.getRecords();
			List<ContractFormInfoEntity> recordList = new ArrayList<>();
			for (ContractFormInfoEntity v : records) {
				//????????????????????????????????????????????????????????????????????????????????????
				List<MonthTypeSelect> list = contractFormInfoMapper.monthType(v.getCreateDept(), contractFormInfo.getYearStart());
				List<MonthTypeSelect> monthTypeSelectList = new ArrayList<>();
				for (int j = 1; j <= 12; j++) {
					monthTypeSelectList.add(j - 1, null);
				}
				for (MonthTypeSelect t : list) {
					for (int i = 1; i <= 12; i++) {
						if (i == t.getMouth()) {
							List<ContractFormInfoEntity> formInfoEntityList = new ArrayList<>();
							monthTypeSelectList.set(i - 1, t);
							contractFormInfoMapper.monthByIdInfo(v.getCreateDept(), contractFormInfo.getYearStart(), String.valueOf(i)).forEach(info -> {
								//????????????????????????   ????????????????????????????????? ?????????????????????
								List<ContractCounterpartEntity> counterpartEntityList = contractCounterpartMapper.selectByIds(info.getId());
								if (Func.isNotEmpty(counterpartEntityList)) {
									info.setCounterpart(counterpartEntityList);
									StringBuilder name = new StringBuilder();
									for (ContractCounterpartEntity counterpartEntity : counterpartEntityList) {
										name.append(counterpartEntity.getName());
										name.append(",");
									}
									name.substring(0, name.length());
									info.setCounterpartName(name.toString());
								}
								formInfoEntityList.add(info);
							});
							t.setContractFormInfoEntityList(formInfoEntityList);
						}
					}
					t.setCompany(sysClient.getDept(v.getCreateDept()).getData().getDeptName());
				}
				v.setMonthTypeSelects(monthTypeSelectList);
				v.setCompany(sysClient.getDept(v.getCreateDept()).getData().getDeptName());
				recordList.add(v);
			}
			page.setRecords(recordList);
		}
		//????????????????????????
		if ("eachType".equals(contractFormInfo.getStatisticsType())) {
			page = baseMapper.eachType(page, contractFormInfo);
			List<ContractFormInfoEntity> records = page.getRecords();
			List<ContractFormInfoEntity> recordList = new ArrayList<>();
			for (ContractFormInfoEntity v : records) {
				BigDecimal payAmountVoidData = contractFormInfoMapper.payTypeAmount(Long.valueOf(v.getContractBigCategory()), DICT_BIZ_FINAL_VALUE_CONTRACT_PAY_TYPE, v.getCreateDept(), contractFormInfo.getYearStart(), contractFormInfo.getYearEnd());
				BigDecimal receiveAmountVoidData = contractFormInfoMapper.payTypeAmount(Long.valueOf(v.getContractBigCategory()), DICT_BIZ_FINAL_VALUE_CONTRACT_RECEIVE_TYPE, v.getCreateDept(), contractFormInfo.getYearStart(), contractFormInfo.getYearEnd());
				v.setPayAmountVoidData(payAmountVoidData);
				v.setReceiveAmountVoidData(receiveAmountVoidData);
				recordList.add(v);
			}
			page.setRecords(recordList);
		}

		return page;
	}

	/**
	 * ????????????????????????
	 *
	 * @param page
	 * @param contractFormInfo
	 * @return
	 */
	@Override
	public IPage<ContractFormInfoResponseVO> pageListStatistics(IPage<ContractFormInfoEntity> page, ContractFormInfoRequestVO contractFormInfo) {
		if (Func.isNotBlank(contractFormInfo.getContractBigCategory()) && DICT_BIZ_FINAL_VALUE_CONTRACT_BIG_CATEGORY.equals(contractFormInfo.getContractBigCategory())) {
			page = contractFormInfoMapper.pageListStatisticsType(page, contractFormInfo);
		}
		if (Func.isNotBlank(contractFormInfo.getContractStatus()) && DICT_BIZ_FINAL_VALUE_CONTRACT_STATUS.equals(contractFormInfo.getContractStatus())) {
			page = contractFormInfoMapper.pageListStatisticsStatus(page, contractFormInfo);
		}
		if (Func.isNotBlank(contractFormInfo.getColPayType()) && DICT_BIZ_FINAL_VALUE_CONTRACT_COL_PAY_TYPE.equals(contractFormInfo.getColPayType())) {
			page = contractFormInfoMapper.pageListStatisticsColPayType(page, contractFormInfo);
		}
		if (Func.isNotBlank(contractFormInfo.getContractBigCategory()) && !DICT_BIZ_FINAL_VALUE_CONTRACT_BIG_CATEGORY.equals(contractFormInfo.getContractBigCategory())) {
			page = contractFormInfoMapper.pageList(page, contractFormInfo);
		}
		if (Func.isNotBlank(contractFormInfo.getContractStatus()) && !DICT_BIZ_FINAL_VALUE_CONTRACT_STATUS.equals(contractFormInfo.getContractStatus())) {
			String[] code = contractFormInfo.getContractStatus().split(",");
			contractFormInfo.setCode(Arrays.asList(code));
			page = contractFormInfoMapper.pageList(page, contractFormInfo);
		}
		if (Func.isNotBlank(contractFormInfo.getColPayType()) && !DICT_BIZ_FINAL_VALUE_CONTRACT_COL_PAY_TYPE.equals(contractFormInfo.getColPayType())) {
			page = contractFormInfoMapper.pageList(page, contractFormInfo);
		}
		//????????????
		if (Func.isNotEmpty(contractFormInfo.getMinAmount()) || Func.isNotEmpty(contractFormInfo.getMaxAmount())) {
			page = contractFormInfoMapper.pageList(page, contractFormInfo);
		}
		if (!DICT_BIZ_FINAL_VALUE_CONTRACT_BIG_CATEGORY.equals(contractFormInfo.getContractBigCategory())
			&& !DICT_BIZ_FINAL_VALUE_CONTRACT_STATUS.equals(contractFormInfo.getContractStatus()) && !DICT_BIZ_FINAL_VALUE_CONTRACT_COL_PAY_TYPE.equals(contractFormInfo.getColPayType())
			|| Func.isNotEmpty(contractFormInfo.getMaxAmount()) || Func.isNotEmpty(contractFormInfo.getMinAmount())) {
			List<ContractFormInfoEntity> records = page.getRecords();
			List<ContractFormInfoEntity> recordList = new ArrayList<>();
			for (ContractFormInfoEntity v : records) {
				if (Func.isNotEmpty(v.getContractBigCategory())) {
					v.setAmountRatio(String.valueOf(
						v.getContractAmount().divide(BigDecimal.valueOf(
							contractFormInfoMapper.getNumAmount(v.getContractBigCategory())), 2, BigDecimal.ROUND_HALF_DOWN).multiply(
							BigDecimal.valueOf(AMOUNT_RATIO_VALUE)) + "%"));
					v.setContractBigCategory(bizClient.getValues("HTDL", Long.valueOf(v.getContractBigCategory())).getData());
				}
				if (Func.isNotEmpty(v.getColPayTerm())) {
					v.setColPayType(bizClient.getValues("col_pay_term", Long.valueOf(v.getColPayTerm())).getData());
				}
				//????????????????????????????????? ??????????????????
				ContractSigningEntity signingEntity = signingMapper.selectSigningById(v.getId());
				if (Func.isNotEmpty(signingEntity)) {
					v.setSigningEntity(signingEntity);
				}
				String contractAmount = v.getContractAmount() + "???";
				v.setAmountVoidData(contractAmount);
				recordList.add(v);
			}
			page.setRecords(recordList);
		}
		IPage<ContractFormInfoResponseVO> pages = ContractFormInfoWrapper.build().entityPVPage(page);
		if (DICT_BIZ_FINAL_VALUE_CONTRACT_BIG_CATEGORY.equals(contractFormInfo.getContractBigCategory())
			|| DICT_BIZ_FINAL_VALUE_CONTRACT_STATUS.equals(contractFormInfo.getContractStatus())
			|| DICT_BIZ_FINAL_VALUE_CONTRACT_COL_PAY_TYPE.equals(contractFormInfo.getColPayType())) {
			List<ContractFormInfoResponseVO> records = pages.getRecords();
			List<ContractFormInfoResponseVO> recordList = new ArrayList<>();
			for (ContractFormInfoResponseVO v : records) {
				BigDecimal contractAmountSum = BigDecimal.valueOf(contractFormInfoMapper.selectAmountSum());
				v.setAmountRatio(v.getContractAmount().divide(contractAmountSum, 2, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(AMOUNT_RATIO_VALUE)) + "%");
				if (DICT_BIZ_FINAL_VALUE_CONTRACT_BIG_CATEGORY.equals(contractFormInfo.getContractBigCategory())) {
					v.setSigningCount(contractFormInfoMapper.selectSigningCount(v.getContractBigCategory()));
					v.setContractBigCategory(v.getDictValue());
				}
				if (DICT_BIZ_FINAL_VALUE_CONTRACT_COL_PAY_TYPE.equals(contractFormInfo.getColPayType())) {
					v.setColPayType(v.getDictValue());
				}
				//TODO ???????????????????????????????????????????????????????????????????????????????????????????????????
				String contractAmount = v.getContractAmount() + "???";
				v.setAmountVoidData(contractAmount);
				recordList.add(v);
			}
			pages.setRecords(recordList);
		}
		return pages;
	}

	/**
	 * ????????????????????????
	 *
	 * @return list
	 */
	@Override
	public void importContractFormInfo(List<ContractFormInfoImporter> data, MultipartFile file, String json, String contractTemplateId, String contractBigCategory, String contractSmallCategory) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		data.forEach(contractFormInfoExcel -> {
			//??????????????????
			List<ContractFormInfoImporterEx> read2;
			ContractFormInfoEntity contractFormInfoEntity = new ContractFormInfoEntity();
			contractFormInfoEntity.setContractTemplateId(Long.valueOf(contractTemplateId));
			contractFormInfoEntity.setContractBigCategory(contractBigCategory);
			contractFormInfoEntity.setContractSmallCategory(contractSmallCategory);
			contractFormInfoEntity.setContractStatus("10");
			contractFormInfoEntity.setContractSoure("40");
			if (!"???".equals(contractFormInfoExcel.getSealName()) && !"".equals(contractFormInfoExcel.getSealName()) && contractFormInfoExcel.getSealName() != null) {
				contractFormInfoEntity.setSealName(contractFormInfoExcel.getSealName());
			}
			if (!"???".equals(contractFormInfoExcel.getDictValue()) && !"".equals(contractFormInfoExcel.getDictValue()) && contractFormInfoExcel.getDictValue() != null) {
				contractFormInfoEntity.setDictValue(contractFormInfoExcel.getDictValue());
			}
			if (!"???".equals(contractFormInfoExcel.getPersonContract()) && !"".equals(contractFormInfoExcel.getPersonContract()) && contractFormInfoExcel.getPersonContract() != null) {
				contractFormInfoEntity.setPersonContract(contractFormInfoExcel.getPersonContract());
			}
			if (!"???".equals(contractFormInfoExcel.getSealNumber()) && !"".equals(contractFormInfoExcel.getSealNumber()) && contractFormInfoExcel.getSealNumber() != null) {
				contractFormInfoEntity.setSealNumber(Integer.parseInt(contractFormInfoExcel.getSealNumber()));
			}
			if (!"???".equals(contractFormInfoExcel.getContractPeriod()) && !"".equals(contractFormInfoExcel.getContractPeriod()) && contractFormInfoExcel.getContractPeriod() != null) {
				contractFormInfoEntity.setContractPeriod(contractFormInfoExcel.getContractPeriod());
			}
			try {
				if (!"???".equals(contractFormInfoExcel.getStartingTime()) && !"".equals(contractFormInfoExcel.getStartingTime()) && contractFormInfoExcel.getStartingTime() != null) {
					Date parse = simpleDateFormat.parse(contractFormInfoExcel.getStartingTime());
					contractFormInfoEntity.setStartingTime(parse);
				}
				if (!"???".equals(contractFormInfoExcel.getEndTime()) && !"".equals(contractFormInfoExcel.getEndTime()) && contractFormInfoExcel.getEndTime() != null) {
					Date parse = simpleDateFormat.parse(contractFormInfoExcel.getEndTime());
					contractFormInfoEntity.setEndTime(parse);
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (!"???".equals(contractFormInfoExcel.getColPayType()) && !"".equals(contractFormInfoExcel.getColPayType()) && contractFormInfoExcel.getColPayType() != null) {
				contractFormInfoEntity.setColPayType(contractFormInfoExcel.getColPayType());
			}
			if (!"???".equals(contractFormInfoExcel.getColPayTerm()) && !"".equals(contractFormInfoExcel.getColPayTerm()) && contractFormInfoExcel.getColPayTerm() != null) {
				contractFormInfoEntity.setColPayTerm(contractFormInfoExcel.getColPayTerm());
			}
			if (!"???".equals(contractFormInfoExcel.getContractAmount()) && !"".equals(contractFormInfoExcel.getContractAmount()) && contractFormInfoExcel.getContractAmount() != null) {
				contractFormInfoEntity.setContractAmount(new BigDecimal(contractFormInfoExcel.getContractAmount()));
			}
			if ("???".equals(contractFormInfoExcel.getExtension()) && contractFormInfoExcel.getExtension() != null) {
				contractFormInfoEntity.setExtension("2");
			} else {
				contractFormInfoEntity.setExtension("1");
			}
			if (!"???".equals(contractFormInfoExcel.getContractForm()) && !"".equals(contractFormInfoExcel.getContractForm()) && contractFormInfoExcel.getContractForm() != null) {
				contractFormInfoEntity.setContractForm(contractFormInfoExcel.getContractForm());
			}
			if (!"???".equals(contractFormInfoExcel.getCounterpartPerson()) && !"".equals(contractFormInfoExcel.getCounterpartPerson()) && contractFormInfoExcel.getCounterpartPerson() != null) {
				contractFormInfoEntity.setCounterpartPerson(contractFormInfoExcel.getCounterpartPerson());
			}
			if (!"???".equals(contractFormInfoExcel.getTelephonePerson()) && !"".equals(contractFormInfoExcel.getTelephonePerson()) && contractFormInfoExcel.getTelephonePerson() != null) {
				contractFormInfoEntity.setTelephonePerson(contractFormInfoExcel.getTelephonePerson());
			}
			if (!"???".equals(contractFormInfoExcel.getEmailPerson()) && !"".equals(contractFormInfoExcel.getEmailPerson()) && contractFormInfoExcel.getEmailPerson() != null) {
				contractFormInfoEntity.setEmailPerson(contractFormInfoExcel.getEmailPerson());
			}
			if (!"???".equals(contractFormInfoExcel.getAddressPerson()) && !"".equals(contractFormInfoExcel.getAddressPerson()) && contractFormInfoExcel.getAddressPerson() != null) {
				contractFormInfoEntity.setAddressPerson(contractFormInfoExcel.getAddressPerson());
			}

            /*if (!"???".equals(contractFormInfoExcel.getYwlMinimum()) && !"".equals(contractFormInfoExcel.getYwlMinimum()) && contractFormInfoExcel.getYwlMinimum() != null) {
                contractFormInfoEntity.setYwlMinimum(contractFormInfoExcel.getYwlMinimum());
            }
            if (!"???".equals(contractFormInfoExcel.getYwlMailbox()) && !"".equals(contractFormInfoExcel.getYwlMailbox()) && contractFormInfoExcel.getYwlMailbox() != null) {
                contractFormInfoEntity.setYwlMailbox(contractFormInfoExcel.getYwlMailbox());
            }
            if (!"???".equals(contractFormInfoExcel.getYwlAgreements()) && !"".equals(contractFormInfoExcel.getYwlAgreements()) && contractFormInfoExcel.getYwlAgreements() != null) {
                contractFormInfoEntity.setYwlAgreements(contractFormInfoExcel.getYwlAgreements());
            }
            if (!"???".equals(contractFormInfoExcel.getYwlPacking()) && !"".equals(contractFormInfoExcel.getYwlPacking()) && contractFormInfoExcel.getYwlPacking() != null) {
                contractFormInfoEntity.setYwlPacking(contractFormInfoExcel.getYwlPacking());
            }
            if (!"???".equals(contractFormInfoExcel.getYwlMode()) && !"".equals(contractFormInfoExcel.getYwlMode()) && contractFormInfoExcel.getYwlMode() != null) {
                contractFormInfoEntity.setYwlMode(contractFormInfoExcel.getYwlMode());
            }
            if (!"???".equals(contractFormInfoExcel.getYwlAcceptance()) && !"".equals(contractFormInfoExcel.getYwlAcceptance()) && contractFormInfoExcel.getYwlAcceptance() != null) {
                contractFormInfoEntity.setYwlAcceptance(contractFormInfoExcel.getYwlAcceptance());
            }
            if (!"???".equals(contractFormInfoExcel.getYwlPaymentMethod()) && !"".equals(contractFormInfoExcel.getYwlPaymentMethod()) && contractFormInfoExcel.getYwlPaymentMethod() != null) {
                contractFormInfoEntity.setYwlPaymentMethod(contractFormInfoExcel.getYwlPaymentMethod());
            }
            if (!"???".equals(contractFormInfoExcel.getYwlNameBank()) && !"".equals(contractFormInfoExcel.getYwlNameBank()) && contractFormInfoExcel.getYwlNameBank() != null) {
                contractFormInfoEntity.setYwlNameBank(contractFormInfoExcel.getYwlNameBank());
            }
            if (!"???".equals(contractFormInfoExcel.getYwlAccountNumber()) && !"".equals(contractFormInfoExcel.getYwlAccountNumber()) && contractFormInfoExcel.getYwlAccountNumber() != null) {
                contractFormInfoEntity.setYwlAccountNumber(contractFormInfoExcel.getYwlAccountNumber());
            }
            if (!"???".equals(contractFormInfoExcel.getYwlDeliveryTimes()) && !"".equals(contractFormInfoExcel.getYwlDeliveryTimes()) && contractFormInfoExcel.getYwlDeliveryTimes() != null) {
                contractFormInfoEntity.setYwlDeliveryTimes(contractFormInfoExcel.getYwlDeliveryTimes());
            }
            if (!"???".equals(contractFormInfoExcel.getYwlDamages()) && !"".equals(contractFormInfoExcel.getYwlDamages()) && contractFormInfoExcel.getYwlDamages() != null) {
                contractFormInfoEntity.setYwlDamages(contractFormInfoExcel.getYwlDamages());
            }
            if (!"???".equals(contractFormInfoExcel.getYwlBreachOfContract()) && !"".equals(contractFormInfoExcel.getYwlBreachOfContract()) && contractFormInfoExcel.getYwlBreachOfContract() != null) {
                contractFormInfoEntity.setYwlBreachOfContract(contractFormInfoExcel.getYwlBreachOfContract());
            }*/
			/*String jsonEx = this.templateDraft(contractFormInfoEntity,json);*/
			/*ContractFormInfoImporter contractFormInfoImporterEx = new ContractFormInfoImporter();*/
			this.save(contractFormInfoEntity);
			if (!"".equals(contractFormInfoEntity.getId()) && contractFormInfoEntity.getId() != null) {
				//????????????contract_counterpart?????????????????????????????????????????????????????????id ????????????id?????????id????????? contract_counterpart_setting ??????
				if (!"".equals(contractFormInfoExcel.getCounterpartName())) {
					List<ContractCounterpartEntity> contractCounterpartEntities = contractCounterpartMapper.selectByName(contractFormInfoExcel.getCounterpartName());
					if (contractCounterpartEntities.size() > 0) {
						contractFormInfoMapper.deleteCounterpart(contractFormInfoEntity.getId());
						contractFormInfoMapper.saveCounterpart(contractFormInfoEntity.getId(), contractCounterpartEntities);
						contractFormInfoEntity.setCounterpart(contractCounterpartEntities);
						//???????????????contract_bond?????? contract_bond??????????????????????????????id
						//?????????????????????????????????????????????
						if (!"???".equals(contractFormInfoExcel.getIsNotBond())) {
							ContractBondEntity contractBondEntity = new ContractBondEntity();
							contractBondEntity.setIsNotBond(contractFormInfoExcel.getIsNotBond());
							if (!"???".equals(contractFormInfoExcel.getPlanPayAmount()) && !"".equals(contractFormInfoExcel.getPlanPayAmount()) && contractFormInfoExcel.getPlanPayAmount() != null) {
								if (!"???".equals(contractFormInfoExcel.getPlanPayAmount())) {
									contractBondEntity.setPlanPayAmount(new BigDecimal(0));
								} else {
									contractBondEntity.setPlanPayAmount(new BigDecimal(2));
								}
							}
							try {
								if (!"???".equals(contractFormInfoExcel.getPlanPayTime()) && !"".equals(contractFormInfoExcel.getPlanPayTime()) && contractFormInfoExcel.getPlanPayTime() != null) {
									Date parse = simpleDateFormat.parse(contractFormInfoExcel.getPlanPayTime());
									contractBondEntity.setPlanReturnTime(parse);
								}
								if (!"???".equals(contractFormInfoExcel.getPlanReturnTime()) && !"".equals(contractFormInfoExcel.getPlanReturnTime()) && contractFormInfoExcel.getPlanReturnTime() != null) {
									Date parse = simpleDateFormat.parse(contractFormInfoExcel.getPlanReturnTime());
									contractBondEntity.setPlanReturnTime(parse);
								}

							} catch (ParseException e) {
								e.printStackTrace();
							}
							contractBondEntity.setCounterpartId(contractCounterpartEntities.get(0).getId());
							contractBondMapper.insert(contractBondEntity);
							//????????????????????????????????????
							List<ContractBondEntity> list = new ArrayList();
							list.add(contractBondEntity);
							contractFormInfoEntity.setContractBond(list);
							//??????????????????id????????????id?????????contract_bond_setting???????????????
							List<Long> ids = new ArrayList<>();
							ids.add(contractFormInfoEntity.getId());
							contractBondMapper.saveBond(ids, contractBondEntity.getId());
						}
					}
				}
			}
			//????????????????????????
			ContractTemplateEntity contractTemplate = contractTemplateMapper.selectById(contractTemplateId);
			ExcelSaveUntil excelSaveUntil = new ExcelSaveUntil();
			excelSaveUntil.excelSave(contractFormInfoEntity, contractFormInfoExcel, contractTemplate, file, json);
			//String jsonEx = this.getjson(json, contractFormInfoEntity);
			//contractFormInfoEntity.setJson(jsonEx);
			//this.saveOrUpdate(contractFormInfoEntity);
		});
	}

	@Override
	public Integer selectSigningCount(String contractBigCategory) {
		return contractFormInfoMapper.selectSigningCount(contractBigCategory);
	}

	//json????????????
	public String getjson(String json, ContractFormInfoEntity contractFormInfo) {
		JSONArray objects = new JSONArray();
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		try {
			objects = JSONArray.parseArray(json);
			JSONObject db = (JSONObject) JSONObject.toJSON(contractFormInfo);
			for (int i = 0; i < objects.size(); i++) {
				JSONObject temp = objects.getJSONObject(i);
				String fieldName = temp.getString("fieldName");
				String componentType = temp.getString("componentType");
				if ("datePicker".equals(componentType)) {
					if (fieldName != null) {
						String dbColum = db.getString(fieldName);
						if (dbColum != null) {
							Date d = sdf.parse(dbColum);
							temp.put("fieldValue", sd.format(d));
						}
					}
				} else if ("relationList".equals(componentType)) {
					System.out.println("??????");
				} else {
					if (fieldName != null) {
						String dbColum = db.getString(fieldName);
						if (dbColum != null) {
							temp.put("fieldValue", dbColum);
						}
					}
				}
				objects.set(i, temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		json = objects.toJSONString();
		List<TemplateFieldJsonEntity> templateFieldList = JSON.parseArray(json, TemplateFieldJsonEntity.class);
		ContractTemplateEntity contractTemplate = contractTemplateMapper.selectById(contractFormInfo.getContractTemplateId());
		for (TemplateFieldJsonEntity templateField : templateFieldList) {
			if (ContractFormInfoTemplateContract.CONTRACT_ID.equals(templateField.getComponentType())) {
				templateField.setFieldValue(contractFormInfo.getId().toString());
			}
			//????????????
			if (ContractFormInfoTemplateContract.CONTRACT_BIG_CATEGORY.equals(templateField.getRelationCode())) {
				JSONObject jsonObj = JSON.parseObject(templateField.getSecondSelectData());
				if (null != jsonObj) {
					jsonObj.put("template", contractTemplate);
					jsonObj.put("first", contractFormInfo.getContractBigCategory());
					jsonObj.put("second", contractFormInfo.getContractSmallCategory());
					templateField.setSecondSelectDataObject(jsonObj);
				}
			}
			//????????????
			if (ContractFormInfoTemplateContract.CONTRACT_COL_PAY.equals(templateField.getRelationCode())) {
				JSONObject jsonObj = JSON.parseObject(templateField.getSecondSelectData());
				if (null != jsonObj) {
					jsonObj.put("first", contractFormInfo.getColPayType());
					jsonObj.put("second", contractFormInfo.getColPayTerm());
					jsonObj.put("days", contractFormInfo.getDays());
					templateField.setSecondSelectDataObject(jsonObj);
				}
			}
			//?????????????????????????????????
			if (ContractFormInfoTemplateContract.COMPONENT_TYPE_REQUORED.equals(templateField.getRequired())) {
				List<Object> objectList = JSON.parseArray(templateField.getRequiredData(), Object.class);
				if (CollectionUtil.isNotEmpty(objectList)) {
					templateField.setRequiredDataList(objectList);
				}
			}
			//???????????????????????????
			if (ContractFormInfoTemplateContract.COMPONENT_TYPE_SMALL.contains(templateField.getComponentType())) {
				List<Object> objectList = JSON.parseArray(templateField.getDicData(), Object.class);
				if (CollectionUtil.isNotEmpty(objectList)) {
					templateField.setDicDataList(objectList);
				}
			}
			if (ContractFormInfoTemplateContract.COMPONENT_TYPE.contains(templateField.getComponentType())) {
				if (ContractFormInfoTemplateContract.CONTRACT_ACCORDING.equals(templateField.getRelationCode())) {
					/*??????????????????*/
					if (CollectionUtil.isNotEmpty(contractFormInfo.getAccording())) {
						templateField.setTableData(JSONObject.toJSONString(contractFormInfo.getAccording()));
						templateField.setTableDataList(contractFormInfo.getAccording());
					}
				}
				//??????????????????????????????????????????
				if (ContractFormInfoTemplateContract.CONTRACT_COUNTERPART.equals(templateField.getRelationCode())) {
					//?????????????????????JSONObject
					JSONObject obj = JSON.parseObject(templateField.getTableDataObject());
					//???????????????????????????
					if (CollectionUtil.isNotEmpty(contractFormInfo.getCounterpart())) {
						obj.put(ContractFormInfoTemplateContract.CONTRACT_COUNTERPART_SUB_COUNTERPART, contractFormInfo.getCounterpart());
					}
					if (CollectionUtil.isNotEmpty(contractFormInfo.getContractBond())) {
						obj.put(ContractFormInfoTemplateContract.CONTRACT_COUNTERPART_SUB_COUNTERPART, contractFormInfo.getContractBond());
					}
					templateField.setTableDataObject(JSONObject.toJSONString(obj));
					templateField.setTableDataObjectList(obj);
				}
				//*??????????????????
				if (ContractFormInfoTemplateContract.CONTRACT_PERFORMANCE.equals(templateField.getRelationCode())) {
					if (CollectionUtil.isNotEmpty(contractFormInfo.getPerformanceList())) {
						templateField.setTableData(JSONObject.toJSONString(contractFormInfo.getPerformanceList()));
						templateField.setTableDataList(contractFormInfo.getPerformanceList());
					}
				}
				//*???????????????????????????
				if (ContractFormInfoTemplateContract.CONTRACT_PERFORMANCE_COLPAY.equals(templateField.getRelationCode())) {
					if (CollectionUtil.isNotEmpty(contractFormInfo.getPerformanceColPayList())) {
						templateField.setTableData(JSONObject.toJSONString(contractFormInfo.getPerformanceColPayList()));
						templateField.setTableDataList(contractFormInfo.getPerformanceColPayList());
					}
				}
				//*???????????????????????????
				if (ContractFormInfoTemplateContract.CONTRACT_RAW_MATERIALS.equals(templateField.getRelationCode())) {
					if (CollectionUtil.isNotEmpty(contractFormInfo.getRawMaterialsList())) {
						templateField.setTableData(JSONObject.toJSONString(contractFormInfo.getRawMaterialsList()));
						templateField.setTableDataList(contractFormInfo.getRawMaterialsList());
					}
				}
			}
		}
		return toJSONString(templateFieldList);
	}


	/**
	 * ??????????????????
	 *
	 * @param contractStatus,id
	 * @param id
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateExportStatus(String contractStatus, Long id) {
		return contractFormInfoMapper.updateExportStatus(contractStatus, id);
	}

	@Override
	public IPage<ContractFormInfoEntity> pageListSealInfo(IPage<ContractFormInfoRequestVO> page, ContractFormInfoRequestVO contractFormInfoRequestVO) {
		return baseMapper.pageListSealInfo(page, contractFormInfoRequestVO);
	}

	/**
	 * ?????????????????????
	 *
	 * @param vo ????????????id????????????id
	 */
	@Override
	public void saveCounterpart(ContractFormInfoRequestVO vo) {
		contractFormInfoMapper.deleteCounterpart(vo.getId());
		contractFormInfoMapper.saveCounterpart(vo.getId(), vo.getCounterpart());
	}

	@Override
	public void saveAccording(ContractFormInfoRequestVO vo) {
		//contractFormInfoMapper.saveAccording(vo.getId(),vo.getAccording());
	}

	/**
	 * ????????????????????????
	 *
	 * @param vo
	 */
	@Override
	public void saveSeal(ContractFormInfoRequestVO vo) {
		contractFormInfoMapper.saveSeal(vo.getId(), vo.getSeal());
	}

	/**
	 * ????????????????????????
	 *
	 * @param vo ????????????id?????????id
	 */
	@Override
	public void saveAssessment(ContractFormInfoRequestVO vo) {
		contractFormInfoMapper.saveAssessment(vo.getId(), vo.getAssessment());
	}

	/**
	 * ????????????????????????
	 *
	 * @param vo ?????????????????????id?????????id
	 */
	@Override
	public void saveArchive(ContractFormInfoRequestVO vo) {
		contractFormInfoMapper.saveArchive(vo.getId(), vo.getArchive());
	}

	/**
	 * ????????????????????????
	 *
	 * @param vo
	 */
	@Override
	public void saveSigning(ContractFormInfoRequestVO vo) {
		contractFormInfoMapper.saveSigning(vo.getId(), vo.getSigning());
	}

	/**
	 * ????????????id?????????????????????????????????vo
	 *
	 * @param id ??????id
	 * @return
	 */
	@Override
	public ContractFormInfoResponseVO getById(Long id) {
		ContractFormInfoResponseVO contractFormInfoResponseVO = new ContractFormInfoResponseVO();
		ContractFormInfoEntity contractFormInfo = contractFormInfoMapper.selectById(id);
		ContractFormInfoEntity changeFormInfoEntity = contractFormInfoMapper.selectByChangeId(id);
		//????????????ID???????????????????????????????????? ?????????????????????????????????????????????????????????10??? ????????????????????????????????????**??????????????????**
		//???????????????????????????????????????????????????????????????????????????????????????VOList **????????????**???
		if (Func.isNotEmpty(changeFormInfoEntity)) {
			if (CONTRACT_CHANGE_REVIEW.equals(changeFormInfoEntity.getContractStatus())) {
				contractFormInfoResponseVO = ContractFormInfoWrapper.build().entityPV(changeFormInfoEntity);
			} else {
				contractFormInfoResponseVO = ContractFormInfoWrapper.build().entityPV(contractFormInfo);
				//?????????????????????????????????SET???VOList???
				List<ContractFormInfoEntity> changList=new ArrayList<>();
				changList.add(changeFormInfoEntity);
				contractFormInfoResponseVO.setFormInfosEntityNewVOList(changList);
			}
		} else {
			contractFormInfoResponseVO = ContractFormInfoWrapper.build().entityPV(contractFormInfo);
		}
		if (Func.isNoneBlank(contractFormInfoResponseVO.getSealName())) {
			String[] sealNameList = contractFormInfoResponseVO.getSealName().split(",");
			contractFormInfoResponseVO.setSealNameList(sealNameList);
		}
		//????????????
		List<ContractAccordingEntity> contractAccordingList = contractAccordingMapper.selectByIds(contractFormInfoResponseVO.getId());
		contractFormInfoResponseVO.setAccording(contractAccordingList);
		//?????????????????????????????????
		List<ContractCounterpartEntity> contractCounterpartList = contractCounterpartMapper.selectByIds(contractFormInfoResponseVO.getId());
		if (Func.isNotEmpty(contractCounterpartList)) {
			contractFormInfoResponseVO.setCounterpart(contractCounterpartList);
			StringBuilder name = new StringBuilder();
			for (ContractCounterpartEntity counterpartEntity : contractCounterpartList) {
				name.append(counterpartEntity.getName());
				name.append(",");
			}
			name.substring(0, name.length());
			contractFormInfoResponseVO.setCounterpartName(name.toString());
		}
		//?????????????????????????????????
			/*ContractCounterpartEntity counterpartEntity = contractCounterpartMapper.selectByIds(id).get(0);
			contractFormInfoResponseVO.setCounterpartEntity(counterpartEntity);*/
		//???????????????
		List<ContractBondEntity> contractBondList = contractBondMapper.selectByIds(contractFormInfoResponseVO.getId());
		contractFormInfoResponseVO.setContractBond(contractBondList);
		//????????????????????????
		List<ContractPerformanceEntity> contractPerformanceList = contractPerformanceMapper.selectByIds(contractFormInfoResponseVO.getId());
		contractFormInfoResponseVO.setPerformanceList(contractPerformanceList);
		//???????????????????????????
		List<ContractPerformanceColPayEntity> contractPerformanceColPayList = contractPerformanceColPayMapper.selectByIds(contractFormInfoResponseVO.getId());
		contractFormInfoResponseVO.setPerformanceColPayList(contractPerformanceColPayList);
		//??????????????????
		ContractRelieveEntity relieveEntity = relieveMapper.selectRelieveById(contractFormInfoResponseVO.getId());
		if (Func.isNotEmpty(relieveEntity)) {
			ContractRelieveResponseVO relieveResponseVO = ContractRelieveWrapper.build().entityPV(relieveEntity);
			if (Func.isNotEmpty(relieveEntity.getSigningBasis())) {
				relieveResponseVO.setAccordingEntity(contractAccordingMapper.selectById(relieveEntity.getSigningBasis()));
			}
			if (Func.isNotBlank(relieveEntity.getTermAgreement())) {
				//???????????????????????????????????????????????????????????????VO
				R<List<FileVO>> result = fileClient.getByIds(relieveResponseVO.getTermAgreement());
				if (result.isSuccess()) {
					relieveResponseVO.setTermAgreementFileVOList(result.getData());
				}
			}
			contractFormInfoResponseVO.setRelieveEntity(relieveResponseVO);
		}
		//????????????????????????????????????vo
		ContractAssessmentEntity contractAssessmentEntity = contractAssessmentMapper.selectByAssessmentId(contractFormInfoResponseVO.getId());
		if (Func.isNotEmpty(contractAssessmentEntity)) {
			ContractAssessmentResponseVO assessmentResponseVO = ContractAssessmentWrapper.build().entityPV(contractAssessmentEntity);
			//??????????????????
			if (!Func.isEmpty(assessmentResponseVO)) {
				if (Func.isNoneBlank(assessmentResponseVO.getAttachedFiles())) {
					R<List<FileVO>> result = fileClient.getByIds(assessmentResponseVO.getAttachedFiles());
					if (result.isSuccess()) {
						assessmentResponseVO.setAssessmentAttachedVOList(result.getData());
					}
				}
			}
			contractFormInfoResponseVO.setAssessmentEntity(assessmentResponseVO);
		}
		//???????????????????????????????????????????????????vo
		List<ContractArchiveNotEntity> archiveNotEntity = archiveNotMapper.selectArchiveNotById(contractFormInfoResponseVO.getId());
		contractFormInfoResponseVO.setArchiveNotEntity(archiveNotEntity);
		//????????????????????????????????????vo
		ContractArchiveEntity contractArchiveEntity = contractArchiveMapper.selectArchiveById(contractFormInfoResponseVO.getId());
		if (Func.isNotEmpty(contractArchiveEntity)) {
			ContractArchiveResponseVO archiveResponseVO = ContractArchiveWrapper.build().entityPV(contractArchiveEntity);
			BladeUser user = AuthUtil.getUser();
			Long userId = Long.valueOf(user.getUserId());
			Long deptId = Long.valueOf(AuthUtil.getDeptId());
			Date now = new Date();
			archiveResponseVO.setCreateUserName(UserCache.getUser(userId).getRealName());
			archiveResponseVO.setCreateDeptName(SysCache.getDeptName(deptId));
			archiveResponseVO.setCreateSystemTime(now);
			contractFormInfoResponseVO.setArchiveEntity(archiveResponseVO);
		}
		//???????????????????????????????????????vo
		if (Func.isEmpty(contractFormInfoResponseVO.getSealInfoEntity())) {
			ContractSealUsingInfoEntity sealUsingInfoEntity = sealUsingInfoMapper.selectUsingById(contractFormInfoResponseVO.getId());
			if (Func.isNotEmpty(sealUsingInfoEntity)) {
				ContractSealUsingInfoResponseVO sealUsingInfoResponseVO = ContractSealUsingInfoWrapper.build().entityPV(sealUsingInfoEntity);
				contractFormInfoResponseVO.setSealInfoEntity(sealUsingInfoResponseVO);
			}
		}
		//???????????????????????????????????????vo
		ContractSigningEntity signingEntity = signingMapper.selectSigningById(contractFormInfoResponseVO.getId());
		if (Func.isNotEmpty(signingEntity)) {
			ContractSigningResponseVO signingResponseVO = ContractSigningWrapper.build().entityPV(signingEntity);
			contractFormInfoResponseVO.setSigningEntity(signingResponseVO);
		}
		//??????????????????
		if (Func.isNoneBlank(contractFormInfoResponseVO.getTextFile())) {
			R<List<FileVO>> result = fileClient.getByIds(contractFormInfoResponseVO.getTextFile());
			if (result.isSuccess()) {
				contractFormInfoResponseVO.setTestFileVOList(result.getData());
			}
		}
		//??????????????????PDF
		if (Func.isNoneBlank(contractFormInfoResponseVO.getTextFilePdf())) {
			R<List<FileVO>> result = fileClient.getByIds(contractFormInfoResponseVO.getTextFilePdf());
			if (result.isSuccess()) {
				contractFormInfoResponseVO.setTestFileVOListPDF(result.getData());
			}
		}
		//??????????????????
		if (Func.isNoneBlank(contractFormInfoResponseVO.getAttachedFiles())) {
			R<List<FileVO>> result = fileClient.getByIds(contractFormInfoResponseVO.getAttachedFiles());
			if (result.isSuccess()) {
				contractFormInfoResponseVO.setAttachedFileVOList(result.getData());
			}
		}
		/* ??????????????? */

		if (!Func.isEmpty(contractFormInfoResponseVO.getCreateUser())) {
			/*User user = UserCache.getUser(entity.getCreateUser());*/
			User user = userClient.userInfoById(contractFormInfoResponseVO.getCreateUser()).getData();
			contractFormInfoResponseVO.setUserRealName(user.getRealName());
		}
		/* ????????????????????? */
		if (!Func.isEmpty(contractFormInfoResponseVO.getCreateDept())) {
			String dept = sysClient.getDeptName(contractFormInfoResponseVO.getCreateDept()).getData();
			contractFormInfoResponseVO.setUserDepartName(dept);
		}

		//?????????????????????
		if (!Func.isEmpty(signingEntity)) {
			if (Func.isNoneBlank(signingEntity.getTextFiles())) {
				R<List<FileVO>> result = fileClient.getByIds(signingEntity.getTextFiles());
				if (result.isSuccess()) {
					contractFormInfoResponseVO.setSigningTextFileVOList(result.getData());
				}
			}
		}
		//?????????????????????
		if (!Func.isEmpty(signingEntity)) {
			if (Func.isNoneBlank(signingEntity.getAttachedFiles())) {
				R<List<FileVO>> result = fileClient.getByIds(signingEntity.getAttachedFiles());
				if (result.isSuccess()) {
					contractFormInfoResponseVO.setSigningAttachedFileVOList(result.getData());
				}
			}
		}
		//??????????????????
		ContractChangeEntity changeEntity = changeMapper.selectById(id);
		if (Func.isNotEmpty(changeEntity)) {
			contractFormInfoResponseVO.setChangeEntity(changeEntity);
			if (Func.isNoneBlank(changeEntity.getSuppleAgreement())) {
				R<List<FileVO>> result = fileClient.getByIds(changeEntity.getSuppleAgreement());
				if (result.isSuccess()) {
					contractFormInfoResponseVO.setSuppleAgreementFileVOList(result.getData());
				}
			}
		}
		return contractFormInfoResponseVO;
	}

	/**
	 * ??????????????????????????????
	 *
	 * @param id
	 * @return
	 */
	@Override
	public ContractFormInfoResponseVO getByChangeHistoryId(Long id) {
		List<ContractFormInfoEntity> formInfoEntityList = new ArrayList<>();
		//??????????????????id????????????????????????
		ContractFormInfoEntity formInfoEntity = contractFormInfoMapper.selectById(id);
		List<ContractCounterpartEntity> contractCounterpartList = contractCounterpartMapper.selectByIds(formInfoEntity.getId());
		if (Func.isNotEmpty(contractCounterpartList)) {
			formInfoEntity.setCounterpart(contractCounterpartList);
			StringBuilder name = new StringBuilder();
			for (ContractCounterpartEntity counterpartEntity : contractCounterpartList) {
				name.append(counterpartEntity.getName());
				name.append(",");
			}
			name.substring(0, name.length());
			formInfoEntity.setCounterpartName(name.toString());
		}
		formInfoEntityList.add(formInfoEntity);
		while (Func.isNotEmpty(formInfoEntity.getChangeContractId())) {
			formInfoEntity = contractFormInfoMapper.selectById(formInfoEntity.getChangeContractId());
			//?????????????????????????????????
			contractCounterpartList = contractCounterpartMapper.selectByIds(formInfoEntity.getId());
			if (Func.isNotEmpty(contractCounterpartList)) {
				formInfoEntity.setCounterpart(contractCounterpartList);
				StringBuilder name = new StringBuilder();
				for (ContractCounterpartEntity counterpartEntity : contractCounterpartList) {
					name.append(counterpartEntity.getName());
					name.append(",");
				}
				name.substring(0, name.length());
				formInfoEntity.setCounterpartName(name.toString());
			}
			formInfoEntityList.add(formInfoEntity);
		}
		//?????????????????????vo
		ContractFormInfoResponseVO formInfoResponseVO = ContractFormInfoWrapper.build().entityPV(formInfoEntity);
		formInfoResponseVO.setFormInfosEntityOldVOList(formInfoEntityList);
		return formInfoResponseVO;
	}

	/**
	 * ????????????????????????
	 *
	 * @param id                 ??????id
	 * @param fileExportCount    ????????????
	 * @param fileExportCategory
	 * @return ??????????????????
	 */
	@Override
	public boolean textExportCount(Long id, Integer fileExportCount, String fileExportCategory) {
		return contractFormInfoMapper.textExportCount(id, fileExportCount, fileExportCategory);
	}


	/**
	 * ????????????????????????
	 *
	 * @param r ????????????
	 * @return ??????
	 */
	@Override
	public R<ContractFormInfoEntity> SingleSign(R<ContractFormInfoEntity> r) {
		ContractFormInfoEntity entity=r.getData();
		// ?????????????????? ??????
		// ??????????????????????????????
		UploadFileEntity uploadFileEntity = new UploadFileEntity();
		//??????????????????
		File filePDF = null;
		//????????????????????????
		File fileBH = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = df.format(new Date());
		//???????????????pdf??????
		if ("10".equals(entity.getContractSoure()) || "20".equals(entity.getContractSoure())) {
			List<FileVO> fileVO = fileClient.getByIds(entity.getTextFile()).getData();
			String newFileDoc = "";
			String newFilePdf = "";
			String suffix = "";
			//doc??????pdf
			if (CollectionUtil.isNotEmpty(fileVO)) {
				newFileDoc = fileVO.get(0).getLink();
				int index = fileVO.get(0).getName().lastIndexOf(".");
				suffix = fileVO.get(0).getName().substring(index + 1, fileVO.get(0).getName().length());
				//???????????????pdf?????????pdf?????????????????????
				if (!"pdf".equals(suffix)) {
					newFilePdf = ftlPath + fileVO.get(0).getName().substring(0, index) +date+ ".pdf";
					AsposeWordToPdfUtils.doc2pdf(newFileDoc, newFilePdf);
					filePDF = new File(newFilePdf);
				} else {
					filePDF = new File(ftlPath + fileVO.get(0).getName().substring(0, index) +date+ ".pdf");
					//?????????????????????
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(filePDF);
						fos.write(AsposeWordToPdfUtils.getUrlFileData(newFileDoc));
						fos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		} else {
			filePDF = new File(entity.getFilePDF());
		}
		InputStream in = null;
		try {
			in=new FileInputStream(filePDF);
			String fileId=AsposeWordToPdfUtils.addWaterMak(in,"????????????",filePDF.getName(),null);
			Thread.sleep(2000);
			String url=AsposeWordToPdfUtils.downloadFile(fileId);
			System.out.println(url);
			//?????????????????????
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(filePDF);
				fos.write(AsposeWordToPdfUtils.getUrlFileData(url));
			} catch (Exception e) {
				e.printStackTrace();
			}
			//?????? ?????????null
			assert fos != null;
			fos.close();
			in.close();
			//????????????
			List<ContractFormInfoEntity> list = this.selectByContractNumber(entity);
			//????????????
			final String[] FLCode = {null};
			R<List<DictBiz>> HTDL = bizClient.getList("HTDL");
			List<DictBiz> dataBiz = HTDL.getData();
			dataBiz.forEach(bz -> {
				if ((bz.getId().toString()).equals(entity.getContractBigCategory())) {
					FLCode[0] = bz.getRemark();
				}
			});
			//????????????????????????
			final String[] GSCode = {null};
			R<List<DictBiz>> seal = bizClient.getList("application_seal");
			dataBiz = seal.getData();
			dataBiz.forEach(bz -> {
				if ((bz.getDictValue()).equals(entity.getSealName())) {
					GSCode[0] = bz.getRemark();
				}
			});
			//???????????????????????????+1
			if (list.size() > 0) {
				entity.setContractNumber(redisCacheUtil.selectTaskNo(list.get(0).getContractNumber(), FLCode[0], GSCode[0]));
			} else {
				entity.setContractNumber(redisCacheUtil.selectTaskNo("", FLCode[0], GSCode[0]));
			}
			String BH=ftlPath + "BH-"+filePDF.getName();
			AsposeWordToPdfUtils.addWaterMark(filePDF.getPath(),BH,entity.getContractNumber());
			fileBH = new File(BH);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*R<FileVO> filePDFVO = null;
		try {
			MultipartFile multipartFile = new MockMultipartFile("file", filePDF.getName(),
				ContentType.MULTIPART.toString(), new FileInputStream(fileBH));
			filePDFVO = fileClient.save(multipartFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*//* ???????????? *//*
		assert filePDFVO != null;
		entity.setTextFilePdf(filePDFVO.getData().getId() + ",");*/
		// ????????????file??????
		List<File> files = new ArrayList<File>();
		files.add(fileBH);
		uploadFileEntity.setFile(files);
		// ??????????????????,????????????????????????????????????
		uploadFileEntity.setIsMerge("0");
		// ?????????????????? ?????????????????? ??????????????????????????????token?????????, ???????????????????????????????????????????????????,????????????????????????????????????,???????????????blade-abutment???resources.application???
		List<UploadFileVo> uploadFileVoList = abutmentClient.uploadFiles(uploadFileEntity).getData();
		// ?????????????????? ??????
		//epk????????????
		entity.setTextFilePdf(uploadFileVoList.get(0).getId());
		MultipartFile multipartFile = null;
		try {
			multipartFile = new MockMultipartFile("file", fileBH.getName(),
				ContentType.MULTIPART.toString(), new FileInputStream(fileBH));
		} catch (IOException e) {
			e.printStackTrace();
		}
		/* ???????????? */
		R<FileVO> fileVO = fileClient.save(multipartFile);
		entity.setOtherInformation(fileVO.getData().getLink());
		R<EkpVo> ekpVo = abutmentClient.sendEkpFormPost(entity);
		if(ekpVo.getCode()==200){
			entity.setRelContractId(ekpVo.getData().getDoc_info());
		}else{
			r.setMsg(ekpVo.getMsg());
			r.setSuccess(false);
		}
		System.out.println("ekp?????????code"+ekpVo.getCode());
		System.out.println("ekp???????????????ID"+ekpVo.getData().getDoc_info());
		System.out.println("ekp?????????ID"+entity.getRelContractId());
		/*R<EkpVo> ekpVo=new R<EkpVo>();
		ekpVo.setCode(0);
		entity.setRelContractId("123");*/
		r.setData(entity);
		r.setCode(ekpVo.getCode());
		return r;
	}

	/**
	 * ??????????????????
	 *
	 * @param json ????????????
	 */
	@Override
	public ContractFormInfoEntity templateDraft(ContractFormInfoEntity contractFormInfo, String json) {
		//???Json??????????????????
		List<TemplateFieldJsonEntity> templateFieldList = JSON.parseArray(json, TemplateFieldJsonEntity.class);
		for (TemplateFieldJsonEntity templateField : templateFieldList) {
            /*if (ContractFormInfoTemplateContract.CONTRACT_ID.equals(templateField.getComponentType())) {
                templateField.setFieldValue(contractFormInfo.getId().toString());
            }*/
			//???????????????????????????????????????
			if (ContractFormInfoTemplateContract.COMPONENT_TYPE_SELECT.equals(templateField.getComponentType())) {
				Object object = JSON.parseObject(templateField.getSecondSelectData(), Object.class);
				if (null != object) {
					templateField.setSecondSelectDataObject(object);
				}
			}
			//?????????????????????????????????
			if (ContractFormInfoTemplateContract.COMPONENT_TYPE_REQUORED.equals(templateField.getRequired())) {
				List<Object> objectList = JSON.parseArray(templateField.getRequiredData(), Object.class);
				if (CollectionUtil.isNotEmpty(objectList)) {
					templateField.setRequiredDataList(objectList);
				}
			}
			//???????????????????????????
			if (ContractFormInfoTemplateContract.COMPONENT_TYPE_SMALL.contains(templateField.getComponentType())) {
				List<Object> objectList = JSON.parseArray(templateField.getDicData(), Object.class);
				if (CollectionUtil.isNotEmpty(objectList)) {
					templateField.setDicDataList(objectList);
				}
			}
			if (ContractFormInfoTemplateContract.COMPONENT_TYPE.contains(templateField.getComponentType())) {
                /*if (ContractFormInfoTemplateContract.CONTRACT_ACCORDING.equals(templateField.getRelationCode())) {
                    List<ContractAccordingEntity> accordingList = JSON.parseArray(templateField.getTableData(), ContractAccordingEntity.class);
                    *//*??????????????????*//*
                    if (CollectionUtil.isNotEmpty(accordingList)) {
                        ContractAccordingEntity contractAccording = accordingList.get(0);
                        contractAccording.setContractId(contractFormInfo.getId());
                        //contractAccordingMapper.insert(contractAccording);
                        accordingList.get(0).setId(contractAccording.getId());
                        templateField.setTableData(JSONObject.toJSONString(accordingList));
                        templateField.setTableDataList(accordingList);
                    }
					contractFormInfo.setAccording(accordingList);
                }
                //??????????????????????????????????????????
                if (ContractFormInfoTemplateContract.CONTRACT_COUNTERPART.equals(templateField.getRelationCode())) {
                    //?????????????????????JSONObject
                    JSONObject obj = JSON.parseObject(templateField.getTableDataObject());
                    //???????????????????????????
                    if (!"{}".equals(obj.getString(ContractFormInfoTemplateContract.CONTRACT_COUNTERPART_SUB_COUNTERPART))) {
                        com.alibaba.fastjson.JSONArray counterpartObject = obj.getJSONArray(ContractFormInfoTemplateContract.CONTRACT_COUNTERPART_SUB_COUNTERPART);
                        List<ContractCounterpartEntity> contractCounterpart = JSON.parseArray(counterpartObject.toString(), ContractCounterpartEntity.class);
                        if (contractCounterpart.size() > 0) {
                            contractFormInfoMapper.deleteCounterpart(contractFormInfo.getId());
                            contractFormInfoMapper.saveCounterpart(contractFormInfo.getId(), contractCounterpart);
                            obj.put(ContractFormInfoTemplateContract.CONTRACT_COUNTERPART_SUB_COUNTERPART, contractCounterpart);
                        }
						contractFormInfo.setCounterpart(contractCounterpart);
                        //???????????????????????????
                        if (!"{}".equals(obj.getString(ContractFormInfoTemplateContract.CONTRACT_COUNTERPART_SUB_CONTRACTBOND))) {
                            com.alibaba.fastjson.JSONArray contractBondArry = obj.getJSONArray(ContractFormInfoTemplateContract.CONTRACT_COUNTERPART_SUB_CONTRACTBOND);
                            List<ContractBondEntity> contractBond = JSON.parseArray(contractBondArry.toString(), ContractBondEntity.class);
							contractFormInfo.setContractBond(contractBond);
                            *//*?????????????????????*//*
                            if (CollectionUtil.isNotEmpty(contractBond)) {
                                List<Long> list = new ArrayList<>();
                                List<ContractBondEntity> bondList = new ArrayList<>();
                                ContractBondPlanEntity contractBondPlan = new ContractBondPlanEntity();
                                //???????????????????????????
                                contractBondService.deleteByContractId(contractFormInfo.getId());
                                //????????????????????????????????????
                                contractBondPlanService.deleteByContractId(contractFormInfo.getId());
                                for (ContractBondEntity contractBondEntity : contractBond) {
                                    BeanUtil.copy(contractBondEntity, contractBondPlan);
                                    if (Func.isEmpty(contractBondEntity.getId())) {
                                        contractBondService.save(contractBondEntity);
                                    }
                                    bondList.add(contractBondEntity);
                                    //???????????????????????????
                                    contractBondPlan.setContractId(contractFormInfo.getId());
                                    contractBondPlan.setId(null);
                                    contractBondPlanService.save(contractBondPlan);
                                    list.add(contractBondEntity.getId());
                                }
                                contractBondService.saveBond(list, contractFormInfo.getId());
                                obj.put(ContractFormInfoTemplateContract.CONTRACT_COUNTERPART_SUB_CONTRACTBOND, bondList);
                            }
                        }
                        templateField.setTableDataObject(JSONObject.toJSONString(obj));
                        templateField.setTableDataObjectList(obj);
                    }
                }
                //*??????????????????
                if (ContractFormInfoTemplateContract.CONTRACT_PERFORMANCE.equals(templateField.getRelationCode())) {
                    List<ContractPerformanceEntity> performanceList = JSON.parseArray(templateField.getTableData(), ContractPerformanceEntity.class);
                    if (CollectionUtil.isNotEmpty(performanceList)) {
                        performanceMapper.deleteByContractId(contractFormInfo.getId());
                        List<ContractPerformanceEntity> list = new ArrayList<>();
                        performanceList.forEach(performance -> {
                            performance.setContractId(contractFormInfo.getId());
                            contractPerformanceMapper.insert(performance);
                            list.add(performance);
                        });
                        templateField.setTableData(JSONObject.toJSONString(list));
                        templateField.setTableDataList(list);
                    }
                }
                //*???????????????????????????
                if (ContractFormInfoTemplateContract.CONTRACT_PERFORMANCE_COLPAY.equals(templateField.getRelationCode())) {
                    List<ContractPerformanceColPayEntity> performanceColPayList = JSON.parseArray(templateField.getTableData(), ContractPerformanceColPayEntity.class);
                    if (CollectionUtil.isNotEmpty(performanceColPayList)) {
                        performanceColPayMapper.deleteByContractId(contractFormInfo.getId());
                        List<ContractPerformanceColPayEntity> list = new ArrayList<>();
                        performanceColPayList.forEach(performanceColPay -> {
                            performanceColPay.setContractId(contractFormInfo.getId());
                            contractPerformanceColPayMapper.insert(performanceColPay);
                            list.add(performanceColPay);
                        });
                        templateField.setTableData(JSONObject.toJSONString(list));
                        templateField.setTableDataList(list);
                    }
                }*/
				//*???????????????????????????
				if (ContractFormInfoTemplateContract.CONTRACT_YWLANEWDISPLAY1.equals(templateField.getRelationCode())) {
					List<YwlANewDisplay1ResponseVO> ywlANewDisplay1List = JSON.parseArray(templateField.getTableData(), YwlANewDisplay1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(ywlANewDisplay1List)) {
						ywlANewDisplay1Service.saveBatchByRefId(contractFormInfo.getId(), ywlANewDisplay1List);
						List<YwlANewDisplay1ResponseVO> list = ywlANewDisplay1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*????????????????????????????????????
				if (ContractFormInfoTemplateContract.CONTRACT_CGLCATEGORYSALESCONTRACTS1.equals(templateField.getRelationCode())) {
					List<CglCategorySalesContracts1ResponseVO> cglCategorySalesContracts1List = JSON.parseArray(templateField.getTableData(), CglCategorySalesContracts1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(cglCategorySalesContracts1List)) {
						cglCategorySalesContracts1Service.saveBatchByRefId(contractFormInfo.getId(), cglCategorySalesContracts1List);
						List<CglCategorySalesContracts1ResponseVO> list = cglCategorySalesContracts1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*???????????????????????????--?????????????????????
				if (ContractFormInfoTemplateContract.CONTRACT_CGLTHESALESCONTRACT1.equals(templateField.getRelationCode())) {
					List<CglTheSalesContract1ResponseVO> cglTheSalesContract1List = JSON.parseArray(templateField.getTableData(), CglTheSalesContract1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(cglTheSalesContract1List)) {
						cglTheSalesContract1Service.saveBatchByRefId(contractFormInfo.getId(), cglTheSalesContract1List);
						List<CglTheSalesContract1ResponseVO> list = cglTheSalesContract1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????-??????????????????
				if (ContractFormInfoTemplateContract.CONTRACT_CGLRAWMATERIALS1.equals(templateField.getRelationCode())) {
					List<CglRawMaterials1ResponseVO> cglRawMaterials1List = JSON.parseArray(templateField.getTableData(), CglRawMaterials1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(cglRawMaterials1List)) {
						cglRawMaterials1Service.saveBatchByRefId(contractFormInfo.getId(), cglRawMaterials1List);
						List<CglRawMaterials1ResponseVO> list = cglRawMaterials1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????-??????????????????
				if (ContractFormInfoTemplateContract.CONTRACT_CGLRAWMATERIALS1.equals(templateField.getRelationCode())) {
					List<SclEquipmentMaintenance1ResponseVO> sclEquipmentMaintenance1List = JSON.parseArray(templateField.getTableData(), SclEquipmentMaintenance1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(sclEquipmentMaintenance1List)) {
						sclEquipmentMaintenance1Service.saveBatchByRefId(contractFormInfo.getId(), sclEquipmentMaintenance1List);
						List<SclEquipmentMaintenance1ResponseVO> list = sclEquipmentMaintenance1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*???????????????????????? ???????????????????????????
				if (ContractFormInfoTemplateContract.CONTRACT_MTADAPTATIONCONTRACT1.equals(templateField.getRelationCode())) {
					List<MtlAdaptationContract1ResponseVO> mtlAdaptationContract1List = JSON.parseArray(templateField.getTableData(), MtlAdaptationContract1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtlAdaptationContract1List)) {
						mtlAdaptationContract1Service.saveBatchByRefId(contractFormInfo.getId(), mtlAdaptationContract1List);
						List<MtlAdaptationContract1ResponseVO> list = mtlAdaptationContract1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*???????????????????????? ?????????????????????
				if (ContractFormInfoTemplateContract.CONTRACT_MTADAPTATIONCONTRACT2.equals(templateField.getRelationCode())) {
					List<MtlAdaptationContract2ResponseVO> mtlAdaptationContract1List = JSON.parseArray(templateField.getTableData(), MtlAdaptationContract2ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtlAdaptationContract1List)) {
						mtlAdaptationContract2Service.saveBatchByRefId(contractFormInfo.getId(), mtlAdaptationContract1List);
						List<MtlAdaptationContract2ResponseVO> list = mtlAdaptationContract2Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????????????????????????? ???????????????????????????1
				if (ContractFormInfoTemplateContract.CONTRACT_MTLSHOOTINGANDPRODUCTIONCONTRACT1.equals(templateField.getRelationCode())) {
					List<MtlShootingAndProductionContract1ResponseVO> mtlShootingAndProductionContract1List = JSON.parseArray(templateField.getTableData(), MtlShootingAndProductionContract1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtlShootingAndProductionContract1List)) {
						mtlShootingAndProductionContract1Service.saveBatchByRefId(contractFormInfo.getId(), mtlShootingAndProductionContract1List);
						List<MtlShootingAndProductionContract1ResponseVO> list = mtlShootingAndProductionContract1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????????????????????????? ???????????????????????????2
				if (ContractFormInfoTemplateContract.CONTRACT_MTLSHOOTINGANDPRODUCTIONCONTRACT2.equals(templateField.getRelationCode())) {
					List<MtlShootingAndProductionContract2ResponseVO> mtlShootingAndProductionContract2List = JSON.parseArray(templateField.getTableData(), MtlShootingAndProductionContract2ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtlShootingAndProductionContract2List)) {
						mtlShootingAndProductionContract2Service.saveBatchByRefId(contractFormInfo.getId(), mtlShootingAndProductionContract2List);
						List<MtlShootingAndProductionContract2ResponseVO> list = mtlShootingAndProductionContract2Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????????????????????????? ???????????????????????????3
				if (ContractFormInfoTemplateContract.CONTRACT_MTLSHOOTINGANDPRODUCTIONCONTRACT3.equals(templateField.getRelationCode())) {
					List<MtlShootingAndProductionContract3ResponseVO> mtlShootingAndProductionContract3List = JSON.parseArray(templateField.getTableData(), MtlShootingAndProductionContract3ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtlShootingAndProductionContract3List)) {
						mtlShootingAndProductionContract3Service.saveBatchByRefId(contractFormInfo.getId(), mtlShootingAndProductionContract3List);
						List<MtlShootingAndProductionContract3ResponseVO> list = mtlShootingAndProductionContract3Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????????????????????????? ?????????
				if (ContractFormInfoTemplateContract.CONTRACT_SCLPROJECTOUTSOURCING1.equals(templateField.getRelationCode())) {
					List<SclProjectOutsourcing1ResponseVO> sclProsjectOutsourcing1List = JSON.parseArray(templateField.getTableData(), SclProjectOutsourcing1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(sclProsjectOutsourcing1List)) {
						sclProjectOutsourcing1Service.saveBatchByRefId(contractFormInfo.getId(), sclProsjectOutsourcing1List);
						List<SclProjectOutsourcing1ResponseVO> list = sclProjectOutsourcing1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????????????????????????????????????????1
				if (ContractFormInfoTemplateContract.CONTRACT_SCLCONSTRUCTIONPROJECT1.equals(templateField.getRelationCode())) {
					List<SclConstructionProject1ResponseVO> sclConstructionProject1List = JSON.parseArray(templateField.getTableData(), SclConstructionProject1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(sclConstructionProject1List)) {
						sclConstructionProject1Service.saveBatchByRefId(contractFormInfo.getId(), sclConstructionProject1List);
						List<SclConstructionProject1ResponseVO> list = sclConstructionProject1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????????????????????????????????????????2
				if (ContractFormInfoTemplateContract.CONTRACT_SCLCONSTRUCTIONPROJECT2.equals(templateField.getRelationCode())) {
					List<SclConstructionProject2ResponseVO> sclConstructionProject2List = JSON.parseArray(templateField.getTableData(), SclConstructionProject2ResponseVO.class);
					if (CollectionUtil.isNotEmpty(sclConstructionProject2List)) {
						sclConstructionProject2Service.saveBatchByRefId(contractFormInfo.getId(), sclConstructionProject2List);
						List<SclConstructionProject2ResponseVO> list = sclConstructionProject2Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????????????????????????????????????????3
				if (ContractFormInfoTemplateContract.CONTRACT_SCLCONSTRUCTIONPROJECT3.equals(templateField.getRelationCode())) {
					List<SclConstructionProject3ResponseVO> sclConstructionProject3List = JSON.parseArray(templateField.getTableData(), SclConstructionProject3ResponseVO.class);
					if (CollectionUtil.isNotEmpty(sclConstructionProject3List)) {
						sclConstructionProject3Service.saveBatchByRefId(contractFormInfo.getId(), sclConstructionProject3List);
						List<SclConstructionProject3ResponseVO> list = sclConstructionProject3Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????????????? ?????????1
				if (ContractFormInfoTemplateContract.CONTRACT_MTLAUDIOPRODUCTIONCONTRACT1.equals(templateField.getRelationCode())) {
					List<MtlAudioProductionContract1ResponseVO> mtlAudioProductionContract1List = JSON.parseArray(templateField.getTableData(), MtlAudioProductionContract1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtlAudioProductionContract1List)) {
						mtlAudioProductionContract1Service.saveBatchByRefId(contractFormInfo.getId(), mtlAudioProductionContract1List);
						List<MtlAudioProductionContract1ResponseVO> list = mtlAudioProductionContract1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????????????? ?????????2
				if (ContractFormInfoTemplateContract.CONTRACT_MTLAUDIOPRODUCTIONCONTRACT2.equals(templateField.getRelationCode())) {
					List<MtlAudioProductionContract2ResponseVO> mtlAudioProductionContract2List = JSON.parseArray(templateField.getTableData(), MtlAudioProductionContract2ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtlAudioProductionContract2List)) {
						mtlAudioProductionContract2Service.saveBatchByRefId(contractFormInfo.getId(), mtlAudioProductionContract2List);
						List<MtlAudioProductionContract2ResponseVO> list = mtlAudioProductionContract2Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????????????????1
				if (ContractFormInfoTemplateContract.CONTRACT_MTLEDITEDTHECONTRACT1.equals(templateField.getRelationCode())) {
					List<MtlEditedTheContract1ResponseVO> mtlEditedTheContract1List = JSON.parseArray(templateField.getTableData(), MtlEditedTheContract1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtlEditedTheContract1List)) {
						mtlEditedTheContract1Service.saveBatchByRefId(contractFormInfo.getId(), mtlEditedTheContract1List);
						List<MtlEditedTheContract1ResponseVO> list = mtlEditedTheContract1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*???????????????????????????1
				if (ContractFormInfoTemplateContract.CONTRACT_MTLVIDEOPRODUCTIONCONTRACT1.equals(templateField.getRelationCode())) {
					List<MtlVideoProductionContract1ResponseVO> mtlVideoProductionContract1List = JSON.parseArray(templateField.getTableData(), MtlVideoProductionContract1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtlVideoProductionContract1List)) {
						mtlVideoProductionContract1Service.saveBatchByRefId(contractFormInfo.getId(), mtlVideoProductionContract1List);
						List<MtlVideoProductionContract1ResponseVO> list = mtlVideoProductionContract1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*???????????????????????????2
				if (ContractFormInfoTemplateContract.CONTRACT_MTLVIDEOPRODUCTIONCONTRACT2.equals(templateField.getRelationCode())) {
					List<MtlVideoProductionContract2ResponseVO> mtlVideoProductionContract2List = JSON.parseArray(templateField.getTableData(), MtlVideoProductionContract2ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtlVideoProductionContract2List)) {
						mtlVideoProductionContract2Service.saveBatchByRefId(contractFormInfo.getId(), mtlVideoProductionContract2List);
						List<MtlVideoProductionContract2ResponseVO> list = mtlVideoProductionContract2Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????????????????????????????????????????1
				if (ContractFormInfoTemplateContract.CONTRACT_CGLSALESCONTRACT1ENTITY.equals(templateField.getRelationCode())) {
					List<CglSalesContract1ResponseVO> cglSalesContract1List = JSON.parseArray(templateField.getTableData(), CglSalesContract1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(cglSalesContract1List)) {
						cglSalesContract1Service.saveBatchByRefId(contractFormInfo.getId(), cglSalesContract1List);
						List<CglSalesContract1ResponseVO> list = cglSalesContract1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*??????????????????????????????????????????1???
				if (ContractFormInfoTemplateContract.CONTRACT_MTBPRODUCTIONCONTRACT1.equals(templateField.getRelationCode())) {
					List<MtbProductionContract1ResponseVO> mtlVideoProductionContract1List = JSON.parseArray(templateField.getTableData(), MtbProductionContract1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtlVideoProductionContract1List)) {
						mtbProductionContract1Service.saveBatchByRefId(contractFormInfo.getId(), mtlVideoProductionContract1List);
						List<MtbProductionContract1ResponseVO> list = mtbProductionContract1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*??????????????????????????????????????????2???
				if (ContractFormInfoTemplateContract.CONTRACT_MTBPRODUCTIONCONTRACT2.equals(templateField.getRelationCode())) {
					List<MtbProductionContract2ResponseVO> mtlVideoProductionContract2List = JSON.parseArray(templateField.getTableData(), MtbProductionContract2ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtlVideoProductionContract2List)) {
						mtbProductionContract2Service.saveBatchByRefId(contractFormInfo.getId(), mtlVideoProductionContract2List);
						List<MtbProductionContract2ResponseVO> list = mtbProductionContract2Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*??????????????????????????????????????????3???
				if (ContractFormInfoTemplateContract.CONTRACT_MTBPRODUCTIONCONTRACT3.equals(templateField.getRelationCode())) {
					List<MtbProductionContract3ResponseVO> mtlVideoProductionContract3List = JSON.parseArray(templateField.getTableData(), MtbProductionContract3ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtlVideoProductionContract3List)) {
						mtbProductionContract3Service.saveBatchByRefId(contractFormInfo.getId(), mtlVideoProductionContract3List);
						List<MtbProductionContract3ResponseVO> list = mtbProductionContract3Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*????????????????????????(?????????1???
				if (ContractFormInfoTemplateContract.CONTRACT_SCLEQUIOMENTMAINTENANCE1.equals(templateField.getRelationCode())) {
					List<SclEquipmentMaintenance1ResponseVO> sclEquipmentMaintenance1List = JSON.parseArray(templateField.getTableData(), SclEquipmentMaintenance1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(sclEquipmentMaintenance1List)) {
						sclEquipmentMaintenance1Service.saveBatchByRefId(contractFormInfo.getId(), sclEquipmentMaintenance1List);
						List<SclEquipmentMaintenance1ResponseVO> list = sclEquipmentMaintenance1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*?????????_???????????????(?????????1???
				if (ContractFormInfoTemplateContract.CONTRACT_CGLPROOFINGCONTRACT1.equals(templateField.getRelationCode())) {
					List<CglProofingContract1ResponseVO> CglProofingContract1List = JSON.parseArray(templateField.getTableData(), CglProofingContract1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(CglProofingContract1List)) {
						cglProofingContract1Service.saveBatchByRefId(contractFormInfo.getId(), CglProofingContract1List);
						List<CglProofingContract1ResponseVO> list = cglProofingContract1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*??????????????????????????????(?????????1???
				if (ContractFormInfoTemplateContract.CONTRACT_PRODUCTOUTSERVICECONTRACT1.equals(templateField.getRelationCode())) {
					List<ProductOutServiceContract1ResponseVO> productOutServiceContract1ResponseVOList = JSON.parseArray(templateField.getTableData(), ProductOutServiceContract1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(productOutServiceContract1ResponseVOList)) {
						productOutServiceContract1Service.saveBatchByRefId(contractFormInfo.getId(), productOutServiceContract1ResponseVOList);
						List<ProductOutServiceContract1ResponseVO> list = productOutServiceContract1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*??????????????????????????????(?????????2???
				if (ContractFormInfoTemplateContract.CONTRACT_PRODUCTOUTSERVICECONTRACT2.equals(templateField.getRelationCode())) {
					List<ProductOutServiceContract2ResponseVO> productOutServiceContract2ResponseVOList = JSON.parseArray(templateField.getTableData(), ProductOutServiceContract2ResponseVO.class);
					if (CollectionUtil.isNotEmpty(productOutServiceContract2ResponseVOList)) {
						productOutServiceContract2Service.saveBatchByRefId(contractFormInfo.getId(), productOutServiceContract2ResponseVOList);
						List<ProductOutServiceContract2ResponseVO> list = productOutServiceContract2Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
				//*??????????????????????????????(?????????3???
				if (ContractFormInfoTemplateContract.CONTRACT_PRODUCTOUTSERVICECONTRACT3.equals(templateField.getRelationCode())) {
					List<ProductOutServiceContract3ResponseVO> productOutServiceContract3ResponseVOList = JSON.parseArray(templateField.getTableData(), ProductOutServiceContract3ResponseVO.class);
					if (CollectionUtil.isNotEmpty(productOutServiceContract3ResponseVOList)) {
						productOutServiceContract3Service.saveBatchByRefId(contractFormInfo.getId(), productOutServiceContract3ResponseVOList);
						List<ProductOutServiceContract3ResponseVO> list = productOutServiceContract3Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
//                //*??????????????????(?????????1???
//                if (ContractFormInfoTemplateContract.CONTRACT_BUSSERVICECONTRACT1.equals(templateField.getRelationCode())) {
//                    List<BusServiceContract1ResponseVO> busServiceContract1ResponseVOList = JSON.parseArray(templateField.getTableData(), BusServiceContract1ResponseVO.class);
//                    if (CollectionUtil.isNotEmpty(busServiceContract1ResponseVOList)) {
//                        busServiceContract1Service.saveBatchByRefId(contractFormInfo.getId(), busServiceContract1ResponseVOList);
//                        List<BusServiceContract1ResponseVO> list = busServiceContract1Service.selectRefList(contractFormInfo.getId());
//                        templateField.setTableData(JSONObject.toJSONString(list));
//                        templateField.setTableDataList(list);
//                    }
//                }
				//*?????????????????????+?????????(?????????1???
				if (ContractFormInfoTemplateContract.CONTRAT_IMTBMARKETRESEARCHCONTRACT1.equals(templateField.getRelationCode())) {
					List<MtbMarketResearchContract1ResponseVO> mtbMarketResearchContract1ResponseVOList = JSON.parseArray(templateField.getTableData(), MtbMarketResearchContract1ResponseVO.class);
					if (CollectionUtil.isNotEmpty(mtbMarketResearchContract1ResponseVOList)) {
						iMtbMarketResearchContract1Service.saveBatchByRefId(contractFormInfo.getId(), mtbMarketResearchContract1ResponseVOList);
						List<MtbMarketResearchContract1ResponseVO> list = iMtbMarketResearchContract1Service.selectRefList(contractFormInfo.getId());
						templateField.setTableData(JSONObject.toJSONString(list));
						templateField.setTableDataList(list);
					}
				}
			}
		}
		contractFormInfo.setJson(toJSONString(templateFieldList));
		return contractFormInfo;
	}

	//??????????????????????????????
	public String toJSONString(List<TemplateFieldJsonEntity> templateFieldList) {
		//??????????????????json?????????
		String json = JSON.toJSONString(templateFieldList);
		//?????????json???????????????????????????
		com.alibaba.fastjson.JSONArray jsonArr = JSON.parseArray(json);
		JSONArray jsonArray = new JSONArray();
		JSONArray array = new JSONArray();
		for (int i = 0; i < jsonArr.size(); i++) {
			JSONObject jsonObject2 = jsonArr.getJSONObject(i);
			if (null != (jsonObject2.get("secondSelectDataObject"))) {
				jsonObject2.put("secondSelectData", jsonObject2.get("secondSelectDataObject"));
				jsonObject2.remove("secondSelectDataObject");
			}
			if (null != (jsonObject2.get("tableDataList"))) {
				jsonObject2.put("tableData", jsonObject2.get("tableDataList"));
				jsonObject2.remove("tableDataList");
			}
			if (null != (jsonObject2.get("requiredDataList"))) {
				jsonObject2.put("requiredData", jsonObject2.get("requiredDataList"));
				jsonObject2.remove("requiredDataList");
			}
			if (null != (jsonObject2.get("dicDataList"))) {
				jsonObject2.put("dicData", jsonObject2.get("dicDataList"));
				jsonObject2.remove("dicDataList");
			}
			if (null != (jsonObject2.get("tableDataObjectList"))) {
				jsonObject2.put("tableDataObject", jsonObject2.get("tableDataObjectList"));
				jsonObject2.remove("tableDataObjectList");
			}
			if ("[]".equals(jsonObject2.get("tableData"))) {
				jsonObject2.put("tableData", array);
			}
			//????????????????????????
			if ("selectMany".equals(jsonObject2.get("componentType"))&&null != (jsonObject2.get("fieldValue"))) {
				String selectManyString=jsonObject2.get("fieldValue").toString();
				String[] selectManyAry=selectManyString.substring(1,selectManyString.length()-1).replaceAll("\"", "").split(",");
				jsonObject2.put("fieldValue", selectManyAry);
			}
			jsonArray.add(jsonObject2);
		}
		json = jsonArray.toJSONString();
		return json;
	}

	/**
	 * ??????????????????
	 *
	 * @return list
	 */
	@Override
	public List<ContractFormInfoEntity> getAmountList() {
		return contractFormInfoMapper.getAmountList();
	}

	/**
	 * ??????????????????
	 *
	 * @return list
	 */
	@Override
	public List<ContractFormInfoEntity> getNumList() {
		return contractFormInfoMapper.getNumList();
	}

	/**
	 * ????????????????????????????????????
	 *
	 * @return list
	 */
	@Override
	public List<ContractFormInfoEntity> getChooseList() {
		return contractFormInfoMapper.getChooseList();
	}

	/**
	 * ????????????????????????
	 *
	 * @return list
	 */
	@Override
	public List<ContractFormInfoEntity> selectByContractNumber(ContractFormInfoEntity entity) {
		return contractFormInfoMapper.selectByContractNumber(entity);
	}


	/**
	 * ??????????????????
	 *
	 * @return list
	 */
	@Override
	public R singleSignIsNot(ContractFormInfoRequestVO contractFormInfo, FileVO fileVO) {
		for (ContractCounterpartEntity counterpart : contractFormInfo.getCounterpart()) {
			// ????????????????????????????????????
			CompanyInfoEntity companyInfoEntity = new CompanyInfoEntity();
			companyInfoEntity.setQueryType("1");
			// ??????????????????  ?????????????????????  ????????????
			companyInfoEntity.setOrganCode(counterpart.getUnifiedSocialCreditCode());
			// ?????????null??????,?????????????????????,??????????????????available???1??????????????????,0?????????
			CompanyInfoVo companyInfoVo = abutmentClient.queryCompanyInfo(companyInfoEntity).getData();
			if (companyInfoVo == null) {
				companyInfoVo = abutmentClient.queryCompanyInfo(companyInfoEntity).getData();
			}
			//?????????0????????????????????????
			if((!"0".equals(companyInfoVo.getOrganCode()))&&("1".equals(contractFormInfo.getContractForm()))) {
					return R.data(1, counterpart.getName(), counterpart.getName() + "??????????????????????????????????????????");
			}
			if(("0".equals(companyInfoVo.getOrganCode()))&&("2".equals(contractFormInfo.getContractForm()))){
				return R.data(1, counterpart.getName(), counterpart.getName() + "??????????????????????????????????????????-????????????");
			}
		}
		String newFileDoc = "";
		String newFilePdf = "";
		String suffix = "";
		File filePDF = null;
		//doc??????pdf
		if (!Func.isEmpty(fileVO)) {
			newFileDoc = fileVO.getLink();
			int index = fileVO.getName().lastIndexOf(".");
			suffix = fileVO.getName().substring(index + 1, fileVO.getName().length());
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String date = df.format(new Date());
			//???????????????pdf?????????pdf?????????????????????
			if (!"pdf".equals(suffix)) {
				//??????????????????

				newFilePdf = ftlPath + fileVO.getName().substring(0, index) + date + ".pdf";
				AsposeWordToPdfUtils.doc2pdf(newFileDoc, newFilePdf);
				filePDF = new File(newFilePdf);
			} else {
				filePDF = new File(ftlPath + fileVO.getName().substring(0, index) + date + ".pdf");
				//?????????????????????
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(filePDF);
					fos.write(AsposeWordToPdfUtils.getUrlFileData(newFileDoc));
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					try {
						if (fos != null) {
							fos.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			InputStream input = null;
			PDDocument document = null;
			try {
				input = new FileInputStream(filePDF);
				// ????????????PDF???????????????
				PDFParser parser = new PDFParser(input);
				parser.parse();
				document = parser.getPDDocument();
				PDFTextStripper stripper = new PDFTextStripper();
				String text = stripper.getText(document);
				int az = text.indexOf("??????????????????");
				int bz = text.indexOf("??????????????????");
				int ay = text.indexOf("??????(??????)");
				int by = text.indexOf("??????(??????)");
				if ((-1 == az || -1 == bz) && (-1 == ay || -1 == by)) {
					return R.data(2, "?????????????????????", "?????????????????????");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if (document != null) {
						document.close();
					}
					if (input != null) {
						input.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return R.data(0, "??????", "??????");
		} else {
			return R.data(2, "?????????????????????", "?????????????????????");
		}
	}

	@Override
	public ContractFormInfoEntity selectByChangeId(Long id) {
		return contractFormInfoMapper.selectByChangeId(id);
	}
}
