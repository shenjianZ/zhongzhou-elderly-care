package com.zzyl.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.zzyl.nursing.domain.Device;

/**
 * 设备Mapper接口
 * 
 * @author Zhy
 * @date 2025-03-06
 */
@Mapper
public interface DeviceMapper extends BaseMapper<Device>
{
    /**
     * 查询设备
     * 
     * @param id 设备主键
     * @return 设备
     */
    Device selectDeviceById(Long id);

    /**
     * 查询设备列表
     * 
     * @param device 设备
     * @return 设备集合
     */
    List<Device> selectDeviceList(Device device);

    /**
     * 新增设备
     *
     * @param device 设备
     * @return 结果
     */
    int insertDevice(Device device);

    /**
     * 修改设备
     *
     * @param device 设备
     * @return 结果
     */
    int updateDevice(Device device);

    /**
     * 删除设备
     *
     * @param id 设备主键
     * @return 结果
     */
    int deleteDeviceById(Long id);

    /**
     * 批量删除设备
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteDeviceByIds(Long[] ids);

    /**
     * 通过设备 id 查询护理人
     * 
     * @return: 
     * @param: 
     */
    List<Long> selectNursingIdsByIotIdWithElder(String iotId);

    /**
     * 通过设备 id 查询护理人
     * 
     * @return: 
     * @param: 
     */
    List<Long> selectNursingIdsByIotIdWithBed(String iotId);

}
