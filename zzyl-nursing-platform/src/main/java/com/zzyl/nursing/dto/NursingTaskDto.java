package com.zzyl.nursing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zzyl.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author shd
 * @version V1.0
 * @date 2025-03-14 14:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("护理任务Dto")
public class NursingTaskDto {
    private static final long serialVersionUID = 1L;

    // 护理员id
    @Excel(name = "护理员id")
    @ApiModelProperty("护理员id")
    private String nurseId;

    // 项目id
    @Excel(name = "项目id")
    @ApiModelProperty("项目id")
    private Integer projectId;

    // 老人姓名
    @Excel(name = "老人姓名")
    @ApiModelProperty("老人姓名")
    private String elderName;

    // 状态  1待执行 2已执行 3已关闭
    @Excel(name = "状态  1待执行 2已执行 3已关闭 ")
    @ApiModelProperty("状态  1待执行 2已执行 3已关闭 ")
    private Integer status;

    /** 页码 */
    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    /** 每页条数 */
    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;
}
