package com.zzyl.nursing.controller.member;

import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.R;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.nursing.domain.MemberReservation;
import com.zzyl.nursing.service.IMemberReservationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 参观预约控制层
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-05 14:26
 */
@RestController
@RequestMapping("/member/reservation")
@Api(tags = "预约信息相关接口")
@Slf4j
@RequiredArgsConstructor
public class MemberReservationController extends BaseController {

	private final IMemberReservationService memberReservationService;

	/**
	 * 查询当天取消预约数量
	 *
	 * @return:
	 * @param:
	 */
	@GetMapping("/cancelled-count")
	@ApiOperation("查询取消预约数量")
	public R<Integer> getCancelledReservationCount() {
		log.info("查询取消预约数量");
		Integer count = memberReservationService.selectReservationCount();
		return R.ok(count);
	}

	/**
	 * 查询每个时间段剩余预约次数
	 *
	 * @return:
	 * @param:
	 */
	// @GetMapping("/countByTime")
	// @ApiOperation("查询每个时间段剩余预约次数")
	// public R<Integer> selectReservationCountByTime(@ApiParam("预约时间") String time) {
	// 	Integer count = memberReservationService.selectReservationCountByTime();
	// 	return R.ok(count);
	// }

	/**
	 * 新增预约
	 *
	 * @return:
	 * @param:
	 */
	@PostMapping
	@ApiOperation("新增预约")
	public R<MemberReservation> insertReservation(
		@ApiParam("预约信息") @RequestBody MemberReservation memberReservation
	) {
		log.info("新增预约，memberReservation={}", memberReservation);
		memberReservationService.insertReservation(memberReservation);
		return R.ok();
	}

	/**
	 * 分页查询预约
	 *
	 * @return:
	 * @param:
	 */
	@GetMapping("/page")
	@ApiOperation("分页查询预约")
	public R<Object> selectPageReservation(
		@ApiParam("预约信息") MemberReservation memberReservation
	) {
		startPage();
		List<MemberReservation> list = memberReservationService.selectReservationList(memberReservation);
		return R.ok(getDataTable(list));
	}

	/**
	 * 取消预约
	 *
	 * @return:
	 * @param:
	 */
	@PutMapping("/{id}/cancel")
	@ApiOperation("取消预约")
	public R<Object> cancelReservation(@ApiParam("预约id") @PathVariable("id") Long id) {
		log.info("取消预约，id={}", id);
		memberReservationService.updateReservationById(id);
		return R.ok();
	}

}
