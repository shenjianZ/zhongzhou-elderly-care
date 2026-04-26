package com.zzyl.nursing.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shd
 * @version V1.0
 * @date 2025-03-14 15:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("护理任务取消原因")
public class NursingTaskCancelDto {
    private String reason;
    private Long taskId;
}
