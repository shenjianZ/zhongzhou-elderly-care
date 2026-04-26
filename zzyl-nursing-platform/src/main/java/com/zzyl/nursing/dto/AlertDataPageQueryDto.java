package com.zzyl.nursing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 分页查询警告数据dto
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-12 17:35
 */
@Data
@ApiModel(value = "分页查询警告数据dto")
public class AlertDataPageQueryDto {

	@ApiModelProperty(value = "页码")
	private Integer pageNum;

	@ApiModelProperty(value = "每页条数")
	private Integer pageSize;

	@ApiModelProperty(value = "设备名称")
	private String deviceName;

	@ApiModelProperty(value = "开始报警时间")
	private Date startTime;

	@ApiModelProperty(value = "结束报警时间")
	private Date endTime;

	@ApiModelProperty(value = "状态")
	private Integer status;

}
