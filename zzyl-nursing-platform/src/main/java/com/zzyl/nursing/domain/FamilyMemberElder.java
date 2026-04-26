package com.zzyl.nursing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Chihiro
 * &#064;date 2025-03-14 09:44
 * @version v1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyMemberElder implements Serializable {
    private Long id;
    private Long familyMemberId;
    private Long elderId;
    private String elderName;
}
