package com.zzyl;
import java.math.BigDecimal;
import java.util.Date;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.zzyl.common.utils.http.HttpUtils;
import com.zzyl.nursing.domain.NursingProject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpUtil 测试类
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-03 22:23
 */
public class HttpTest {

	@Test
	public void testGet1() {
		String content = HttpUtil.get("https://www.baidu.com");
		System.out.println(content);
	}

	@Test
	public void testGet2() {
		Map<String, Object> params = Map.of("pageNum", 1, "pageSize", 5);
		HttpResponse response = HttpUtil.createGet("http://localhost:9003/nursing/project/list")
			.header("authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6IjEyNGYwYzY4LWI1YjgtNDhiNS05OWVmLTIyYTRkOTcyNzk0NCJ9.Oz1sjYOHAmB6Xh28ORpVDEmCnBzt3lapV3O3LcQ7NTdPsggafdm9_BcHYcADwlimBQMG7_CRSko3AmmHNGxq1w")
			.form(params)
			.execute();
		System.out.println(response.body());
	}

	@Test
	public void testPost() {
		NursingProject nursingProject = new NursingProject();
		nursingProject.setName("测试计划222");
		nursingProject.setOrderNo(1);
		nursingProject.setUnit("次");
		nursingProject.setPrice(new BigDecimal("50"));
		nursingProject.setImage("222.jpg");
		nursingProject.setNursingRequirement("测试计划222测试计划222");
		nursingProject.setStatus(1);

		HttpResponse response = HttpUtil.createPost("http://localhost:9003/nursing/project").header(
			"authorization",
			"Bearer eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6IjEyNGYwYzY4LWI1YjgtNDhiNS05OWVmLTIyYTRkOTcyNzk0NCJ9.Oz1sjYOHAmB6Xh28ORpVDEmCnBzt3lapV3O3LcQ7NTdPsggafdm9_BcHYcADwlimBQMG7_CRSko3AmmHNGxq1w"
		).body(JSONUtil.toJsonStr(nursingProject)).execute();

		System.out.println(response.body());
	}

}
