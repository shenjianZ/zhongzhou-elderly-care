package com.zzyl.nursing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Chihiro
 * &#064;date 2025-03-14 12:00
 * @version v1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DevicePropertyList implements Serializable {
    private List<PropertyStatusInfo> propertyStatusInfo;
}
