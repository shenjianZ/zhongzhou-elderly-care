package com.zzyl.nursing.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * LoginVO
 *
 * @author Zhy
 * @version 1.0
 * @date 2025-03-03 16:15
 */
@Data
@ApiModel(value = "登录对象")
public class LoginVo {

	@ApiModelProperty(value = "JWT token")
	private String token;

	@ApiModelProperty(value = "昵称")
	private String nickName;

}
