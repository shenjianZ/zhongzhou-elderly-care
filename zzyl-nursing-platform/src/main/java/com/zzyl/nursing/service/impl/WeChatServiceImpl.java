package com.zzyl.nursing.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.nursing.service.IWeChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 微信小程序服务实现类
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-03 17:06
 */
@Service
@Slf4j
public class WeChatServiceImpl implements IWeChatService {

	// 登录
	private static final String REQUEST_URL = "https://api.weixin.qq.com/sns/jscode2session?grant_type=authorization_code";

	// 获取token
	private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

	// 获取手机号
	private static final String PHONE_REQUEST_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=";


	@Value("${wechat.mini-program.appId}")
	private String appid;

	@Value("${wechat.mini-program.secret}")
	private String secret;

	/**
	 * 获取微信小程序的openid
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public String getOpenId(String code) {
		Map<String, Object> params = new HashMap<>();
		params.put("appid", appid);
		params.put("secret", secret);
		params.put("js_code", code);

		String content = HttpUtil.get(REQUEST_URL, params);

		if (StrUtil.isNotEmpty(content)) {
			// 解析json数据
			JSONObject jsonObject = JSONUtil.parseObj(content);

			if (ObjUtil.isNotEmpty(jsonObject) && ObjUtil.isEmpty(jsonObject.get("errcode"))) {
				return Objects.toString(jsonObject.get("openid"));
			}
		}
		throw new BaseException("获取openid失败");
	}

	/**
	 * 获取微信小程序的手机号码
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public String getPhoneNumber(String detailCode) {
		String accessToken = getAccessToken();

		Map<String, Object> map = Map.of("code", detailCode);
		String result = HttpUtil.post(PHONE_REQUEST_URL + accessToken, JSONUtil.toJsonStr(map));

		if (StrUtil.isNotEmpty(result)) {
			JSONObject jsonObject = JSONUtil.parseObj(result);
			if (ObjUtil.isNotEmpty(jsonObject) && ObjUtil.equal(jsonObject.getInt("errcode"), 0)) {
				return jsonObject.getJSONObject("phone_info").getStr("phoneNumber");
			}
		}
		throw new BaseException("获取手机号失败");
	}

	/**
	 * 获取微信小程序的 access_token
	 *
	 * @return:
	 * @param:
	 */
	public String getAccessToken() {
		Map<String, Object> params = new HashMap<>();
		params.put("appid", appid);
		params.put("secret", secret);

		String content = HttpUtil.get(TOKEN_URL, params);

		if (StrUtil.isNotEmpty(content)) {
			// 解析json数据
			JSONObject jsonObject = JSONUtil.parseObj(content);

			if (ObjUtil.isNotEmpty(jsonObject) && ObjUtil.isEmpty(jsonObject.get("errcode"))) {
				return JSONUtil.parseObj(content).getStr("access_token");
			}
		}
		throw new BaseException("获取openid失败");
	}
}
