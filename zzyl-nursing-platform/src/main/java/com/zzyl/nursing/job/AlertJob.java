package com.zzyl.nursing.job;

import com.zzyl.nursing.service.IAlertRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 设备数据告警过滤定时任务
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-11 15:56
 */
@Component
@RequiredArgsConstructor
public class AlertJob {

	private final IAlertRuleService alertRuleService;

	public void deviceDataAlertFilter() {
		alertRuleService.alertFilter();
	}

}
