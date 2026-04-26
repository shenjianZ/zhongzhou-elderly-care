package com.zzyl.nursing.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 合同编码生成器
 *
 * @Author: Zhy
 * @Date: 2025-02-25 20:24
 * @Version: 1.0
 */
public class CodeGeneratorUtil {

	private static final AtomicInteger counter = new AtomicInteger(1);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	private static final int MAX_COUNTER = 9999;

	// 私有构造函数避免实例化
	private CodeGeneratorUtil() {
		throw new AssertionError("Utility class cannot be instantiated.");
	}

	/**
	 * 生成一个16位的合同编号。
	 *
	 * @return 16位的合同编号字符串。
	 */
	public static String generateContractNumber() {
		// 获取当前时间并格式化
		String currentTime = LocalDateTime.now().format(formatter);

		// 获取并递增计数器
		int count = counter.getAndIncrement();
		if (count > MAX_COUNTER) {
			// 如果计数器超过最大值，则重置为最小值
			count = 1;
			counter.set(count);
		}

		// 将计数器格式化为4位字符串
		String countStr = String.format("%04d", count);

		// 返回完整的合同编号
		return currentTime + countStr;
	}

}
