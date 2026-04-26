package com.zzyl.nursing.vo;

import com.zzyl.nursing.domain.DevicePropertyList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Chihiro
 * &#064;date 2025-03-14 12:03
 * @version v1.0
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class DevicePropertyVo implements Serializable {
    private DevicePropertyList list;
}
