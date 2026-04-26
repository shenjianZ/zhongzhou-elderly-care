package com.zzyl.nursing.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.dto.DeviceDataPageReqDto;
import com.zzyl.nursing.mapper.DeviceMapper;
import com.zzyl.nursing.vo.IotMsgNotifyData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.DeviceDataMapper;
import com.zzyl.nursing.domain.DeviceData;
import com.zzyl.nursing.service.IDeviceDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * 设备数据Service业务层处理
 *
 * @author Zhy
 * @date 2025-03-08
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceDataServiceImpl extends ServiceImpl<DeviceDataMapper, DeviceData> implements IDeviceDataService {

	private final DeviceDataMapper deviceDataMapper;
	private final DeviceMapper deviceMapper;
	private final RedisTemplate redisTemplate;

	/**
	 * 查询设备数据
	 *
	 * @param id 设备数据主键
	 * @return 设备数据
	 */
	@Override
	public DeviceData selectDeviceDataById(Long id) {
		return deviceDataMapper.selectById(id);
	}

	/**
	 * 查询设备数据列表
	 *
	 * @param deviceData 设备数据
	 * @return 设备数据
	 */
	@Override
	public List<DeviceData> selectDeviceDataList(DeviceData deviceData) {
		return deviceDataMapper.selectDeviceDataList(deviceData);
	}

	/**
	 * 新增设备数据
	 *
	 * @param deviceData 设备数据
	 * @return 结果
	 */
	@Override
	public int insertDeviceData(DeviceData deviceData) {
		return deviceDataMapper.insert(deviceData);
	}

	/**
	 * 修改设备数据
	 *
	 * @param deviceData 设备数据
	 * @return 结果
	 */
	@Override
	public int updateDeviceData(DeviceData deviceData) {
		return deviceDataMapper.updateById(deviceData);
	}

	/**
	 * 批量删除设备数据
	 *
	 * @param ids 需要删除的设备数据主键
	 * @return 结果
	 */
	@Override
	public int deleteDeviceDataByIds(Long[] ids) {
		return deviceDataMapper.deleteBatchIds(Arrays.asList(ids));
	}

	/**
	 * 删除设备数据信息
	 *
	 * @param id 设备数据主键
	 * @return 结果
	 */
	@Override
	public int deleteDeviceDataById(Long id) {
		return deviceDataMapper.deleteById(id);
	}

	/**
	 * 批量保存数据
	 *
	 * @param iotMsgNotifyData 设备数据集合
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchInsertDeviceData(IotMsgNotifyData iotMsgNotifyData) {
		// 1. 根据设备 ID 查询本地设备是否存在
		String deviceId = iotMsgNotifyData.getHeader().getDeviceId();
		Device device = deviceMapper.selectOne(new LambdaQueryWrapper<Device>().eq(Device::getIotId, deviceId));
		if (ObjUtil.isEmpty(device)) {
			throw new BaseException("设备不存在");
		}

		// 2. 从设备实体中获取数据，赋值到设备数据实体中

		// 3. 把 IOT 获取到的设备赋值到设备数据实体中
		// 存放设备数据列表
		List<DeviceData> deviceDataList = new ArrayList<>();

		iotMsgNotifyData.getBody().getServices().forEach(item -> {
			LocalDateTime activeTime = LocalDateTimeUtil.parse(item.getEventTime(), "yyyyMMdd'T'HHmmss'Z'");
			// 日期时区转换
			LocalDateTime alarmTime = activeTime.atZone(ZoneId.from(ZoneOffset.UTC)).withZoneSameInstant(ZoneId.of(
				"Asia/Shanghai")).toLocalDateTime();

			// 遍历该设备中的每个属性
			item.getProperties().forEach((key, value) -> {
				DeviceData deviceData = DeviceData.builder()
					.iotId(deviceId)
					.deviceName(device.getDeviceName())
					.productKey(device.getProductKey())
					.productName(device.getProductName())
					.functionId(key)
					.accessLocation(device.getRemark())
					.locationType(device.getLocationType())
					.physicalLocationType(device.getPhysicalLocationType())
					.deviceDescription(device.getDeviceDescription())
					.dataValue(value.toString())
					.alarmTime(alarmTime)
					.build();

				deviceDataList.add(deviceData);
			});
		});

		// 4. 批量保存设备数据
		if (!saveBatch(deviceDataList)) {
			throw new BaseException("批量保存设备数据失败");
		}

		// 5. 将最新数据保存在 Redis 中
		try {
			redisTemplate.opsForHash().put(CacheConstants.IOT_DEVICE_LAST_DATA, deviceId, JSONUtil.toJsonStr(deviceDataList));
		} catch (Exception e) {
			log.warn("保存设备最新数据到 Redis 失败");
		}
	}

	/**
	 * 分页查询设备数据列表
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public TableDataInfo<DeviceData> selectDeviceDataPageList(DeviceDataPageReqDto deviceDataPageReqDto) {

		Page<DeviceData> page = new Page<>(deviceDataPageReqDto.getPageNum(), deviceDataPageReqDto.getPageSize());

		this.lambdaQuery().eq(
			StrUtil.isNotBlank(deviceDataPageReqDto.getDeviceName()),
			DeviceData::getDeviceName,
			deviceDataPageReqDto.getDeviceName()
		).eq(
			StrUtil.isNotBlank(deviceDataPageReqDto.getFunctionId()),
			DeviceData::getFunctionId,
			deviceDataPageReqDto.getFunctionId()
		).between((ObjUtil.isNotEmpty(deviceDataPageReqDto.getStartTime()) &&
			ObjUtil.isNotEmpty(deviceDataPageReqDto.getEndTime())
		), DeviceData::getAlarmTime, deviceDataPageReqDto.getStartTime(), deviceDataPageReqDto.getEndTime()).page(page);

		TableDataInfo<DeviceData> tableDataInfo = new TableDataInfo<>();
		tableDataInfo.setTotal(page.getTotal());
		tableDataInfo.setRows(page.getRecords());
		tableDataInfo.setCode(200);
		tableDataInfo.setMsg("获取设备数据成功");

		return tableDataInfo;
	}

}
