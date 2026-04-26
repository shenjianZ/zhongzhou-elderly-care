package com.zzyl.nursing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询老人信息
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-06 10:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "分页查询老人信息")
public class ElderPageQuery {

	/** 状态 0-禁用 1-启用 */
	@ApiModelProperty(value = "状态 0-禁用 1-启用")
	private Integer status;

	/** 页码 */
	@ApiModelProperty(value = "页码")
	private Integer pageNum;

	/** 每页条数 */
	@ApiModelProperty(value = "每页条数")
	private Integer pageSize;

	/** 姓名 */
	@ApiModelProperty(value = "姓名")
	private String name;

	/** 身份证号 */
	@ApiModelProperty(value = "身份证号")
	private String idCardNo;

}
