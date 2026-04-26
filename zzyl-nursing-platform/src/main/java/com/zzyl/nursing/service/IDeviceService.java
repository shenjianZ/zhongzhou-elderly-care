package com.zzyl.nursing.service;

import java.util.List;

import com.huaweicloud.sdk.iotda.v5.model.ServiceCapability;
import com.zzyl.nursing.domain.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.vo.DeviceDetailVo;
import com.zzyl.nursing.vo.DeviceReportDataVo;
import com.zzyl.nursing.vo.ProductVo;

/**
 * 设备Service接口
 *
 * @author Zhy
 * @date 2025-03-06
 */
public interface IDeviceService extends IService<Device> {

	/**
	 * 查询设备列表
	 *
	 * @param device 设备
	 * @return 设备集合
	 */
	List<Device> selectDeviceList(Device device);

	/**
	 * 从物联网平台同步产品列表
	 *
	 * @return:
	 * @param:
	 */
	void syncProductList();

	/**
	 * 查询所有产品列表
	 *
	 * @return:
	 * @param:
	 */
	List<ProductVo> allProduct();

	/**
	 * 注册设备
	 *
	 * @return:
	 * @param:
	 */
	void registerProduct(DeviceDto deviceDto);

	/**
	 * 查询设备详细数据
	 *
	 * @return:
	 * @param:
	 */
	DeviceDetailVo queryDeviceDetail(String iotId);

	/**
	 * 查询设备上报数据
	 *
	 * @return:
	 * @param:
	 */
	List<DeviceReportDataVo> queryDeviceReportData(String iotId);

	/**
	 * 更新设备数据
	 * 
	 * @return: 
	 * @param: 
	 */
	void updateDevice(Device device);

	/**
	 * 删除设备
	 *
	 * @return:
	 * @param:
	 */
	void deleteDeviceByIotId(String iotId);

	/**
	 * 获取产品对应的服务信息
	 *
	 * @return:
	 * @param:
	 */
	List<ServiceCapability> queryProductByProductKey(String productKey);
}
