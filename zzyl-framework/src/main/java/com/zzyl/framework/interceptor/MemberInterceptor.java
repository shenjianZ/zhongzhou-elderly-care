package com.zzyl.framework.interceptor;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.framework.web.service.TokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器，用于拦截C端请求
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-05 13:21
 */
@Component
@RequiredArgsConstructor
public class MemberInterceptor implements HandlerInterceptor {

	private final TokenService tokenService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		// 如果不是 Controller 层请求，直接放行
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		// 获取 token
		String token = request.getHeader("authorization");

		// 如果 token 为空，直接返回 401 错误
		if (StrUtil.isEmpty(token)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
			return false;
		}

		// 解析 token
		Claims claims = tokenService.parseToken(token);
		if (ObjUtil.isEmpty(claims)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
			return false;
		}

		// 提取 token 中的数据
		Long userId = claims.get("id", Long.class);
		if (ObjUtil.isEmpty(userId)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
			return false;
		}

		// 将用户 id 放入 ThreadLocal
		UserThreadLocal.set(userId);

		return true;
	}

	@Override
	public void afterCompletion(
		HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex
	) {
		try {
			UserThreadLocal.remove();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
