package com.zzyl.nursing.vo;

import com.zzyl.nursing.domain.Contract;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 入住详情响应模型
 *
 * @Author: Zhy
 * @Date: 2025-02-25
 **/
@ApiModel(description = "入住详情响应模型")
@Data
public class CheckInDetailVo {
    /**
     * 老人信息
     */
    @ApiModelProperty(value = "老人响应信息")
    private CheckInElderVo checkInElderVo;

    /**
     * 家属信息
     */
    @ApiModelProperty(value = "家属响应信息")
    private List<ElderFamilyVo> elderFamilyVoList;

    /**
     * 入住配置
     */
    @ApiModelProperty(value = "入住配置响应信息")
    private CheckInConfigVo checkInConfigVo;

    /**
     * 签约办理
     */
    @ApiModelProperty(value = "签约办理响应信息")
    private Contract contract;

}
