package com.zzyl.nursing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 处理设备报警数据DTO
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-12 18:02
 */
@Data
@ApiModel(value = "处理设备报警数据DTO")
public class HandleAlertDataDto {

	@ApiModelProperty(value = "主键ID")
	private Long id;

	@ApiModelProperty(value = "处理结果")
	private String processingResult;

	@ApiModelProperty(value = "处理时间")
	private LocalDateTime processingTime;

}
