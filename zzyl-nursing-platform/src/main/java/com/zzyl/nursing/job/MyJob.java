package com.zzyl.nursing.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @Author: Zhy
 * @Date: 2025-02-27 17:17
 * @Version: 1.0
 */
@Component("myJob")
@Slf4j
public class MyJob {

	public void executeTask(){
		log.info("MyJob executeTask");
	}

}
