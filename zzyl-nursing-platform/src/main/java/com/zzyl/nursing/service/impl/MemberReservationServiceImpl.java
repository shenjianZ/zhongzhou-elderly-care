package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzyl.common.utils.UserThreadLocal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.MemberReservationMapper;
import com.zzyl.nursing.domain.MemberReservation;
import com.zzyl.nursing.service.IMemberReservationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * 预约信息Service业务层处理
 *
 * @author Zhy
 * @date 2025-03-05
 */
@Service
@RequiredArgsConstructor
public class MemberReservationServiceImpl extends ServiceImpl<MemberReservationMapper, MemberReservation> implements IMemberReservationService {

	private final MemberReservationMapper reservationMapper;

	/**
	 * 查询预约信息
	 *
	 * @param id 预约信息主键
	 * @return 预约信息
	 */
	@Override
	public MemberReservation selectReservationById(Long id) {
		return reservationMapper.selectById(id);
	}

	/**
	 * 查询预约信息列表
	 *
	 * @param reservation 预约信息
	 * @return 预约信息
	 */
	@Override
	public List<MemberReservation> selectReservationList(MemberReservation reservation) {
		return reservationMapper.selectReservationList(reservation);
	}

	/**
	 * 新增预约信息
	 *
	 * @param memberReservation 预约信息
	 */
	@Override
	public void insertReservation(MemberReservation memberReservation) {
		memberReservation.setStatus(0);
		save(memberReservation);
	}

	/**
	 * 修改预约信息
	 *
	 * @param reservation 预约信息
	 * @return 结果
	 */
	@Override
	public int updateReservation(MemberReservation reservation) {
		return reservationMapper.updateById(reservation);
	}

	/**
	 * 批量删除预约信息
	 *
	 * @param ids 需要删除的预约信息主键
	 * @return 结果
	 */
	@Override
	public int deleteReservationByIds(Long[] ids) {
		return reservationMapper.deleteBatchIds(Arrays.asList(ids));
	}

	/**
	 * 删除预约信息信息
	 *
	 * @param id 预约信息主键
	 * @return 结果
	 */
	@Override
	public int deleteReservationById(Long id) {
		return reservationMapper.deleteById(id);
	}

	/**
	 * 查询当天取消预约数量
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public Integer selectReservationCount() {
		// 从ThreadLocal中获取当前登录用户ID
		Long userId = UserThreadLocal.getUserId();

		// 获取当天开始、结束时间
		long time = System.currentTimeMillis();
		// 当天
		LocalDateTime ldt = LocalDateTimeUtil.of(time);
		// 当天开始时间
		LocalDateTime startTime = ldt.toLocalDate().atStartOfDay();
		// 当天结束时间
		LocalDateTime endTime = startTime.plusDays(1);

		List<MemberReservation> memberReservationList = reservationMapper.selectReservationCount(userId,
			startTime,
			endTime
		);
		return memberReservationList.size();
	}

	/**
	 * 取消预约
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public void updateReservationById(Long id) {
		MemberReservation memberReservation = new MemberReservation();
		memberReservation.setId(id);
		memberReservation.setStatus(2);
		updateById(memberReservation);
	}

	/**
	 * 定时更新预约状态
	 *
	 * @return:
	 * @param:
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateReservationStatus() {
		LambdaQueryWrapper<MemberReservation> queryWrapper = new LambdaQueryWrapper<>();
		List<MemberReservation> list = list(queryWrapper.eq(MemberReservation::getStatus, 0)
			.le(MemberReservation::getTime, LocalDateTime.now()));

		if(ObjUtil.isEmpty(list)){
			return;
		}

		list.forEach(reservation -> reservation.setStatus(3));

		this.updateBatchById(list);
	}
}
