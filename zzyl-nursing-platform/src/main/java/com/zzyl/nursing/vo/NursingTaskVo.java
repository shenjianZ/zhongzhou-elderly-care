package com.zzyl.nursing.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;
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
 * @date 2025-03-14 14:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("护理任务vo")
public class NursingTaskVo extends BaseEntity {
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

    // 状态  1待执行 2已执行 3已关闭
    @Excel(name = "状态  1待执行 2已执行 3已关闭 ")
    @ApiModelProperty("状态  1待执行 2已执行 3已关闭 ")
    private Integer status;

    // 护理项目名称
    @Excel(name = "护理员姓名")
    @ApiModelProperty("护理员姓名")
    private List<String> nursingName;

    private String nursingLevelName;

    private Integer age;

    private String updater;

    private String mark;

    private String taskImage;

    private LocalDateTime realServerTime;

    private String cancelReason;
}
