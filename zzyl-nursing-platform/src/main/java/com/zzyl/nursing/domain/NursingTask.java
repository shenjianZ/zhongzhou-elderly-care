package com.zzyl.nursing.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;

/**
 * 护理任务对象 nursing_task
 *
 * @author zzyl
 * @date 2025-03-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("护理任务实体")
public class NursingTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    // id
    @ApiModelProperty("id")
    private Long id;

    // 护理员id
    @Excel(name = "护理员id")
    @ApiModelProperty("护理员id")
    private String nursingId;

    // 项目id
    @Excel(name = "项目id")
    @ApiModelProperty("项目id")
    private Integer projectId;

    // 护理项目名称
    @Excel(name = "护理项目名称")
    @ApiModelProperty("护理项目名称")
    private String projectName;

    // 老人id
    @Excel(name = "老人id")
    @ApiModelProperty("老人id")
    private Long elderId;

    // 老人姓名
    @Excel(name = "老人姓名")
    @ApiModelProperty("老人姓名")
    private String elderName;

    // 床位编号
    @Excel(name = "床位编号")
    @ApiModelProperty("床位编号")
    private String bedNumber;

    // 预计服务时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预计服务时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("预计服务时间")
    private LocalDateTime estimatedServerTime;

    // 实际服务时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "实际服务时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("实际服务时间")
    private LocalDateTime realServerTime;

    // 执行记录
    @Excel(name = "执行记录")
    @ApiModelProperty("执行记录")
    private String mark;

    // 取消原因
    @Excel(name = "取消原因")
    @ApiModelProperty("取消原因")
    private String cancelReason;

    // 状态  1待执行 2已执行 3已关闭
    @Excel(name = "状态  1待执行 2已执行 3已关闭 ")
    @ApiModelProperty("状态  1待执行 2已执行 3已关闭 ")
    private Integer status;

    // 执行图片
    @Excel(name = "执行图片")
    @ApiModelProperty("执行图片")
    private String taskImage;

    // 任务类型 1护理计划外 2护理计划内
    @Excel(name = "任务类型 1护理计划外 2护理计划内")
    @ApiModelProperty("任务类型 1护理计划外 2护理计划内")
    private String taskType;

}
