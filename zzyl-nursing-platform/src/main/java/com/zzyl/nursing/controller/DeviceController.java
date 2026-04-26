package com.zzyl.nursing.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.huaweicloud.sdk.iotda.v5.model.ServiceCapability;
import com.zzyl.common.core.domain.R;
import com.zzyl.nursing.dto.DeviceDto;
import com.zzyl.nursing.vo.DeviceDetailVo;
import com.zzyl.nursing.vo.DeviceReportDataVo;
import com.zzyl.nursing.vo.ProductVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.zzyl.common.annotation.Log;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.enums.BusinessType;
import com.zzyl.nursing.domain.Device;
import com.zzyl.nursing.service.IDeviceService;
import com.zzyl.common.utils.poi.ExcelUtil;
import com.zzyl.common.core.page.TableDataInfo;

/**
 * 设备Controller
 *
 * @author Zhy
 * @date 2025-03-06
 */
@Api(tags = "设备管理")
@RestController
@RequestMapping("/nursing/device")
@RequiredArgsConstructor
public class DeviceController extends BaseController {

	private final IDeviceService deviceService;

	/**
	 * 查询设备列表
	 */
	@ApiOperation("查询设备列表")
	@PreAuthorize("@ss.hasPermi('nursing:device:list')")
	@GetMapping("/list")
	public TableDataInfo<List<Device>> list(@ApiParam("设备查询条件") Device device) {
		startPage();
		List<Device> list = deviceService.selectDeviceList(device);
		return getDataTable(list);
	}

	/**
	 * 导出设备列表
	 */
	@ApiOperation("导出设备列表")
	@PreAuthorize("@ss.hasPermi('nursing:device:export')")
	@Log(title = "设备", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
	public void export(@ApiParam(value = "设备查询条件") HttpServletResponse response, Device device) {
		List<Device> list = deviceService.selectDeviceList(device);
		ExcelUtil<Device> util = new ExcelUtil<Device>(Device.class);
		util.exportExcel(response, list, "设备数据");
	}

	/**
	 * 从物联网平台同步产品列表
	 *
	 * @return:
	 * @param:
	 */
	@ApiOperation("从物联网平台同步产品列表")
	@PostMapping("/syncProductList")
	public AjaxResult syncProductList() {
		deviceService.syncProductList();
		return AjaxResult.success();
	}

	/**
	 * 查询所有产品列表
	 *
	 * @return:
	 * @param:
	 */
	@ApiOperation("查询所有产品列表")
	@GetMapping(value = "/allProduct")
	public R<List<ProductVo>> allProduct() {
		return R.ok(deviceService.allProduct());
	}

	/**
	 * 注册设备
	 *
	 * @return:
	 * @param:
	 */
	@ApiOperation("注册设备")
	@PostMapping("/register")
	public AjaxResult registerProduct(@ApiParam("设备信息") @RequestBody DeviceDto deviceDto) {
		deviceService.registerProduct(deviceDto);
		return AjaxResult.success();
	}

	/**
	 * 查询设备详细数据
	 *
	 * @return:
	 * @param:
	 */
	@ApiOperation("查询设备详细数据")
	@GetMapping("/{iotId}")
	public R<DeviceDetailVo> getProductByIotId(@ApiParam("设备标识码") @PathVariable("iotId") String iotId) {
		return R.ok(deviceService.queryDeviceDetail(iotId));
	}

	/**
	 * 查询设备上报数据
	 *
	 * @return:
	 * @param:
	 */
	@ApiOperation("查询设备详细数据")
	@GetMapping("/queryServiceProperties/{iotId}")
	public R<List<DeviceReportDataVo>> queryDeviceReportData(
		@ApiParam("设备标识码") @PathVariable("iotId") String iotId
	) {
		return R.ok(deviceService.queryDeviceReportData(iotId));
	}

	/**
	 * 修改设备
	 *
	 * @return:
	 * @param:
	 */
	@ApiOperation("修改设备")
	@PutMapping
	public AjaxResult updateDevice(@ApiParam("设备数据") @RequestBody Device device) {
		deviceService.updateDevice(device);
		return success();
	}

	/**
	 * 删除设备
	 *
	 * @return:
	 * @param:
	 */
	@ApiOperation("修改设备")
	@DeleteMapping("/{iotId}")
	public AjaxResult deleteDevice(@ApiParam("设备标识码") @PathVariable("iotId") String iotId) {
		deviceService.deleteDeviceByIotId(iotId);
		return success();
	}

	/**
	 * 获取产品对应的服务信息
	 *
	 * @return:
	 * @param:
	 */
	@ApiOperation("获取产品对应的服务信息")
	@GetMapping("/queryProduct/{productKey}")
	public R<List<ServiceCapability>> queryProductByProductKey(
		@ApiParam("产品key") @PathVariable("productKey") String productKey
	) {
		List<ServiceCapability> list = deviceService.queryProductByProductKey(productKey);
		return R.ok(list);
	}

}
