package com.zzyl.qf;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.model.chat.ChatResponse;

/**
 * TODO
 *
 * @Author: Zhy
 * @Date: 2025-02-28 14:12
 * @Version: 1.0
 */
public class AIModelTest {

	public static void main(String[] args) {
		/*
		 * 第一个参数：认证类型，固定选择 Auth.TYPE_OAUTH
		 * 第二个参数：accessKeyId，从百度云控制台创建的应用里可以找到
		 * 第三个参数：accessKeySecret，从百度云控制台创建的应用里可以找到
		 */
		Qianfan qianfan = new Qianfan(
			Auth.TYPE_OAUTH,
			"fONdLXB5zssLPU5PTIBRsyjl",
			"9ntuGXNc482mKuOGy6pe1THSOzMB1oTs"
		);
		ChatResponse response = qianfan.chatCompletion()
			.model("ERNIE-4.0-8K-Preview")
			.temperature(0.6)
			.maxOutputTokens(2000)
			.responseFormat("json_object")
			.addMessage("user", "你能帮我分析一份完整的体检报告吗？")
			.execute();

		String result = response.getResult();
		System.out.println(result);
	}

}
