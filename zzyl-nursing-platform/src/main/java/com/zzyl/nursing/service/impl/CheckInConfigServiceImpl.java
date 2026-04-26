package com.zzyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.CheckInConfigMapper;
import com.zzyl.nursing.domain.CheckInConfig;
import com.zzyl.nursing.service.ICheckInConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 入住配置表Service业务层处理
 * 
 * @Author: Zhy
 * @Date: 2025-02-25
 */
@Service
public class CheckInConfigServiceImpl extends ServiceImpl<CheckInConfigMapper, CheckInConfig> implements ICheckInConfigService
{
    @Autowired
    private CheckInConfigMapper checkInConfigMapper;

    /**
     * 查询入住配置表
     * 
     * @param id 入住配置表主键
     * @return 入住配置表
     */
    @Override
    public CheckInConfig selectCheckInConfigById(Long id)
    {
        return checkInConfigMapper.selectById(id);
    }

    /**
     * 查询入住配置表列表
     * 
     * @param checkInConfig 入住配置表
     * @return 入住配置表
     */
    @Override
    public List<CheckInConfig> selectCheckInConfigList(CheckInConfig checkInConfig)
    {
        return checkInConfigMapper.selectCheckInConfigList(checkInConfig);
    }

    /**
     * 新增入住配置表
     * 
     * @param checkInConfig 入住配置表
     * @return 结果
     */
    @Override
    public int insertCheckInConfig(CheckInConfig checkInConfig)
    {
        return checkInConfigMapper.insert(checkInConfig);
    }

    /**
     * 修改入住配置表
     * 
     * @param checkInConfig 入住配置表
     * @return 结果
     */
    @Override
    public int updateCheckInConfig(CheckInConfig checkInConfig)
    {
        return checkInConfigMapper.updateById(checkInConfig);
    }

    /**
     * 批量删除入住配置表
     * 
     * @param ids 需要删除的入住配置表主键
     * @return 结果
     */
    @Override
    public int deleteCheckInConfigByIds(Long[] ids)
    {
        return checkInConfigMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除入住配置表信息
     * 
     * @param id 入住配置表主键
     * @return 结果
     */
    @Override
    public int deleteCheckInConfigById(Long id)
    {
        return checkInConfigMapper.deleteById(id);
    }
}
