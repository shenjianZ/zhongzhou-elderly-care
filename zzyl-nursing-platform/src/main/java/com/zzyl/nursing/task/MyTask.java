package com.zzyl.nursing.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义定时任务
 *
 * @Author: Zhy
 * @Date: 2025-02-27 16:57
 * @Version: 1.0
 */
@Component
@Slf4j
public class MyTask {

	// @Scheduled(cron = "0/5 * * * * ?")
	public void executeTask() {
		log.info("{}", LocalDateTime.now());
	}

}
