package com.zzyl.nursing.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.nursing.config.WebSocketServer;
import com.zzyl.nursing.domain.AlertData;
import com.zzyl.nursing.domain.DeviceData;
import com.zzyl.nursing.mapper.DeviceMapper;
import com.zzyl.nursing.service.IAlertDataService;
import com.zzyl.nursing.vo.AlertNotifyVo;
import com.zzyl.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.AlertRuleMapper;
import com.zzyl.nursing.domain.AlertRule;
import com.zzyl.nursing.service.IAlertRuleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 报警规则Service业务层处理
 *
 * @author Zhy
 * @date 2025-03-11
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertRuleServiceImpl extends ServiceImpl<AlertRuleMapper, AlertRule> implements IAlertRuleService {

	private final AlertRuleMapper alertRuleMapper;
	private final RedisTemplate redisTemplate;
	private final DeviceMapper deviceMapper;
	private final SysUserRoleMapper sysUserRoleMapper;
	private final IAlertDataService alertDataService;
	private final WebSocketServer webSocketServer;

	/**
	 * 查询报警规则
	 *
	 * @param id 报警规则主键
	 * @return 报警规则
	 */
	@Override
	public AlertRule selectAlertRuleById(Long id) {
		return alertRuleMapper.selectById(id);
	}

	/**
	 * 查询报警规则列表
	 *
	 * @param alertRule 报警规则
	 * @return 报警规则
	 */
	@Override
	public List<AlertRule> selectAlertRuleList(AlertRule alertRule) {
		return alertRuleMapper.selectAlertRuleList(alertRule);
	}

	/**
	 * 新增报警规则
	 *
	 * @param alertRule 报警规则
	 * @return 结果
	 */
	@Override
	public int insertAlertRule(AlertRule alertRule) {
		return alertRuleMapper.insert(alertRule);
	}

	/**
	 * 修改报警规则
	 *
	 * @param alertRule 报警规则
	 * @return 结果
	 */
	@Override
	public int updateAlertRule(AlertRule alertRule) {
		return alertRuleMapper.updateById(alertRule);
	}

	/**
	 * 批量删除报警规则
	 *
	 * @param ids 需要删除的报警规则主键
	 * @return 结果
	 */
	@Override
	public int deleteAlertRuleByIds(Long[] ids) {
		return alertRuleMapper.deleteBatchIds(Arrays.asList(ids));
	}

	/**
	 * 删除报警规则信息
	 *
	 * @param id 报警规则主键
	 * @return 结果
	 */
	@Override
	public int deleteAlertRuleById(Long id) {
		return alertRuleMapper.deleteById(id);
	}

	/**
	 * 新增报警规则
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public void createAlertRule(AlertRule alertRule) {

	}

	/**
	 * 设备数据异常告警
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public void alertFilter() {
		// log.warn("触发报警规则，记录数据---->");

		// 1.查询所有规则
		long count = this.lambdaQuery().eq(AlertRule::getStatus, 1).count();
		if (count <= 0) {
			return;
		}
		// 2.查询所有设备上报的数据
		List<Object> values = redisTemplate.opsForHash().values(CacheConstants.IOT_DEVICE_LAST_DATA);
		if (CollUtil.isEmpty(values)) {
			return;
		}
		// 遍历 values，把每个设备上报的数据合并到一起
		List<DeviceData> deviceDataList = new ArrayList<>();
		values.forEach(v -> deviceDataList.addAll(JSONUtil.toList(JSONUtil.toJsonStr(v), DeviceData.class)));

		LocalDateTime now = LocalDateTime.now();
		// 3.根据报警规则进行过滤
		deviceDataList.forEach(d -> alertFilterEveryData(d, now));

	}

	/**
	 * 过滤每一条数据。去匹配规则，是否满足，满足插入到数据库
	 *
	 * @param data 上报数据
	 */
	private void alertFilterEveryData(DeviceData data, LocalDateTime now) {
		// 1.判断上报数据和现在时间是否超过了1分钟，如果超过，则认为该数据没有匹配价值
		LocalDateTime alarmTime = data.getAlarmTime();
		long between = LocalDateTimeUtil.between(alarmTime, now, ChronoUnit.SECONDS);
		if (between > 60) {
			return;
		}
		// 2.查询当前产品下所有设备指定物理型且开启的规则
		List<AlertRule> allDeviceRules = this.lambdaQuery()
			.eq(AlertRule::getProductKey, data.getProductKey())
			.eq(AlertRule::getFunctionId, data.getFunctionId())
			.eq(AlertRule::getIotId, -1)
			.eq(AlertRule::getStatus, 1)
			.list();
		// 3.查询当前产品下指定设备指定物理型且开启的规则
		List<AlertRule> OneDeviceRules = this.lambdaQuery()
			.eq(AlertRule::getProductKey, data.getProductKey())
			.eq(AlertRule::getFunctionId, data.getFunctionId())
			.eq(AlertRule::getIotId, data.getIotId())
			.eq(AlertRule::getStatus, 1)
			.list();
		// 4.合并一起
		Collection<AlertRule> allRules = CollUtil.addAll(allDeviceRules, OneDeviceRules);
		if (CollUtil.isEmpty(allRules)) {
			return;
		}
		// 5.遍历规则，进行匹配
		allRules.forEach(rule -> dataFilterRule(rule, data));
	}

	/**
	 * 根据规则匹配数据
	 *
	 * @param rule 规则
	 * @param data 上报数据
	 */
	private void dataFilterRule(AlertRule rule, DeviceData data) {
		// 1.判断是否在生效时间内
		String[] split = rule.getAlertEffectivePeriod().split("~");
		LocalTime startTime = LocalTime.parse(split[0]);
		LocalTime endTime = LocalTime.parse(split[1]);
		// 数据上报的时间
		LocalTime localTime = data.getAlarmTime().toLocalTime();
		// 如果上报的时间不在生效时段内，则结束请求
		if (localTime.isBefore(startTime) || localTime.isAfter(endTime)) {
			return;
		}

		// 2.校验数据是否达到阈值
		// 统计次数的key
		String aggCountKey = CacheConstants.ALERT_TRIGGER_COUNT_PREFIX +
			rule.getId() +
			data.getIotId() +
			":" +
			data.getFunctionId();
		Double value = Double.valueOf(data.getDataValue());
		Double ruleValue = rule.getValue();
		int result = Double.compare(value, ruleValue);
		// 0相等  1:value大   -1:ruleValue大
		if ((">=".equals(rule.getOperator()) && result < 0) || ("<".equals(rule.getOperator()) && result >= 0)) {
			redisTemplate.delete(aggCountKey);
			return;
		}

		// 3.判断是否在沉默时间内，如果在，则本次报警不发送
		String aggSilentKey = CacheConstants.ALERT_SILENT_PREFIX +
			rule.getId() +
			data.getIotId() +
			":" +
			data.getFunctionId();
		if (ObjUtil.isNotNull(redisTemplate.opsForValue().get(aggSilentKey))) {
			return;
		}

		// 4.判断报警次数 是否等于持续周期
		Integer aggCount = (Integer) redisTemplate.opsForValue().get(aggCountKey);
		aggCount = ObjUtil.isNull(aggCount) ? 1 : aggCount + 1;
		if (ObjUtil.notEqual(aggCount, rule.getDuration())) {
			redisTemplate.opsForValue().set(aggCountKey, aggCount);
			return;
		}

		// 5.校验通过，删除报警次数，增加沉默周期
		redisTemplate.delete(aggCountKey);
		redisTemplate.opsForValue().set(aggSilentKey, 1, rule.getAlertSilentPeriod(), TimeUnit.MINUTES);

		// 6.查询老人报警数据对应的护理人员
		List<Long> userIds = new ArrayList<>();
		if (rule.getAlertDataType().equals(0)) {
			// 是老人报警类型
			if (data.getLocationType().equals(0)) {
				// 是随身设备
				userIds = deviceMapper.selectNursingIdsByIotIdWithElder(data.getIotId());
			} else if (data.getLocationType().equals(1) && data.getPhysicalLocationType().equals(2)) {
				// 固定设备
				userIds = deviceMapper.selectNursingIdsByIotIdWithBed(data.getIotId());
			}
		} else {
			// 设备异常数据
			// 7.查询设备报警数据对应的维修人员
			userIds = sysUserRoleMapper.selectUserIdByRoleName("维修工");
		}

		userIds = userIds.stream().filter(Objects::nonNull).collect(Collectors.toList());
		// 通知管理员
		List<Long> adminIds = sysUserRoleMapper.selectUserIdByRoleName("超级管理员");
		Collection<Long> allUserIds = CollUtil.addAll(adminIds, userIds).stream().distinct().collect(Collectors.toList());
		// 8.保存数据
		List<AlertData> alertData = insertBatch(allUserIds, rule, data);

		// 9. Websocket推送消息
		websocketNotify(alertData.get(0), rule, allUserIds);
	}


	/**
	 * websocket推送消息
	 *
	 * @param alertData 报警数据
	 * @param rule 规则
	 * @param allUserIds 通知人列表
	 */
	private void websocketNotify(AlertData alertData, AlertRule rule, Collection<Long> allUserIds) {

		// 属性拷贝
		AlertNotifyVo alertNotifyVo = BeanUtil.toBean(alertData, AlertNotifyVo.class);
		alertNotifyVo.setFunctionName(rule.getFunctionName());
		alertNotifyVo.setAlertDataType(rule.getAlertDataType());
		alertNotifyVo.setNotifyType(1);
		// 向指定的人推送消息
		webSocketServer.sendMessageToConsumer(alertNotifyVo, allUserIds);
	}

	/**
	 * 批量保存报警规则数据
	 *
	 * @param userIds 通知人列表
	 * @param rule 规则
	 * @param data 上报数据
	 */
	private List<AlertData> insertBatch(Collection<Long> userIds, AlertRule rule, DeviceData data) {
		List<AlertData> dataList = userIds.stream().map(userId -> {
			AlertData alertData = BeanUtil.toBean(data, AlertData.class);
			alertData.setId(null);
			alertData.setAlertRuleId(rule.getId());
			String alertReason = StrUtil.format("{}{}{},持续了{}周期报警",
				rule.getFunctionName(),
				rule.getOperator(),
				rule.getValue(),
				rule.getDuration()
			);
			alertData.setAlertReason(alertReason);
			alertData.setType(rule.getAlertDataType());
			alertData.setStatus(0);
			alertData.setUserId(userId);
			return alertData;
		}).collect(Collectors.toList());
		alertDataService.saveBatch(dataList);

		return dataList;
	}

}
