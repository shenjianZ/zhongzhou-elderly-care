package com.zzyl.nursing.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.nursing.domain.DeviceData;
import com.zzyl.nursing.domain.Room;
import com.zzyl.nursing.mapper.RoomMapper;
import com.zzyl.nursing.service.IRoomService;
import com.zzyl.nursing.vo.BedVo;
import com.zzyl.nursing.vo.RoomVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 房间Service业务层处理
 *
 * @author ruoyi
 * @date 2024-04-26
 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements IRoomService {

	private final RoomMapper roomMapper;
	private final RedisTemplate redisTemplate;

	/**
	 * 查询房间
	 *
	 * @param id 房间主键
	 * @return 房间
	 */
	@Override
	public Room selectRoomById(Long id) {
		return getById(id);
	}

	/**
	 * 查询房间列表
	 *
	 * @param room 房间
	 * @return 房间
	 */
	@Override
	public List<Room> selectRoomList(Room room) {
		return roomMapper.selectRoomList(room);
	}

	/**
	 * 新增房间
	 *
	 * @param room 房间
	 * @return 结果
	 */
	@Override
	public int insertRoom(Room room) {
		return save(room) ? 1 : 0;
	}

	/**
	 * 修改房间
	 *
	 * @param room 房间
	 * @return 结果
	 */
	@Override
	public int updateRoom(Room room) {
		return updateById(room) ? 1 : 0;
	}

	/**
	 * 批量删除房间
	 *
	 * @param ids 需要删除的房间主键
	 * @return 结果
	 */
	@Override
	public int deleteRoomByIds(Long[] ids) {
		return removeByIds(Arrays.asList(ids)) ? 1 : 0;
	}

	/**
	 * 根据楼层 id 获取房间视图对象列表
	 *
	 * @param: floorId
	 * @return:
	 */
	@Override
	public List<RoomVo> getRoomsByFloorId(Long floorId) {
		return roomMapper.selectByFloorId(floorId);
	}


	/**
	 * 获取所有房间（负责老人）
	 *
	 * @param floorId 楼层id
	 * @return 房间视图对象列表
	 */
	@Override
	public List<RoomVo> getRoomsWithNurByFloorId(Long floorId) {
		return roomMapper.selectByFloorIdWithNur(floorId);
	}

	/**
	 * 根据房间id查询 房间 数据
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public RoomVo getRoomPriceByRoomId(Long id) {
		return roomMapper.selectRoomPriceByRoomId(id);
	}

	/**
	 * 获取房间中的智能设备及数据
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public List<RoomVo> getRoomsWithDeviceByFloorId(Long floorId) {
		List<RoomVo> roomVos = roomMapper.getRoomsWithDeviceByFloorId(floorId);
		if (ObjUtil.isNotEmpty(roomVos)) {
			roomVos.forEach(roomVo -> {
				List<BedVo> bedVoList = roomVo.getBedVoList();
				bedVoList.forEach(bedVo -> {
					// 处理床位中的设备数据
					bedVo.getDeviceVos().forEach(deviceVo -> {
						String iotId = deviceVo.getIotId();
						// 从 Redis 中获取设备最新数据
						String jsonStr = (String) redisTemplate.opsForHash().get(CacheConstants.IOT_DEVICE_LAST_DATA, iotId);
						if (ObjUtil.isNotEmpty(jsonStr)) {
							deviceVo.setDeviceDataVos(JSONUtil.toList(jsonStr, DeviceData.class));
						}
					});
				});

				// 处理房间中的设备数据
				roomVo.getDeviceVos().forEach(deviceVo -> {
					String iotId = deviceVo.getIotId();
					// 从 Redis 中获取设备最新数据
					String jsonStr = (String) redisTemplate.opsForHash().get(CacheConstants.IOT_DEVICE_LAST_DATA, iotId);
					if (ObjUtil.isNotEmpty(jsonStr)) {
						deviceVo.setDeviceDataVos(JSONUtil.toList(jsonStr, DeviceData.class));
					}
				});
			});
		}

		return roomVos;
	}
}
