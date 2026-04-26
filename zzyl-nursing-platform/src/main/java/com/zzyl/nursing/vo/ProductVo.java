package com.zzyl.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品信息
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-06 10:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "产品信息")
public class ProductVo {

	/** 产品ID */
	@ApiModelProperty(value = "产品ID")
	private String productId;

	/** 产品名称 */
	@ApiModelProperty(value = "产品名称")
	private String name;

}
