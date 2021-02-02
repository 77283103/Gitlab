package org.springblade.contract.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springblade.core.mp.base.BaseEntity;


/**
 * 合同签订关联表 实体类
 *
 * @author 合同签订关联表
 * @date : 2020-11-05 09:34:31
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "合同签订关联表请求对象")
public class ContractSigningArchiveRequestVO extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 关联签订ID
	 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value="关联签订ID")
	private Long signingId;
	/**
	 * 页数
	 */
    @ApiModelProperty(value="页数")
	private Integer pages;
	/**
	 * 原件/复印件
	 */
    @ApiModelProperty(value="原件/复印件")
	private String attachedFileInt;
	/**
	 * 名称
	 */
    @ApiModelProperty(value="名称")
	private String name;

}
