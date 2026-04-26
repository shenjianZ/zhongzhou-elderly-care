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
import com.zzyl.nursing.domain.CheckInConfig;
import com.zzyl.nursing.service.ICheckInConfigService;
import com.zzyl.common.utils.poi.ExcelUtil;
import com.zzyl.common.core.page.TableDataInfo;

/**
 * 入住配置表Controller
 *
 * @Author: Zhy
 * @Date: 2025-02-25
 */
@Api(tags = "入住配置表管理")
@RestController
@RequestMapping("/nursing/checkInConfig")
@RequiredArgsConstructor
public class CheckInConfigController extends BaseController
{

    private final ICheckInConfigService checkInConfigService;

    /**
     * 查询入住配置表列表
     */
    @ApiOperation("查询入住配置表列表")
    @PreAuthorize("@ss.hasPermi('nursing:checkInConfig:list')")
    @GetMapping("/list")
    public TableDataInfo<List<CheckInConfig>> list(@ApiParam("入住配置表查询条件") CheckInConfig checkInConfig)
    {
        startPage();
        List<CheckInConfig> list = checkInConfigService.selectCheckInConfigList(checkInConfig);
        return getDataTable(list);
    }

    /**
     * 导出入住配置表列表
     */
    @ApiOperation("导出入住配置表列表")
    @PreAuthorize("@ss.hasPermi('nursing:checkInConfig:export')")
    @Log(title = "入住配置表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@ApiParam(value = "入住配置表查询条件") HttpServletResponse response, CheckInConfig checkInConfig)
    {
        List<CheckInConfig> list = checkInConfigService.selectCheckInConfigList(checkInConfig);
        ExcelUtil<CheckInConfig> util = new ExcelUtil<>(CheckInConfig.class);
        util.exportExcel(response, list, "入住配置表数据");
    }

    /**
     * 获取入住配置表详细信息
     */
    @ApiOperation("获取入住配置表详细信息")
    @PreAuthorize("@ss.hasPermi('nursing:checkInConfig:query')")
    @GetMapping(value = "/{id}")
    public R<CheckInConfig> getInfo(@ApiParam("入住配置表ID") @PathVariable("id") Long id)
    {
        return R.ok(checkInConfigService.selectCheckInConfigById(id));
    }

    /**
     * 新增入住配置表
     */
    @ApiOperation("新增入住配置表")
    @PreAuthorize("@ss.hasPermi('nursing:checkInConfig:add')")
    @Log(title = "入住配置表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@ApiParam("入住配置表信息") @RequestBody CheckInConfig checkInConfig)
    {
        return toAjax(checkInConfigService.insertCheckInConfig(checkInConfig));
    }

    /**
     * 修改入住配置表
     */
    @ApiOperation("修改入住配置表")
    @PreAuthorize("@ss.hasPermi('nursing:checkInConfig:edit')")
    @Log(title = "入住配置表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@ApiParam("入住配置表信息") @RequestBody CheckInConfig checkInConfig)
    {
        return toAjax(checkInConfigService.updateCheckInConfig(checkInConfig));
    }

    /**
     * 删除入住配置表
     */
    @ApiOperation("删除入住配置表")
    @PreAuthorize("@ss.hasPermi('nursing:checkInConfig:remove')")
    @Log(title = "入住配置表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@ApiParam("入住配置表ID数组") @PathVariable Long[] ids)
    {
        return toAjax(checkInConfigService.deleteCheckInConfigByIds(ids));
    }
}
