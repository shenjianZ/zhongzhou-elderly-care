package com.zzyl.nursing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Chihiro
 * &#064;date 2025-03-14 12:06
 * @version v1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyStatusInfo implements Serializable {
    private String dataType;
    private String identifier;
    private String name;
    private Long time;
    private String unit;
    private double value;
}
