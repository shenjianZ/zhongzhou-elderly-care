package com.zzyl.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzyl.nursing.vo.CheckInConfigVo;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.zzyl.nursing.domain.CheckIn;

/**
 * 入住表Mapper接口
 * 
 * @author Zhy
 * @date 2025-02-25
 */
@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn>
{
    /**
     * 查询入住表
     * 
     * @param id 入住表主键
     * @return 入住表
     */
    public CheckIn selectCheckInById(Long id);

    /**
     * 查询入住表列表
     * 
     * @param checkIn 入住表
     * @return 入住表集合
     */
    public List<CheckIn> selectCheckInList(CheckIn checkIn);

    /**
     * 新增入住表
     * 
     * @param checkIn 入住表
     * @return 结果
     */
    public int insertCheckIn(CheckIn checkIn);

    /**
     * 修改入住表
     * 
     * @param checkIn 入住表
     * @return 结果
     */
    public int updateCheckIn(CheckIn checkIn);

    /**
     * 删除入住表
     * 
     * @param id 入住表主键
     * @return 结果
     */
    public int deleteCheckInById(Long id);

    /**
     * 批量删除入住表
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCheckInByIds(Long[] ids);

    /**
     * 查询入住配置信息
     *
     * @return:
     * @param: id 入住ID
     */
    CheckInConfigVo selectCheckInInfoById(Long id);
}
