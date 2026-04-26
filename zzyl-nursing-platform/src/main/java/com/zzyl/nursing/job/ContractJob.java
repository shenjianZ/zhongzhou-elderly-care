package com.zzyl.nursing.job;

import com.zzyl.nursing.service.IContractService;
import com.zzyl.nursing.service.IMemberReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 创建定时任务，更新合同状态
 *
 * @Author: Zhy
 * @Date: 2025-02-27 19:16
 * @Version: 1.0
 */
@Component("contractJob")
@RequiredArgsConstructor
public class ContractJob {

	private final IContractService contractService;
	private final IMemberReservationService memberReservationService;

	public void updateContractStatus() {
		contractService.updateContractStatus();
	}

	public void updateReservationStatus() {
		memberReservationService.updateReservationStatus();
	}

}
