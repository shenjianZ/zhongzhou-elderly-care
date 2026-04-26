package com.zzyl.framework.interceptor;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class MyMetaObjectHandler implements MetaObjectHandler {

	private final HttpServletRequest request;

	/**
	 * 是否排除路径，排除微信端的路径，不自动填充用户id
	 *
	 * @return:
	 * @param:
	 */
	private boolean isExclude() {
		// 校验是否为 Web 请求，避免非 Web 请求导致 IllegalStateException 异常
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			try {
				HttpServletRequest request = attributes.getRequest();
				String path = request.getRequestURI();
				return !path.startsWith("/member");
			} catch (Exception e) {
				return true;
			}
		}
		return false;

		// String path = request.getRequestURI();
		// return !path.startsWith("/member");
	}

	// 插入数据时自动填充
	@Override
	public void insertFill(MetaObject metaObject) {
		if (isExclude()) {
			this.strictInsertFill(metaObject, "createBy", String.class, String.valueOf(getLoginUserId()));
		}
		this.strictInsertFill(metaObject, "createTime", Date.class, DateUtils.getNowDate());
	}

	// 修改数据时自动填充
	@Override
	public void updateFill(MetaObject metaObject) {
		if (isExclude()) {
			this.setFieldValByName("updateBy", String.valueOf(getLoginUserId()), metaObject);
		}
		this.setFieldValByName("updateTime", DateUtils.getNowDate(), metaObject);
		//        this.strictUpdateFill(metaObject, "updateBy", String.class, String.valueOf(getLoginUserId()));
		//        this.strictUpdateFill(metaObject, "updateTime", Date.class, DateUtils.getNowDate());
	}

	// 获取当前登录人的id
	public Long getLoginUserId() {
		Authentication authentication = SecurityUtils.getAuthentication();
		if (ObjectUtils.isNull(authentication) || ObjectUtils.isNull(authentication.getPrincipal()) || ObjectUtils.isNull(
			SecurityUtils.getLoginUser())) {
			return 0L;
		}

		return SecurityUtils.getLoginUser().getUserId();
	}
}
