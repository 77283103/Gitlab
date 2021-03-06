package org.springblade.contract.vo;

import lombok.ToString;
import lombok.Getter;
import lombok.Setter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;

import org.springblade.contract.entity.ContractCounterpartEntity;
import org.springblade.contract.entity.ContractFormInfoEntity;
import org.springblade.core.tool.utils.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import org.springblade.core.mp.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;

import javax.validation.constraints.NotNull;


/**
 * 接收/提供服务计划清单 请求模型VO
 *
 * @NotNull 验证对象是否不为null, 无法查检长度为0的字符串
 * @NotBlank 检查约束 (字符串) 是不是Null还有被Trim的长度是否大于0,只对字符串,且会去掉前后空格.
 * @NotEmpty 检查(集合)约束元素是否为NULL或者是EMPTY.
 *
 * @author szw
 * @date : 2020-11-05 17:06:55
 */
@Getter
@Setter
@ToString
@ApiModel(description = "接收/提供服务计划清单请求对象")
public class ContractPerformanceRequestVO extends BaseEntity{

	private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "服务内容")
	private String name;
    @ApiModelProperty(value = "交易类别")
	private String type;
	@NotNull(message = "计划缴纳时间不能为空")
	@DateTimeFormat(pattern = DateUtil.PATTERN_DATE)
	@JsonFormat(pattern = DateUtil.PATTERN_DATE)
	@ApiModelProperty(value = "计划缴纳时间", required = true)
	private Date planPayTime;
	@DateTimeFormat(pattern = DateUtil.PATTERN_DATE)
	@JsonFormat(pattern = DateUtil.PATTERN_DATE)
	@ApiModelProperty(value = "实际缴纳时间")
	private Date actualPayTime;
    @ApiModelProperty(value = "服务接受履约金额缴纳期限")
	private String  contractPerformance;
	@ApiModelProperty(value = "关联合同信息")
	private ContractFormInfoEntity contractFormInfoEntity;

	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "合同id")
	private Long contractId;
	@ApiModelProperty(value = "合同相对方")
	private List<ContractCounterpartEntity> counterpartEntityList;

}
