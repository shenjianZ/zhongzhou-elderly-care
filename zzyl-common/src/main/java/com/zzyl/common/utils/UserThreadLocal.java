package com.zzyl.common.utils;

/**
 * 用户线程变量
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-05 12:26
 */
public class UserThreadLocal {

	private static final ThreadLocal<Long> LOCAL = new ThreadLocal<>();

	private UserThreadLocal() {

	}

	/**
	 * 将authUserInfo放到ThreadLocal中
	 *
	 * @param authUserInfo {@link Long}
	 */
	public static void set(Long authUserInfo) {
		LOCAL.set(authUserInfo);
	}

	/**
	 * 从ThreadLocal中获取authUserInfo
	 */
	public static Long get() {
		return LOCAL.get();
	}

	/**
	 * 从当前线程中删除authUserInfo
	 */
	public static void remove() {
		LOCAL.remove();
	}

	/**
	 * 从当前线程中获取前端用户id
	 *
	 * @return 用户id
	 */
	public static Long getUserId() {
		return LOCAL.get();
	}

}
