package com.zzyl.nursing.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 设备上报数据VO
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-06 16:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "设备上报数据VO")
public class DeviceReportDataVo {

	/** 属性名 */
	@ApiModelProperty(value = "属性名")
	private String functionId;

	/** 上报时间 */
	@ApiModelProperty(value = "上报时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime eventTime;

	/** 上报数据 */
	@ApiModelProperty(value = "上报数据")
	private String value;

}
