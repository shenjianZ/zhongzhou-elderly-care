package com.zzyl.nursing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-12 12:00
 */
@Configuration
public class WebSocketConfig {

	/**
	 * 注册基于 @ServerEndpoint 声明的 Websocket Endpoint
	 */
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

}
