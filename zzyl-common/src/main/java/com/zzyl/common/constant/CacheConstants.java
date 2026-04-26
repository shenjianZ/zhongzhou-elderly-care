package com.zzyl.common.constant;

/**
 * 缓存的key 常量
 *
 * @author ruoyi
 */
public class CacheConstants {
	/** 登录用户 redis key */
	public static final String LOGIN_TOKEN_KEY = "login_tokens:";

	/** 验证码 redis key */
	public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

	/** 参数管理 cache key */
	public static final String SYS_CONFIG_KEY = "sys_config:";

	/** 字典管理 cache key */
	public static final String SYS_DICT_KEY = "sys_dict:";

	/** 防重提交 redis key */
	public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

	/** 限流 redis key */
	public static final String RATE_LIMIT_KEY = "rate_limit:";

	/** 登录账户密码错误次数 redis key */
	public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

	/** 护理等级 redis key */
	public static final String NURSING_LEVEL_ENABLE_KEY = "nursing_level_enable:all";

	/** 护理计划 redis key */
	public static final String NURSING_PLAN_ENABLE_KEY = "nursing_plan_enable:all";

	/** 护理项目 redis key */
	public static final String NURSING_PROJECT_ENABLE_KEY = "nursing_project_enable:all";

	/** 体检报告 redis key */
	public static final String HEALTH_REPORT_KEY = "health:report:";

	/** IOT 设备 redis key */
	public static final String IOT_ALL_PRODUCT = "iot:all_product";

	/** IOT 设备 redis key */
	public static final String IOT_DEVICE_LAST_DATA = "iot:device_last_data:";

	/** 报警规则联系触发次数 */
	public static final String ALERT_TRIGGER_COUNT_PREFIX = "iot:alert_trigger_count:rule_";

	/** 报警规则沉默周期 */
	public static final String ALERT_SILENT_PREFIX = "iot:alert_silent:rule_";

}
