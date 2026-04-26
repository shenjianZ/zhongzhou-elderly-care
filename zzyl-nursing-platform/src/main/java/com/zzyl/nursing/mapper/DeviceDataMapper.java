package com.zzyl.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.zzyl.nursing.domain.DeviceData;
import org.apache.ibatis.annotations.Param;

/**
 * 设备数据Mapper接口
 * 
 * @author Zhy
 * @date 2025-03-08
 */
@Mapper
public interface DeviceDataMapper extends BaseMapper<DeviceData>
{
    /**
     * 查询设备数据
     * 
     * @param id 设备数据主键
     * @return 设备数据
     */
    public DeviceData selectDeviceDataById(Long id);

    /**
     * 查询设备数据列表
     * 
     * @param deviceData 设备数据
     * @return 设备数据集合
     */
    public List<DeviceData> selectDeviceDataList(DeviceData deviceData);

    /**
     * 新增设备数据
     * 
     * @param deviceData 设备数据
     * @return 结果
     */
    public int insertDeviceData(DeviceData deviceData);

    /**
     * 修改设备数据
     * 
     * @param deviceData 设备数据
     * @return 结果
     */
    public int updateDeviceData(DeviceData deviceData);

    /**
     * 删除设备数据
     * 
     * @param id 设备数据主键
     * @return 结果
     */
    public int deleteDeviceDataById(Long id);

    /**
     * 批量删除设备数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeviceDataByIds(Long[] ids);

    @MapKey("data_value")
    List<Map<String, Object>> queryDeviceDataListByDay(@Param("functionId") String functionId, @Param("iotId") String iotId, @Param("startTime") LocalDateTime startTimeDate, @Param("endTime") LocalDateTime endTimeDate);

    @MapKey("data_value")
    List<Map<String, Object>> queryDeviceDataListByWeek(@Param("functionId") String functionId, @Param("iotId") String iotId, @Param("startTime") LocalDateTime startTimeDate, @Param("endTime") LocalDateTime endTimeDate);
}
