package com.zzyl.nursing.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.zzyl.common.core.domain.R;
import com.zzyl.nursing.dto.HealthAssessmentDto;
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
import com.zzyl.nursing.domain.HealthAssessment;
import com.zzyl.nursing.service.IHealthAssessmentService;
import com.zzyl.common.utils.poi.ExcelUtil;
import com.zzyl.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 健康评估Controller
 *
 * @Author: Zhy
 * @Date: 2025-02-28
 */
@Api(tags = "健康评估管理")
@RestController
@RequestMapping("/nursing/healthAssessment")
@RequiredArgsConstructor
public class HealthAssessmentController extends BaseController {

	private final IHealthAssessmentService healthAssessmentService;

	/**
	 * 查询健康评估列表
	 */
	@ApiOperation("查询健康评估列表")
	@PreAuthorize("@ss.hasPermi('nursing:healthAssessment:list')")
	@GetMapping("/list")
	public TableDataInfo<List<HealthAssessment>> list(@ApiParam("健康评估查询条件") HealthAssessment healthAssessment) {
		startPage();
		List<HealthAssessment> list = healthAssessmentService.selectHealthAssessmentList(healthAssessment);
		return getDataTable(list);
	}

	/**
	 * 导出健康评估列表
	 */
	@ApiOperation("导出健康评估列表")
	@PreAuthorize("@ss.hasPermi('nursing:healthAssessment:export')")
	@Log(title = "健康评估", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
	public void export(
		@ApiParam(value = "健康评估查询条件") HttpServletResponse response, HealthAssessment healthAssessment
	) {
		List<HealthAssessment> list = healthAssessmentService.selectHealthAssessmentList(healthAssessment);
		ExcelUtil<HealthAssessment> util = new ExcelUtil<HealthAssessment>(HealthAssessment.class);
		util.exportExcel(response, list, "健康评估数据");
	}

	/**
	 * 获取健康评估详细信息
	 */
	@ApiOperation("获取健康评估详细信息")
	@PreAuthorize("@ss.hasPermi('nursing:healthAssessment:query')")
	@GetMapping(value = "/{id}")
	public R<HealthAssessment> getInfo(@ApiParam("健康评估ID") @PathVariable("id") Long id) {
		return R.ok(healthAssessmentService.selectHealthAssessmentById(id));
	}

	/**
	 * 新增健康评估
	 */
	@ApiOperation("新增健康评估")
	@PreAuthorize("@ss.hasPermi('nursing:healthAssessment:add')")
	@Log(title = "健康评估", businessType = BusinessType.INSERT)
	@PostMapping
	public AjaxResult add(@ApiParam("健康评估信息") @RequestBody HealthAssessmentDto healthAssessmentDto) {
		Long id = healthAssessmentService.insertHealthAssessment(healthAssessmentDto);
		return success(id);
	}

	/**
	 * 修改健康评估
	 */
	@ApiOperation("修改健康评估")
	@PreAuthorize("@ss.hasPermi('nursing:healthAssessment:edit')")
	@Log(title = "健康评估", businessType = BusinessType.UPDATE)
	@PutMapping
	public AjaxResult edit(@ApiParam("健康评估信息") @RequestBody HealthAssessment healthAssessment) {
		return toAjax(healthAssessmentService.updateHealthAssessment(healthAssessment));
	}

	/**
	 * 删除健康评估
	 */
	@ApiOperation("删除健康评估")
	@PreAuthorize("@ss.hasPermi('nursing:healthAssessment:remove')")
	@Log(title = "健康评估", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	public AjaxResult remove(@ApiParam("健康评估ID数组") @PathVariable Long[] ids) {
		return toAjax(healthAssessmentService.deleteHealthAssessmentByIds(ids));
	}

	/**
	 * 上传体检报告
	 */
	@ApiOperation("上传体检报告")
	@PostMapping("/upload")
	public AjaxResult uploadFile(String idCardNo, MultipartFile file) {
		return healthAssessmentService.uploadFile(idCardNo, file);
	}

}
