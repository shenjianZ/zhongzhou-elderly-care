package com.zzyl.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzyl.nursing.domain.FamilyMember;
import com.zzyl.nursing.dto.ElderDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 老人家属Mapper接口
 * 
 * @author Zhy
 * &#064;date  2025-03-03
 */
@Mapper
public interface FamilyMemberElderMapper {

    void insert(ElderDto elderDto);

    List<ElderDto> getMyElder(Long id);

    List<ElderDto> getMyElderPage(@Param("userId") Long userId, @Param("startNum") Integer startNum, @Param("pageSize") Integer pageSize);
}
