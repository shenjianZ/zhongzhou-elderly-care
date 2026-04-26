package com.zzyl.common.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 百度千帆大模型配置
 *
 * @Author: Zhy
 * @Date: 2025-02-28 16:38
 * @Version: 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "baidu")
public class BaiduAIProperties {

	private String accessKey;
	private String secretKey;
	private String qianfanModel;

}
