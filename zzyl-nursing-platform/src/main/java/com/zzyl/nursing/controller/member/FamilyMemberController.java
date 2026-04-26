package com.zzyl.nursing.controller.member;

import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.core.domain.R;
import com.zzyl.nursing.dto.ElderDto;
import com.zzyl.nursing.dto.QueryDevicePropertyDto;
import com.zzyl.nursing.dto.UserLoginRequestDto;
import com.zzyl.nursing.vo.DevicePropertyVo;
import com.zzyl.nursing.vo.FamilyMemberElderVo;
import com.zzyl.nursing.vo.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.nursing.service.IFamilyMemberService;

import java.util.List;
import java.util.Map;

/**
 * 老人家属Controller
 *
 * @author Zhy
 * @date 2025-03-03
 */
@Api(tags = "老人家属管理")
@RestController
@RequestMapping("/member/user")
@RequiredArgsConstructor
public class FamilyMemberController extends BaseController {

	private final IFamilyMemberService familyMemberService;

	/**
	 * 小程序登录
	 *
	 * @return:
	 * @param: userLoginRequestDto 用户登录请求参数
	 */
	@PostMapping("/login")
	@ApiOperation("小程序登录")
	public R<LoginVo> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
		LoginVo loginVo = familyMemberService.login(userLoginRequestDto);
		return R.ok(loginVo);
	}

	@PostMapping("/add")
	@ApiOperation("绑定老人家属")
	public AjaxResult addMember(@RequestBody ElderDto elderDto) {
		familyMemberService.addMember(elderDto);
		return AjaxResult.success();
	}

	@GetMapping("/my")
	@ApiOperation("查询当前登录用户的老人家属信息")
	public AjaxResult getMyElder() {
		List<ElderDto> elderDtoList = familyMemberService.getMyElder();
		return AjaxResult.success(elderDtoList);
	}

	@GetMapping("/list-by-page")
	@ApiOperation("分页查询当前登录用户的老人家属信息")
	private AjaxResult listByPage(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
		List<FamilyMemberElderVo> familyMemberElderVoList = familyMemberService.listByPage(pageNum, pageSize);
		return AjaxResult.success(familyMemberElderVoList);
	}

	@PostMapping("/QueryDevicePropertyStatus")
	@ApiOperation("查询设备属性状态")
	private AjaxResult queryDevicePropertyStatus(@RequestBody QueryDevicePropertyDto queryDevicePropertyDto) {
		DevicePropertyVo devicePropertyVo = familyMemberService.queryDevicePropertyStatus(queryDevicePropertyDto);
		return AjaxResult.success(devicePropertyVo);
	}

	@GetMapping("/queryDeviceDataListByDay")
	@ApiOperation("按天统计查询指标数据")
	private AjaxResult queryDeviceDataListByDay(String functionId, String iotId, Long startTime, Long endTime) {
		List<Map<String,Object>> familyMemberElderVoList = familyMemberService.queryDeviceDataListByDay(functionId, iotId, startTime, endTime);
		return AjaxResult.success(familyMemberElderVoList);
	}

	@GetMapping("/queryDeviceDataListByWeek")
	@ApiOperation("按周统计查询指标数据")
	private AjaxResult queryDeviceDataListByWeek(String functionId, String iotId, Long startTime, Long endTime) {
		List<Map<String,Object>> familyMemberElderVoList = familyMemberService.queryDeviceDataListByWeek(functionId, iotId, startTime, endTime);
		return AjaxResult.success(familyMemberElderVoList);
	}

}
