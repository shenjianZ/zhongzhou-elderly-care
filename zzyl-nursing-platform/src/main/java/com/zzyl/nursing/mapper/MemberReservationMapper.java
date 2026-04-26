package com.zzyl.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

import com.zzyl.nursing.domain.MemberReservation;
import org.apache.ibatis.annotations.Param;

/**
 * 预约信息Mapper接口
 *
 * @author Zhy
 * @date 2025-03-05
 */
@Mapper
public interface MemberReservationMapper extends BaseMapper<MemberReservation> {
	/**
	 * 查询预约信息
	 *
	 * @param id 预约信息主键
	 * @return 预约信息
	 */
	public MemberReservation selectReservationById(Long id);

	/**
	 * 查询预约信息列表
	 *
	 * @param reservation 预约信息
	 * @return 预约信息集合
	 */
	public List<MemberReservation> selectReservationList(MemberReservation reservation);

	/**
	 * 新增预约信息
	 *
	 * @param reservation 预约信息
	 * @return 结果
	 */
	public int insertReservation(MemberReservation reservation);

	/**
	 * 修改预约信息
	 *
	 * @param reservation 预约信息
	 * @return 结果
	 */
	public int updateReservation(MemberReservation reservation);

	/**
	 * 删除预约信息
	 *
	 * @param id 预约信息主键
	 * @return 结果
	 */
	public int deleteReservationById(Long id);

	/**
	 * 批量删除预约信息
	 *
	 * @param ids 需要删除的数据主键集合
	 * @return 结果
	 */
	public int deleteReservationByIds(Long[] ids);

	/**
	 * 查询当天取消预约数量
	 *
	 * @return:
	 * @param:
	 */
	List<MemberReservation> selectReservationCount(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

	/**
	 * 取消预约
	 *
	 * @return:
	 * @param:
	 */
	void updateStatus(Long id, int i);
}
