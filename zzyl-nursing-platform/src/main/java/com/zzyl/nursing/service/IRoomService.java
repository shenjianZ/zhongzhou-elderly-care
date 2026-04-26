package com.zzyl.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzyl.nursing.domain.Room;
import com.zzyl.nursing.vo.RoomVo;

import java.util.List;

/**
 * 房间Service接口
 *
 * @author ruoyi
 * @date 2024-04-26
 */
public interface IRoomService extends IService<Room> {
	/**
	 * 查询房间
	 *
	 * @param id 房间ID
	 * @return 房间
	 */
	public Room selectRoomById(Long id);

	/**
	 * 查询房间列表
	 *
	 * @param room 房间
	 * @return 房间集合
	 */
	public List<Room> selectRoomList(Room room);

	/**
	 * 新增房间
	 *
	 * @param room 房间
	 * @return 结果
	 */
	public int insertRoom(Room room);

	/**
	 * 修改房间
	 *
	 * @param room 房间
	 * @return 结果
	 */
	public int updateRoom(Room room);

	/**
	 * 批量删除房间
	 *
	 * @param ids 需要删除的房间ID集合
	 * @return 结果
	 */
	public int deleteRoomByIds(Long[] ids);

	/**
	 * 根据楼层 id 获取房间 视图对象列表
	 *
	 * @param floorId 楼层id
	 * @return
	 */
	List<RoomVo> getRoomsByFloorId(Long floorId);

	/**
	 * 获取所有房间（负责老人）
	 *
	 * @param floorId
	 * @return
	 */
	List<RoomVo> getRoomsWithNurByFloorId(Long floorId);

	/**
	 * 根据房间id查询房间 数据
	 *
	 * @return:
	 * @param:
	 */
	RoomVo getRoomPriceByRoomId(Long id);

	/**
	 * 获取房间中的智能设备及数据
	 *
	 * @return:
	 * @param:
	 */
	List<RoomVo> getRoomsWithDeviceByFloorId(Long floorId);
}
