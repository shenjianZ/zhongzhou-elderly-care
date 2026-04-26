package com.zzyl.nursing.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.zzyl.common.core.domain.R;
import com.zzyl.nursing.dto.CheckInApplyDto;
import com.zzyl.nursing.vo.CheckInDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zzyl.common.annotation.Log;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.enums.BusinessType;
import com.zzyl.nursing.domain.CheckIn;
import com.zzyl.nursing.service.ICheckInService;
import com.zzyl.common.utils.poi.ExcelUtil;
import com.zzyl.common.core.page.TableDataInfo;

/**
 * 入住表Controller
 *
 * @Author: Zhy
 * @Date: 2025-02-25
 */
@Api(tags = "入住表管理")
@RestController
@RequestMapping("/nursing/checkIn")
@RequiredArgsConstructor
public class CheckInController extends BaseController {

	private final ICheckInService checkInService;

	/**
	 * 查询入住表列表
	 */
	@ApiOperation("查询入住表列表")
	@PreAuthorize("@ss.hasPermi('nursing:checkIn:list')")
	@GetMapping("/list")
	public TableDataInfo<List<CheckIn>> list(@ApiParam("入住表查询条件") CheckIn checkIn) {
		startPage();
		List<CheckIn> list = checkInService.selectCheckInList(checkIn);
		return getDataTable(list);
	}

	/**
	 * 导出入住表列表
	 */
	@ApiOperation("导出入住表列表")
	@PreAuthorize("@ss.hasPermi('nursing:checkIn:export')")
	@Log(title = "入住表", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
	public void export(@ApiParam(value = "入住表查询条件") HttpServletResponse response, CheckIn checkIn) {
		List<CheckIn> list = checkInService.selectCheckInList(checkIn);
		ExcelUtil<CheckIn> util = new ExcelUtil<>(CheckIn.class);
		util.exportExcel(response, list, "入住表数据");
	}

	/**
	 * 获取入住表详细信息
	 */
	@ApiOperation("获取入住表详细信息")
	@PreAuthorize("@ss.hasPermi('nursing:checkIn:query')")
	@GetMapping(value = "/{id}")
	public R<CheckIn> getInfo(@ApiParam("入住表ID") @PathVariable("id") Long id) {
		return R.ok(checkInService.selectCheckInById(id));
	}

	/**
	 * 新增入住表
	 */
	@ApiOperation("新增入住表")
	@PreAuthorize("@ss.hasPermi('nursing:checkIn:add')")
	@Log(title = "入住表", businessType = BusinessType.INSERT)
	@PostMapping
	public AjaxResult add(@ApiParam("入住表信息") @RequestBody CheckIn checkIn) {
		return toAjax(checkInService.insertCheckIn(checkIn));
	}

	/**
	 * 修改入住表
	 */
	@ApiOperation("修改入住表")
	@PreAuthorize("@ss.hasPermi('nursing:checkIn:edit')")
	@Log(title = "入住表", businessType = BusinessType.UPDATE)
	@PutMapping
	public AjaxResult edit(@ApiParam("入住表信息") @RequestBody CheckIn checkIn) {
		return toAjax(checkInService.updateCheckIn(checkIn));
	}

	/**
	 * 删除入住表
	 */
	@ApiOperation("删除入住表")
	@PreAuthorize("@ss.hasPermi('nursing:checkIn:remove')")
	@Log(title = "入住表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	public AjaxResult remove(@ApiParam("入住表ID数组") @PathVariable Long[] ids) {
		return toAjax(checkInService.deleteCheckInByIds(ids));
	}

	/**
	 * 申请入住
	 */
	@ApiOperation("申请入住")
	@Log(title = "入住表", businessType = BusinessType.INSERT)
	@PostMapping("/apply")
	public AjaxResult apply(@ApiParam("申请入住请求DTO") @RequestBody CheckInApplyDto checkInApplyDto) {
		checkInService.apply(checkInApplyDto);
		return AjaxResult.success("操作成功");
	}

	/**
	 * 获取老人入住详细信息
	 */
	@ApiOperation("获取老人入住详细信息")
	@GetMapping(value = "/detail/{id}")
	public R<CheckInDetailVo> getCheckInInfo(@ApiParam("入住记录ID") @PathVariable("id") Long id) {
		return R.ok(checkInService.getCheckInInfo(id));
	}

}
