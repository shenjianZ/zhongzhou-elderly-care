package com.zzyl.nursing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Chihiro
 * &#064;date 2025-03-14 11:56
 * @version v1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryDevicePropertyDto implements Serializable {
    private String deviceName;
    private String productKey;
}
