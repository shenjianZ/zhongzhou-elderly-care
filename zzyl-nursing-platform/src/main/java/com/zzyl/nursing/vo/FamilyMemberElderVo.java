package com.zzyl.nursing.vo;

import com.zzyl.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Chihiro
 * &#064;date 2025-03-14 10:39
 * @version v1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyMemberElderVo  extends BaseEntity implements Serializable{
    private Long mid;
    private String mremark;
    private Long elderId;
    private String name;
    private String image;
    private String bedNumber;
    private String typeName;
    private String iotId;
    private String deviceName;
    private String productKey;
}
