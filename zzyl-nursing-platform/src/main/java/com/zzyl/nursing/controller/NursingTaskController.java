package com.zzyl.nursing.controller;

import com.zzyl.common.annotation.Log;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.core.domain.R;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.enums.BusinessType;
import com.zzyl.common.utils.poi.ExcelUtil;
import com.zzyl.nursing.domain.NursingTask;
import com.zzyl.nursing.dto.NursingTaskCancelDto;
import com.zzyl.nursing.dto.NursingTaskDoDto;
import com.zzyl.nursing.dto.NursingTaskDto;
import com.zzyl.nursing.dto.NursingTaskUpdateDto;
import com.zzyl.nursing.service.INursingTaskService;
import com.zzyl.nursing.vo.NursingTaskVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 护理任务Controller
 * 
 * @author ruoyi
 * @date 2024-09-27
 */
@RestController
@RequestMapping("/nursing/nursingTask")
@Api(tags = "护理任务的接口")
public class NursingTaskController extends BaseController
{
    @Autowired
    // @Qualifier("nursingTaskServiceImpl")
    private INursingTaskService nursingTaskService;

    /**
     * 查询护理任务列表
     */
    @ApiOperation("查询护理任务列表")
    @GetMapping("/list")
    public TableDataInfo<NursingTaskVo> list(NursingTaskDto nursingTaskDto)
    {
        return nursingTaskService.list(nursingTaskDto);
    }

    /**
     * 导出护理任务列表
     */
    @ApiOperation("导出护理任务列表")
    @Log(title = "护理任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NursingTask nursingTask)
    {
        List<NursingTask> list = nursingTaskService.selectNursingTaskList(nursingTask);
        ExcelUtil<NursingTask> util = new ExcelUtil<NursingTask>(NursingTask.class);
        util.exportExcel(response, list, "护理任务数据");
    }

    /**
     * 获取护理任务详细信息
     */
    @ApiOperation("获取护理任务详细信息")
    @GetMapping(value = "/{id}")
    public R<NursingTaskVo> getInfo(@ApiParam(value = "护理任务ID", required = true)
            @PathVariable("id") Long id)
    {
        return R.ok(nursingTaskService.selectNursingTaskById(id));
    }

    /**
     * 新增护理任务
     */
    @ApiOperation("新增护理任务")
    @Log(title = "护理任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@ApiParam(value = "护理任务实体")
            @RequestBody NursingTask nursingTask)
    {
        return toAjax(nursingTaskService.insertNursingTask(nursingTask));
    }

    /**
     * 修改护理任务
     */
    @ApiOperation("修改护理任务")
    @Log(title = "护理任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@ApiParam(value = "护理任务实体")
            @RequestBody NursingTask nursingTask)
    {
        return toAjax(nursingTaskService.updateNursingTask(nursingTask));
    }

    /**
     * 删除护理任务
     */
    @ApiOperation("删除护理任务")
    @Log(title = "护理任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(nursingTaskService.deleteNursingTaskByIds(ids));
    }

    @ApiOperation("取消任务")
    @PutMapping("/cancel")
    public AjaxResult cancel(@ApiParam("取消原因") @RequestBody NursingTaskCancelDto nursingTaskCancelDto) {
        nursingTaskService.cancel(nursingTaskCancelDto);
        return success();
    }

    @ApiOperation("执行任务")
    @PutMapping("/do")
    public AjaxResult doTask(@ApiParam("执行任务dto") @RequestBody NursingTaskDoDto nursingTaskDoDto) {
        nursingTaskService.doTask(nursingTaskDoDto);
        return success();
    }

    @ApiOperation("任务改期")
    @PutMapping("/updateTime")
    public AjaxResult updateTime(@ApiParam("执行任务dto") @RequestBody NursingTaskUpdateDto nursingTaskUpdateDto) {
        nursingTaskService.updateTime(nursingTaskUpdateDto);
        return success();
    }



}
