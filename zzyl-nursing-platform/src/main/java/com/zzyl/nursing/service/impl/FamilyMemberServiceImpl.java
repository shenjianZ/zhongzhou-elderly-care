package com.zzyl.nursing.service.impl;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.framework.web.service.TokenService;
import com.zzyl.nursing.domain.*;
import com.zzyl.nursing.dto.ElderDto;
import com.zzyl.nursing.dto.QueryDevicePropertyDto;
import com.zzyl.nursing.dto.UserLoginRequestDto;
import com.zzyl.nursing.mapper.*;
import com.zzyl.nursing.service.IWeChatService;
import com.zzyl.nursing.vo.DevicePropertyVo;
import com.zzyl.nursing.vo.FamilyMemberElderVo;
import com.zzyl.nursing.vo.LoginVo;
import lombok.RequiredArgsConstructor;
import org.apache.xmlbeans.impl.xb.xsdschema.NamespaceList;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.service.IFamilyMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * 老人家属Service业务层处理
 *
 * @author Zhy
 * @date 2025-03-03
 */
@Service
@RequiredArgsConstructor
public class FamilyMemberServiceImpl extends ServiceImpl<FamilyMemberMapper, FamilyMember> implements IFamilyMemberService {

	private final FamilyMemberMapper familyMemberMapper;
	private final IWeChatService weChatService;
	private final TokenService tokenService;
	private final ElderServiceImpl elderServiceImpl;
	private final FamilyMemberElderMapper familyMemberElderMapper;
	private final ElderMapper elderMapper;
	private final BedMapper bedMapper;
	private final DeviceServiceImpl deviceServiceImpl;
	private final RedisTemplate redisTemplate;
	private final DeviceDataMapper deviceDataMapper;

	static List<String> DEFAULT_NICKNAME_PREFIX = ListUtil.of("生活更美好",
		"大桔大利",
		"日富一日",
		"好柿开花",
		"柿柿如意",
		"一椰暴富",
		"大柚所为",
		"杨梅吐气",
		"天生荔枝"
	);

	/**
	 * 查询老人家属
	 *
	 * @param id 老人家属主键
	 * @return 老人家属
	 */
	@Override
	public FamilyMember selectFamilyMemberById(Long id) {
		return familyMemberMapper.selectById(id);
	}

	/**
	 * 查询老人家属列表
	 *
	 * @param familyMember 老人家属
	 * @return 老人家属
	 */
	@Override
	public List<FamilyMember> selectFamilyMemberList(FamilyMember familyMember) {
		return familyMemberMapper.selectFamilyMemberList(familyMember);
	}

	/**
	 * 新增老人家属
	 *
	 * @param familyMember 老人家属
	 * @return 结果
	 */
	@Override
	public int insertFamilyMember(FamilyMember familyMember) {
		return familyMemberMapper.insert(familyMember);
	}

	/**
	 * 修改老人家属
	 *
	 * @param familyMember 老人家属
	 * @return 结果
	 */
	@Override
	public int updateFamilyMember(FamilyMember familyMember) {
		return familyMemberMapper.updateById(familyMember);
	}

	/**
	 * 批量删除老人家属
	 *
	 * @param ids 需要删除的老人家属主键
	 * @return 结果
	 */
	@Override
	public int deleteFamilyMemberByIds(Long[] ids) {
		return familyMemberMapper.deleteBatchIds(Arrays.asList(ids));
	}

	/**
	 * 删除老人家属信息
	 *
	 * @param id 老人家属主键
	 * @return 结果
	 */
	@Override
	public int deleteFamilyMemberById(Long id) {
		return familyMemberMapper.deleteById(id);
	}

