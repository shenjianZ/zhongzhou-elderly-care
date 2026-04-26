package com.zzyl.nursing.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * C端用户登录
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-03 16:14
 */
@Data
public class UserLoginRequestDto {

	@ApiModelProperty("昵称")
	private String nickName;

	@ApiModelProperty("登录临时凭证")
	private String code;

	@ApiModelProperty("手机号临时凭证")
	private String phoneCode;

}
