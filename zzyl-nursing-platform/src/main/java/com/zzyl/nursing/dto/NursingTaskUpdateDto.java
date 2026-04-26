package com.zzyl.nursing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author shd
 * @version V1.0
 * @date 2025-03-14 15:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NursingTaskUpdateDto {
    private String estimatedServerTime;
    private Long taskId;
}
