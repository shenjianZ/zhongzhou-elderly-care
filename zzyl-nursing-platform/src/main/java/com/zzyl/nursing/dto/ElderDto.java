package com.zzyl.nursing.dto;

import com.zzyl.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Chihiro
 * &#064;date 2025-03-14 09:46
 * @version v1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElderDto extends BaseEntity implements Serializable {
    private Long id;
    private String idCard;
    private String name;
    private String remark;
    private Long familyMemberId;
    private Long elderId;
}
