package com.zzyl.nursing.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.zzyl.common.core.domain.R;
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
import com.zzyl.nursing.domain.Contract;
import com.zzyl.nursing.service.IContractService;
import com.zzyl.common.utils.poi.ExcelUtil;
import com.zzyl.common.core.page.TableDataInfo;

/**
 * 合同表Controller
 *
 * @Author: Zhy
 * @Date: 2025-02-25
 */
@Api(tags = "合同表管理")
@RestController
@RequestMapping("/nursing/contract")
@RequiredArgsConstructor
public class ContractController extends BaseController {

	private final IContractService contractService;

	/**
	 * 查询合同表列表
	 */
	@ApiOperation("查询合同表列表")
	@PreAuthorize("@ss.hasPermi('nursing:contract:list')")
	@GetMapping("/list")
	public TableDataInfo<List<Contract>> list(@ApiParam("合同表查询条件") Contract contract) {
		startPage();
		List<Contract> list = contractService.selectContractList(contract);
		return getDataTable(list);
	}

	/**
	 * 导出合同表列表
	 */
	@ApiOperation("导出合同表列表")
	@PreAuthorize("@ss.hasPermi('nursing:contract:export')")
	@Log(title = "合同表", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
	public void export(@ApiParam(value = "合同表查询条件") HttpServletResponse response, Contract contract) {
		List<Contract> list = contractService.selectContractList(contract);
		ExcelUtil<Contract> util = new ExcelUtil<>(Contract.class);
		util.exportExcel(response, list, "合同表数据");
	}

	/**
	 * 获取合同表详细信息
	 */
	@ApiOperation("获取合同表详细信息")
	@PreAuthorize("@ss.hasPermi('nursing:contract:query')")
	@GetMapping(value = "/{id}")
	public R<Contract> getInfo(@ApiParam("合同表ID") @PathVariable("id") Integer id) {
		return R.ok(contractService.selectContractById(id));
	}

	/**
	 * 新增合同表
	 */
	@ApiOperation("新增合同表")
	@PreAuthorize("@ss.hasPermi('nursing:contract:add')")
	@Log(title = "合同表", businessType = BusinessType.INSERT)
	@PostMapping
	public AjaxResult add(@ApiParam("合同表信息") @RequestBody Contract contract) {
		return toAjax(contractService.insertContract(contract));
	}

	/**
	 * 修改合同表
	 */
	@ApiOperation("修改合同表")
	@PreAuthorize("@ss.hasPermi('nursing:contract:edit')")
	@Log(title = "合同表", businessType = BusinessType.UPDATE)
	@PutMapping
	public AjaxResult edit(@ApiParam("合同表信息") @RequestBody Contract contract) {
		return toAjax(contractService.updateContract(contract));
	}

	/**
	 * 删除合同表
	 */
	@ApiOperation("删除合同表")
	@PreAuthorize("@ss.hasPermi('nursing:contract:remove')")
	@Log(title = "合同表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	public AjaxResult remove(@ApiParam("合同表ID数组") @PathVariable Integer[] ids) {
		return toAjax(contractService.deleteContractByIds(ids));
	}
}
