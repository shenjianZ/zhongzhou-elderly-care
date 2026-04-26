package com.zzyl.nursing.controller.member;

import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.nursing.domain.RoomType;
import com.zzyl.nursing.service.IRoomTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 客户房型管理
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-03 15:15
 */
@RestController
@RequestMapping("/member/roomTypes")
@Api(tags = "客户房型管理")
@RequiredArgsConstructor
public class MemberRoomTypeController extends BaseController {

	private final IRoomTypeService roomTypeService;

	@GetMapping
	@ApiOperation("根据状态查询房型")
	public AjaxResult findRoomTypeListByStatus(Integer status) {

		List<RoomType> roomTypeVoList = roomTypeService.findRoomTypeListByStatus(status);
		return success(roomTypeVoList);
	}

}