	/**
	 * 小程序登录
	 *
	 * @return:
	 * @param:
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public LoginVo login(UserLoginRequestDto userLoginRequestDto) {
		// 1. 根据 opendId 查询用户信息
		String openId = weChatService.getOpenId(userLoginRequestDto.getCode());
		if (ObjUtil.isEmpty(openId)) {
			throw new BaseException("获取 openId 失败");
		}
		// 2. 获取用户信息
		FamilyMember member = this.lambdaQuery().eq(FamilyMember::getOpenId, openId).one();
		// 3. 如果为空，则创建用户
		if (ObjUtil.isNull(member)) {
			member = new FamilyMember();
			member.setOpenId(openId);
		}
		// 4. 获取手机号
		String phoneNumber = weChatService.getPhoneNumber(userLoginRequestDto.getPhoneCode());
		// 5. 新增或更新用户信息
		insertOrUpdate(member, phoneNumber);
		// 6. 生成 token
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", member.getId());
		claims.put("nickName", member.getName());
		String token = tokenService.createToken(claims);
		// 7. 封装数据并返回
		LoginVo loginVo = new LoginVo();
		loginVo.setToken(token);
		loginVo.setNickName(member.getName());

		return loginVo;
	}

	@Override
	public void addMember(ElderDto elderDto) {
		// 校验老人姓名和身份证号是否为入住老人
		Elder elder = elderServiceImpl.lambdaQuery()
				.eq(Elder::getName, elderDto.getName())
				.eq(Elder::getIdCardNo, elderDto.getIdCard())
				.one();
		if (ObjectUtil.isNull(elder)) {
			throw new BaseException("该老人未入住，请先入住");
		}

		// 新增老人家属绑定信息
		elderDto.setCreateTime(new Date());
		elderDto.setFamilyMemberId(UserThreadLocal.getUserId());
		elderDto.setElderId(elder.getId());
		familyMemberElderMapper.insert(elderDto);
	}

	@Override
	public List<ElderDto> getMyElder() {
		return familyMemberElderMapper.getMyElder(UserThreadLocal.getUserId());
	}

	@Override
	public List<FamilyMemberElderVo> listByPage(Integer pageNum, Integer pageSize) {
		Integer startNum = (pageNum - 1) * pageSize;
		List<ElderDto> elderList = familyMemberElderMapper.getMyElderPage(UserThreadLocal.getUserId(), startNum, pageSize);
		List<FamilyMemberElderVo> familyMemberElderVoList = new ArrayList<>();
		for (ElderDto elder : elderList) {
			FamilyMemberElderVo familyMemberElderVo = new FamilyMemberElderVo();
			familyMemberElderVo.setMid(elder.getId());
			familyMemberElderVo.setMremark(elder.getRemark());
			Long elderId = elder.getElderId();
			familyMemberElderVo.setElderId(elderId);
			Elder elderInfo = elderMapper.selectElderById(elderId);
			familyMemberElderVo.setName(elderInfo.getName());
			familyMemberElderVo.setImage(elderInfo.getImage());
			familyMemberElderVo.setBedNumber(elderInfo.getBedNumber());
			String typeName = bedMapper.getRoomTypeNameByBedId(elderInfo.getBedId());
			familyMemberElderVo.setTypeName(typeName);
			Device device = deviceServiceImpl.lambdaQuery()
					.eq(Device::getBindingLocation, elderInfo.getId())
					.eq(Device::getPhysicalLocationType, -1).one();
			if (ObjectUtil.isNotNull(device)) {
				familyMemberElderVo.setIotId(device.getIotId());
				familyMemberElderVo.setDeviceName(device.getDeviceName());
				familyMemberElderVo.setProductKey(device.getProductKey());
			}
			familyMemberElderVoList.add(familyMemberElderVo);
		}
		return familyMemberElderVoList;
	}

	@Override
	public DevicePropertyVo queryDevicePropertyStatus(QueryDevicePropertyDto queryDevicePropertyDto) {
		DevicePropertyVo devicePropertyVo = new DevicePropertyVo();
		DevicePropertyList devicePropertyList = new DevicePropertyList();
		List<PropertyStatusInfo> propertyStatusInfoList = new ArrayList<>();
		Device device = deviceServiceImpl.lambdaQuery()
				.eq(Device::getProductKey, queryDevicePropertyDto.getProductKey())
				.eq(Device::getDeviceName, queryDevicePropertyDto.getDeviceName()).one();
		// List<DeviceData> deviceDataList = (List<DeviceData>)redisTemplate.opsForHash().get("iot:device_last_data", device.getIotId());
		List<DeviceData> deviceDataList = (List<DeviceData>)redisTemplate.opsForHash().get(CacheConstants.IOT_DEVICE_LAST_DATA, device.getIotId());
		for (DeviceData deviceData : deviceDataList) {
			PropertyStatusInfo propertyStatusInfo = new PropertyStatusInfo();
			propertyStatusInfo.setDataType("double");
			propertyStatusInfo.setIdentifier(deviceData.getFunctionId());
			propertyStatusInfo.setName(deviceData.getFunctionId());
			LocalDateTime alarmTime = deviceData.getAlarmTime();
			// alarmTime转为Long
			propertyStatusInfo.setTime(deviceData.getAlarmTime()
					.atZone(ZoneOffset.UTC)
					.toInstant()
					.toEpochMilli());
			propertyStatusInfo.setValue(Double.parseDouble(deviceData.getDataValue()));
			propertyStatusInfoList.add(propertyStatusInfo);
		}
		devicePropertyList.setPropertyStatusInfo(propertyStatusInfoList);
		devicePropertyVo.setList(devicePropertyList);
		return devicePropertyVo;
	}

	@Override
	public List<Map<String, Object>> queryDeviceDataListByDay(String functionId, String iotId, Long startTime, Long endTime) {
		LocalDateTime startTimeDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneOffset.UTC).plusHours(8);
		LocalDateTime endTimeDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneOffset.UTC).plusHours(8);
		List<Map<String, Object>> list = deviceDataMapper.queryDeviceDataListByDay(functionId, iotId, startTimeDate, endTimeDate);
		List<Map<String, Object>> newList = new ArrayList<>(24);
		for (int i = 0; i <= 23; i++) {
			String tailTime = ":00";
			String time;
			if (i < 10) {
				time = "0" + i + tailTime;
			} else {
				time = i + tailTime;
			}
			for (Map<String, Object> map : list) {
				if (time.equals(map.get("dateTime"))) {
					newList.add(map);
					break;
				}
			}
			Map<String, Object> map = new HashMap<>();
			map.put("dateTime", time);
			map.put("dataValue", 0.0);
			if (newList.size() != i + 1) {
				newList.add(map);
			}
		}
		return newList;
	}

	@Override
	public List<Map<String, Object>> queryDeviceDataListByWeek(String functionId, String iotId, Long startTime, Long endTime) {
		LocalDateTime startTimeDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneOffset.UTC).plusHours(8);
		LocalDateTime endTimeDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneOffset.UTC).plusHours(8);
		List<Map<String, Object>> mapList = deviceDataMapper.queryDeviceDataListByWeek(functionId, iotId, startTimeDate, endTimeDate);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd");
		List<Map<String, Object>> newList = new ArrayList<>(7);
		for (int i = 0; i < 7; i++) {
			String dateTime = startTimeDate.format(formatter);
			for (Map<String, Object> map : mapList) {
				if (dateTime.equals(map.get("dateTime"))) {
					newList.add(map);
					break;
				}
			}
			if (newList.size() != i + 1) {
				Map<String, Object> map = new HashMap<>();
				map.put("dateTime", dateTime);
				map.put("dataValue", 0.0);
				newList.add(map);
			}
			startTimeDate = startTimeDate.plusDays(1);
		}
		return newList;
	}

	/**
	 * 新增或更新用户信息
	 *
	 * @return:
	 * @param:
	 */
	private void insertOrUpdate(FamilyMember member, String phoneNumber) {
		// 更新手机号
		if (!StrUtil.equals(member.getPhone(), phoneNumber)) {
			member.setPhone(phoneNumber);
		}

		// 更新用户
		if (ObjUtil.isNotNull(member.getId())) {
			this.updateById(member);
			return;
		}

		// 新增用户
		String nickName = DEFAULT_NICKNAME_PREFIX.get((int) (Math.random() * DEFAULT_NICKNAME_PREFIX.size())) + StrUtil.sub(member.getPhone(),
			7,
			member.getPhone().length()
		);
		member.setName(nickName);
		this.save(member);
	}
}
