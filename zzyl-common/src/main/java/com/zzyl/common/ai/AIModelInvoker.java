package com.zzyl.common.ai;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.model.chat.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 集成百度千帆大模型
 *
 * @Author: Zhy
 * @Date: 2025-02-28 16:39
 * @Version: 1.0
 */
@Component
@RequiredArgsConstructor
public class AIModelInvoker {

	private final BaiduAIProperties baiduAIProperties;

	/**
	 * 调用百度智能对话API
	 *
	 * @return: 返回的文本结果
	 * @param: prompt 输入的提示文本
	 */
	public String qianfanInvoker(String prompt) {
		/*
		 * 第一个参数：认证类型，固定选择 Auth.TYPE_OAUTH
		 * 第二个参数：accessKeyId，从百度云控制台创建的应用里可以找到
		 * 第三个参数：accessKeySecret，从百度云控制台创建的应用里可以找到
		 */
		Qianfan qianfan = new Qianfan(Auth.TYPE_OAUTH, baiduAIProperties.getAccessKey(), baiduAIProperties.getSecretKey());

		ChatResponse response = qianfan.chatCompletion()
			.model(baiduAIProperties.getQianfanModel())
			.temperature(0.6)
			.maxOutputTokens(2000)
			.responseFormat("json_object")
			.addMessage("user", prompt)
			.execute();

		return response.getResult();
	}

}
