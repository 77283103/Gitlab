package org.springblade.contract.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.contract.entity.ContractCounterpartEntity;
import org.springblade.contract.entity.ContractSealUsingInfoEntity;
import org.springblade.contract.entity.ContractSigningArchiveEntity;
import org.springblade.contract.entity.ContractSigningEntity;
import org.springblade.contract.mapper.ContractCounterpartMapper;
import org.springblade.contract.service.*;
import org.springblade.contract.vo.ContractFormInfoResponseVO;
import org.springblade.contract.vo.ContractSigningArchiveRequestVO;
import org.springblade.contract.vo.ContractSigningRequestVO;
import org.springblade.contract.vo.ContractSigningResponseVO;
import org.springblade.contract.wrapper.ContractSigningArchiveWrapper;
import org.springblade.contract.wrapper.ContractSigningWrapper;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.Charsets;
import org.springblade.core.tool.utils.Func;
import org.springblade.system.cache.SysCache;
import org.springblade.system.feign.IDictBizClient;
import org.springblade.system.user.cache.UserCache;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * ??????????????? ?????????
 *
 * @author : liyj
 * @date : 2020-09-23 19:27:05
 */
@RestController
@AllArgsConstructor
@RequestMapping("/signing")
@Api(value = "???????????????", tags = "???????????????")
public class ContractSigningController extends BladeController {

    private IDictBizClient bizClient;
    private IContractSealUsingInfoService sealUsingInfoService;
    private IContractSigningService contractSigningService;
    private IContractFormInfoService contractFormInfoService;
    private IContractCounterpartService counterpartService;
    private ContractCounterpartMapper counterpartMapper;
    private IContractSigningArchiveService signingArchiveService;
    private static final String CONTRACT_SIGNING_SAVE_STATUS = "60";
    private static final String CONTRACT_CONTRACT_FORM_VALUE = "1";
    private static final String CONTRACT_ARCHIVE_STATUS = "110";

