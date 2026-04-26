package com.zzyl.nursing.service;

/**
 * 微信小程序服务接口
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-03 17:05
 */
public interface IWeChatService {

	/**
	 * 获取微信小程序的openid
	 * 
	 * @return: 
	 * @param: 
	 */
	 String getOpenId(String code);

	 /**
	  * 获取微信小程序的手机号码
	  * 
	  * @return: 
	  * @param: 
	  */
	 String getPhoneNumber(String detailCode);

}
