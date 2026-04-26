package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.huaweicloud.sdk.iotda.v5.model.*;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.vo.DeviceDetailVo;
import com.zzyl.nursing.vo.DeviceReportDataVo;
import com.zzyl.nursing.vo.ProductVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.DeviceMapper;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.service.IDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 设备Service业务层处理
 *
 * @author Zhy
 * @date 2025-03-06
 */
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService {

	private final DeviceMapper deviceMapper;
	private final IoTDAClient client;
	private final RedisTemplate<String, String> redisTemplate;

	/**
	 * 查询设备列表
	 *
	 * @param device 设备
	 * @return 设备
	 */
	@Override
	public List<Device> selectDeviceList(Device device) {
		return deviceMapper.selectDeviceList(device);
	}

	/**
	 * 从物联网平台同步产品列表
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public void syncProductList() {
		ListProductsRequest listProductsRequest = new ListProductsRequest();
		listProductsRequest.setLimit(50);
		ListProductsResponse response = client.listProducts(listProductsRequest);

		if (ObjUtil.isNotEmpty(response.getProducts())) {
			// 存入 Redis 缓存
			redisTemplate.opsForValue().set(CacheConstants.IOT_ALL_PRODUCT, JSONUtil.toJsonStr(response.getProducts()));
		}
	}

	/**
	 * 查询所有产品列表
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public List<ProductVo> allProduct() {
		String jsonStr = redisTemplate.opsForValue().get(CacheConstants.IOT_ALL_PRODUCT);
		if (ObjUtil.isEmpty(jsonStr)) {
			// 返回一个空列表
			return Collections.emptyList();
		}
		return JSONUtil.toList(jsonStr, ProductVo.class);
	}

	/**
	 * 注册设备
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public void registerProduct(DeviceDto deviceDto) {
		// 1. 检查设备名字是否重复
		LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
		long count = count(queryWrapper.eq(Device::getDeviceName, deviceDto.getDeviceName()));
		if (count > 0) {
			throw new BaseException("设备名字重复");
		}

		// 2. 检查设备标识是否重复
		queryWrapper = new LambdaQueryWrapper<>();
		count = count(queryWrapper.eq(Device::getNodeId, deviceDto.getNodeId()));
		if (count > 0) {
			throw new BaseException("设备标识重复");
		}

		// 3. 检查同一个位置是否绑定的相同的产品
		queryWrapper = new LambdaQueryWrapper<>();
		count = count(queryWrapper.eq(Device::getBindingLocation, deviceDto.getBindingLocation())
			.eq(Device::getProductKey, deviceDto.getProductKey())
			.eq(Device::getLocationType, deviceDto.getLocationType()));
		if (count > 0) {
			throw new BaseException("同一个位置不能绑定同类型的产品");
		}

		// 补全实体
		String secret = UUID.randomUUID().toString().replace("-", "");
		String iotId = UUID.randomUUID().toString().replace("-", "");

		// 4. 注册设备到 IOT 平台
		// 封装发送的请求
		AddDeviceRequest request = new AddDeviceRequest();
		// 封装请求体
		AddDevice body = new AddDevice();
		body.withProductId(deviceDto.getProductKey());
		body.withDeviceName(deviceDto.getDeviceName());
		body.withNodeId(deviceDto.getNodeId());
		body.withDeviceId(iotId);
		// 将封装的请求体信息放入请求中
		request.withBody(body);
		// 创建认证信息
		AuthInfo authInfo = new AuthInfo();
		// 设置秘钥
		authInfo.withSecret(secret);
		// 将认证信息放入请求中
		body.setAuthInfo(authInfo);
		AddDeviceResponse response;
		try {
			response = client.addDevice(request);
		} catch (Exception e) {
			throw new BaseException("IOT平台注册设备失败，" + e.getMessage());
		}

		// 5. 保存设备信息到数据库
		Device device = BeanUtil.toBean(deviceDto, Device.class);
		device.setSecret(secret);
		device.setIotId(iotId);
		try {
			save(device);
		} catch (Exception e) {
			// 删除 IOT 平台注册的设备
			// TODO

			throw new BaseException("新增设备信息失败，请联系管理员");
		}
	}

	/**
	 * 查询设备详细数据
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public DeviceDetailVo queryDeviceDetail(String iotId) {
		// 1. 查询数据库中是否有数据
		LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
		Device device = getOne(queryWrapper.eq(Device::getIotId, iotId));
		if (ObjUtil.isEmpty(device)) {
			return null;
		}

		// 2. 查询 IOT
		ShowDeviceRequest request = new ShowDeviceRequest();
		request.withDeviceId(iotId);
		ShowDeviceResponse response;
		try {
			response = client.showDevice(request);
			if (!Objects.equals(response.getHttpStatusCode(), 200)) {
				throw new BaseException("查询设备详细数据失败");
			}
		} catch (Exception e) {
			throw new BaseException("查询设备详细数据失败，" + e.getMessage());
		}

		// 3. 整合属性
		DeviceDetailVo deviceDetailVo = BeanUtil.toBean(device, DeviceDetailVo.class);
		deviceDetailVo.setDeviceStatus(response.getStatus());
		String activeTimeStr = response.getActiveTime();
		if (ObjUtil.isNotEmpty(activeTimeStr)) {
			LocalDateTime activeTime = LocalDateTimeUtil.parse(activeTimeStr, DatePattern.UTC_MS_PATTERN);
			// 日期时区转换
			activeTime = activeTime.atZone(ZoneId.from(ZoneOffset.UTC))
				.withZoneSameInstant(ZoneId.of("Asia/Shanghai"))
				.toLocalDateTime();
			deviceDetailVo.setActiveTime(activeTime);
		}

		return deviceDetailVo;
	}

	/**
	 * 查询设备上报数据
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public List<DeviceReportDataVo> queryDeviceReportData(String iotId) {
		// 1. 查询 IOT 中的数据
		ShowDeviceShadowRequest request = new ShowDeviceShadowRequest();
		request.withDeviceId(iotId);
		ShowDeviceShadowResponse response;
		try {
			response = client.showDeviceShadow(request);
			if (!ObjUtil.equals(response.getHttpStatusCode(), 200)) {
				throw new BaseException("从 IOT 平台查询设备上报数据失败");
			}
		} catch (Exception e) {
			throw new BaseException("从 IOT 平台查询设备上报数据失败");
		}

		// 2. 解析数据
		List<DeviceShadowData> shadowList = response.getShadow();
		if (ObjUtil.isEmpty(shadowList)) {
			return new ArrayList<>();
		}
		DeviceShadowData shadowData = shadowList.get(0);
		if (ObjUtil.isEmpty(shadowData)) {
			return new ArrayList<>();
		}
		// 3. 处理时间
		String eventTime = shadowData.getReported().getEventTime();
		LocalDateTime activeTime = LocalDateTimeUtil.parse(eventTime, "yyyyMMdd'T'HHmmss'Z'");
		// 日期时区转换
		activeTime = activeTime.atZone(ZoneId.from(ZoneOffset.UTC))
			.withZoneSameInstant(ZoneId.of("Asia/Shanghai"))
			.toLocalDateTime();

		// 4. 封装结果
		List<DeviceReportDataVo> deviceVo = new ArrayList<>();
		Object properties = shadowData.getReported().getProperties();
		if (properties instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) properties;
			LocalDateTime finalActiveTime = activeTime;
			map.forEach((k, v) -> {
				deviceVo.add(new DeviceReportDataVo(k, finalActiveTime, v.toString()));
			});
		}

		return deviceVo;
	}

	/**
	 * 更新设备数据
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public void updateDevice(Device device) {
		// 1. 更新 IOT
		UpdateDeviceRequest request = new UpdateDeviceRequest();
		request.withDeviceId(device.getIotId());
		UpdateDevice body = new UpdateDevice();
		body.withDeviceName(device.getDeviceName());
		// 将封装的请求体信息放入请求中
		request.withBody(body);

		UpdateDeviceResponse response;
		try {
			response = client.updateDevice(request);
			if (!ObjUtil.equals(response.getHttpStatusCode(), 200)) {
				throw new BaseException("IOT 更新设备信息失败");
			}
		} catch (Exception e) {
			throw new BaseException("IOT 更新设备信息失败");
		}

		// 2. 更新数据库
		try {
			updateById(device);
		} catch (Exception e) {
			// TODO
			// 回滚 IOT 更新

			throw new BaseException("数据库更新设备信息失败");
		}
	}

	/**
	 * 删除设备
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public void deleteDeviceByIotId(String iotId) {
		// 1. 删除 IOT 上的设备
		DeleteDeviceRequest request = new DeleteDeviceRequest();
		request.withDeviceId(iotId);
		try {
			client.deleteDevice(request);
		} catch (Exception e) {
			throw new BaseException("IOT 删除设备失败");
		}

		// 2. 删除数据库中的设备信息
		try {
			LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(Device::getIotId, iotId);
			remove(queryWrapper);
		} catch (Exception e) {
			throw new BaseException("数据库删除设备信息失败");
		}
	}

	/**
	 * 获取产品对应的服务信息
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public List<ServiceCapability> queryProductByProductKey(String productKey) {
		ShowProductRequest request = new ShowProductRequest();
		request.withProductId(productKey);
		try {
			ShowProductResponse response = client.showProduct(request);
			List<ServiceCapability> serviceList = response.getServiceCapabilities();
			if(ObjUtil.isEmpty(serviceList)){
				return List.of();
			}
			return serviceList;
		} catch (Exception e) {
			throw new BaseException("查询产品服务信息失败");
		}
	}

}