    /**
     * ??????
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "??????", notes = "??????contractSigning")
    @PreAuth("hasPermission('contract:signing:detail')")
    public R<ContractSigningResponseVO> detail(@RequestParam Long id) {
        ContractSigningEntity detail = contractSigningService.getById(id);
        return R.data(ContractSigningWrapper.build().entityPV(detail));
    }

    /**
     * ??????
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "??????", notes = "??????contractSigning")
    @PreAuth("hasPermission('contract:signing:page')")
    public R<IPage<ContractSigningResponseVO>> list(ContractSigningRequestVO contractSigning, Query query) {
        IPage<ContractSigningEntity> pages = contractSigningService.pageList(Condition.getPage(query), contractSigning);
        return R.data(ContractSigningWrapper.build().entityPVPage(pages));
    }

    /**
     * ??????
     */
    @PostMapping("/add")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "??????", notes = "??????contractSigning")
    @PreAuth("hasPermission('contract:signing:add')")
    @Transactional(rollbackFor = Exception.class)
    public R<ContractSigningEntity> save(@Valid @RequestBody ContractSigningRequestVO contractSigning) {
        ContractSigningEntity entity = new ContractSigningEntity();
        BeanUtil.copy(contractSigning,entity);
        contractSigningService.save(entity);
        //String contractForm = contractFormInfoService.getById(contractSigning.getContractId()).getContractForm();
        //???????????????????????????????????????????????????????????????????????????
        /*if (CONTRACT_CONTRACT_FORM_VALUE.equals(contractForm)) {
            String contractStatus = CONTRACT_ARCHIVE_STATUS;
            contractFormInfoService.updateExportStatus(contractStatus, contractSigning.getContractId());
        } else {
            String contractStatus = CONTRACT_SIGNING_SAVE_STATUS;
            contractFormInfoService.updateExportStatus(contractStatus, contractSigning.getContractId());
        }*/
		String contractStatus = CONTRACT_SIGNING_SAVE_STATUS;
		contractFormInfoService.updateExportStatus(contractStatus, contractSigning.getContractId());
        List<ContractSigningArchiveEntity> signingArchiveList= JSONObject.parseArray(contractSigning.getSigningArchiveJson(),ContractSigningArchiveEntity.class);
        signingArchiveList.forEach(signingArchive -> {
            if (Func.isNotEmpty(signingArchive.getId())){
                signingArchive.setId(null);
            }
            signingArchive.setSigningId(contractSigning.getContractId());
            signingArchiveService.save(signingArchive);
        });
        return R.data(entity);
    }

    /**
     * ????????????????????????
     */
    @PostMapping("/adds")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "??????", notes = "signingArchive")
    @PreAuth("hasPermission('contract:signing:adds')")
    public R save(@Valid @RequestBody ContractSigningArchiveRequestVO signingArchive) {
        return R.status(signingArchiveService.save(ContractSigningArchiveWrapper.build().QVEntity(signingArchive)));
    }
    /**
     * ??????
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "??????", notes = "??????contractSigning")
    @PreAuth("hasPermission('contract:signing:update')")
    public R update(@Valid @RequestBody ContractSigningRequestVO contractSigning) {
        if (Func.isEmpty(contractSigning.getId())) {
            throw new ServiceException("id????????????");
        }
        return R.status(contractSigningService.updateById(ContractSigningWrapper.build().QVEntity(contractSigning)));
    }


    /**
     * ??????
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "????????????", notes = "??????ids")
    @PreAuth("hasPermission('contract:signing:remove')")
    public R remove(@ApiParam(value = "????????????", required = true) @RequestParam String ids) {
        return R.status(contractSigningService.deleteLogic(Func.toLongList(ids)));
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    @GetMapping("/logInfo")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "??????????????????????????????")
    @PreAuth("hasPermission('contract:signing:logInfo')")
    public R<ContractSigningResponseVO> logInfo() {
        ContractSigningResponseVO responseVO = ContractSigningWrapper.build().createPV();
        BladeUser user = AuthUtil.getUser();
        Long userId = Long.valueOf(user.getUserId());
        Long deptId = Long.valueOf(AuthUtil.getDeptId());
        Date now = new Date();
        responseVO.setCreateUserName(UserCache.getUser(userId).getRealName());
        responseVO.setCreateDeptName(SysCache.getDeptName(deptId));
        return R.data(responseVO);
    }

    /**
     *
     * ??????????????????excel
     *
     * @param formInfoEntity
     * @param response
     */
    @PostMapping("/exportTargetDataResult")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "??????", notes = "")
    public void exportTargetDataResult(@RequestBody ContractFormInfoResponseVO formInfoEntity, HttpServletResponse response) {

        if (Func.isNotEmpty(formInfoEntity)) {
            /* ?????????????????? */
            String fileName = "????????????????????????";
            WriteSheet sheet1 = new WriteSheet();
            /* ?????????sheet????????? */
            sheet1.setSheetName("????????????????????????");
            sheet1.setSheetNo(0);
            /* ????????????????????? */
            List<Object> data = new ArrayList<>();
            /* formInfoEntityList ???????????????????????? ??????????????????????????? ????????????????????????????????????????????????????????????????????? */
            /* formInfoEntityList ???????????????????????? ??????????????????????????? ????????????????????????????????????????????????????????????????????? */
            /* ?????? cloumns ???????????????cloumns????????????????????????????????? ???????????????????????? ??????list????????????????????????list??? ????????????????????????excel????????????*/
            List<Object> cloumns = new ArrayList<Object>();
            /*????????????*/
            cloumns.add(formInfoEntity.getContractNumber());
            /*????????????*/
            cloumns.add(formInfoEntity.getContractName());
            /*????????????????????????*/
            cloumns.add(bizClient.getValues("HTDL", Long.valueOf(formInfoEntity.getContractBigCategory())).getData());
            /*????????????????????????*/
            cloumns.add(bizClient.getValues("HTDL", Long.valueOf(formInfoEntity.getContractSmallCategory())).getData());
            /*?????????????????????*/
            StringBuilder name = new StringBuilder();
            for (ContractCounterpartEntity counterpartEntity : formInfoEntity.getCounterpart()) {
                name.append(counterpartEntity.getName());
                name.append(",");
            }
            name.substring(0, name.length());
            cloumns.add(name.toString());
            /* ???????????? */
            cloumns.add(formInfoEntity.getContractAmount());
            /*??????*/
            cloumns.add(bizClient.getValue("bz", formInfoEntity.getCurrencyCategory()).getData());
            /*????????????*/
            cloumns.add(formInfoEntity.getSubmitStatus());
            /*????????????*/
            cloumns.add(formInfoEntity.getCreateTime());
            /*??????????????????*/
            cloumns.add(formInfoEntity.getFileExportCount()+1);
            data.add(cloumns);
            /* ???????????????excel????????? ??????list??????????????????????????????????????? */
            List<List<String>> headList = new ArrayList<List<String>>();
            /* ??????????????????????????????????????????????????????????????????????????????  ???????????????list */
            List<String> head = Arrays.asList("????????????", "????????????", "??????????????????", "??????????????????", "???????????????", "????????????", "??????", "????????????",
                    "????????????", "??????????????????");
            /* ???????????????????????????list?????????????????????????????? */
            List<String> head2 = null;
            for (String head1 : head) {
                head2 = new ArrayList<>();
                /* ??????????????????????????????list?????? */
                head2.add(head1);
                /* ????????????????????????????????????????????? */
                headList.add(head2);
            }
            try {
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding(Charsets.UTF_8.name());
                fileName = URLEncoder.encode(fileName, Charsets.UTF_8.name());
                response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
                EasyExcel.write(response.getOutputStream()).head(headList).sheet().doWrite(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * ??????????????????excel
     *
     * @param formInfoEntity
     * @param response
     */
    @PostMapping("/exportTargetDataResultSigning")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "??????", notes = "")
    public void exportTargetDataResultSigning(@RequestBody ContractFormInfoResponseVO formInfoEntity, HttpServletResponse response) {

        if (Func.isNotEmpty(formInfoEntity)) {
            /* ?????????????????? */
            String fileName = "????????????????????????";
            WriteSheet sheet1 = new WriteSheet();
            /* ?????????sheet????????? */
            sheet1.setSheetName("????????????????????????");
            sheet1.setSheetNo(0);
            /* ????????????????????? */
            List<Object> data = new ArrayList<>();
            /* formInfoEntityList ???????????????????????? ??????????????????????????? ????????????????????????????????????????????????????????????????????? */
            /* ?????? cloumns ???????????????cloumns????????????????????????????????? ???????????????????????? ??????list????????????????????????list??? ????????????????????????excel????????????*/
            List<Object> cloumns = new ArrayList<Object>();
            /*????????????*/
            cloumns.add(formInfoEntity.getContractNumber());
            /*????????????*/
            cloumns.add(formInfoEntity.getContractName());
            /*????????????????????????*/
            cloumns.add(bizClient.getValues("HTDL", Long.valueOf(formInfoEntity.getContractBigCategory())).getData());
            /*????????????????????????*/
            cloumns.add(bizClient.getValues("HTDL", Long.valueOf(formInfoEntity.getContractSmallCategory())).getData());
            /*?????????????????????*/
            StringBuilder name = new StringBuilder();
            for (ContractCounterpartEntity counterpartEntity : formInfoEntity.getCounterpart()) {
                name.append(counterpartEntity.getName());
                name.append(",");
            }
            name.substring(0, name.length());
            cloumns.add(name.toString());
            ContractSigningEntity signingEntity=contractSigningService.selectSigningById(formInfoEntity.getId());
            /*?????????*/
            cloumns.add(signingEntity.getManager());
            ContractSealUsingInfoEntity sealUsingInfoEntity=sealUsingInfoService.selectSealUsing(formInfoEntity.getId());
            /*????????????*/
            cloumns.add(sealUsingInfoEntity.getSignTime());
            /*????????????*/
            cloumns.add("????????????:"+bizClient.getValue("submission_type",signingEntity.getSubmissionType()).getData()+"//????????????:"
                    +signingEntity.getCourierNum()+"//????????????:"+signingEntity.getCourierCompany()
                    +"//?????????:"+signingEntity.getAddressee()+"//???????????????:"+signingEntity.getAddress());
            data.add(cloumns);
            /* ???????????????excel????????? ??????list??????????????????????????????????????? */
            List<List<String>> headList = new ArrayList<List<String>>();
            /* ??????????????????????????????????????????????????????????????????????????????  ???????????????list */
            List<String> head = Arrays.asList("????????????", "????????????", "??????????????????", "??????????????????", "???????????????", "?????????", "????????????", "????????????");
            /* ???????????????????????????list?????????????????????????????? */
            List<String> head2 = null;
            for (String head1 : head) {
                head2 = new ArrayList<>();
                /* ??????????????????????????????list?????? */
                head2.add(head1);
                /* ????????????????????????????????????????????? */
                headList.add(head2);
            }
            try {
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding(Charsets.UTF_8.name());
                fileName = URLEncoder.encode(fileName, Charsets.UTF_8.name());
                response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
                EasyExcel.write(response.getOutputStream()).head(headList).sheet().doWrite(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
