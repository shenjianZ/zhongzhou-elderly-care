package com.zzyl.nursing.controller.member;

import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.R;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.nursing.domain.NursingProject;
import com.zzyl.nursing.service.INursingProjectService;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 服务项目控制层
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-05 15:48
 */
@RestController
@RequestMapping("/member/orders/project")
@Slf4j
@Api(tags = "服务项目控制层")
@RequiredArgsConstructor
public class MemberOrderProjectController extends BaseController {

	private final INursingProjectService nursingProjectService;

	@ApiOperation("分页查询护理项目列表")
	@GetMapping("/page")
	public TableDataInfo<List<NursingProject>> selectPageProject(@ApiParam("护理项目查询条件") NursingProject nursingProject) {
		// 开始分页
		startPage();

		List<NursingProject> list = nursingProjectService.selectNursingProjectList(nursingProject);
		return getDataTable(list);
	}

	@ApiOperation("根据编号查询护理项目信息")
	@GetMapping("/{id}")
	public R<NursingProject> selectPageProjectById(@ApiParam("护理项目id") @PathVariable("id") Long id) {
		return R.ok(nursingProjectService.selectNursingProjectById(id));
	}

}
