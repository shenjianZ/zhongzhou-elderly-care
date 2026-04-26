package com.zzyl.nursing.service;

import java.util.List;
import com.zzyl.nursing.domain.MemberReservation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 预约信息Service接口
 * 
 * @author Zhy
 * @date 2025-03-05
 */
public interface IMemberReservationService extends IService<MemberReservation>
{
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
     * @param memberReservation 预约信息
     */
    void insertReservation(MemberReservation memberReservation);

    /**
     * 修改预约信息
     * 
     * @param reservation 预约信息
     * @return 结果
     */
    public int updateReservation(MemberReservation reservation);

    /**
     * 批量删除预约信息
     * 
     * @param ids 需要删除的预约信息主键集合
     * @return 结果
     */
    public int deleteReservationByIds(Long[] ids);

    /**
     * 删除预约信息信息
     * 
     * @param id 预约信息主键
     * @return 结果
     */
    public int deleteReservationById(Long id);

    /**
     * 查询当天取消预约数量
     * 
     * @return: 
     * @param: 
     */
    Integer selectReservationCount();

    /**
     * 取消预约
     * 
     * @return: 
     * @param: 
     */
    void updateReservationById(Long id);
    
    /**
     * 定时更新预约状态
     * 
     * @return: 
     * @param: 
     */
    void updateReservationStatus();
}
