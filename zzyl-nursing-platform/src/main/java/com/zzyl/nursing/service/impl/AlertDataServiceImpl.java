package com.zzyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import com.zzyl.common.utils.SecurityUtils;
import com.zzyl.nursing.dto.AlertDataPageQueryDto;
import com.zzyl.nursing.dto.HandleAlertDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.AlertDataMapper;
import com.zzyl.nursing.domain.AlertData;
import com.zzyl.nursing.service.IAlertDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 报警数据Service业务层处理
 *
 * @author Zhy
 * @date 2025-03-11
 */
@Service
@RequiredArgsConstructor
public class AlertDataServiceImpl extends ServiceImpl<AlertDataMapper, AlertData> implements IAlertDataService {

	private final AlertDataMapper alertDataMapper;

	/**
	 * 查询报警数据
	 *
	 * @param id 报警数据主键
	 * @return 报警数据
	 */
	@Override
	public AlertData selectAlertDataById(Long id) {
		return alertDataMapper.selectById(id);
	}

	/**
	 * 查询报警数据列表
	 *
	 * @param alertData 报警数据
	 * @return 报警数据
	 */
	@Override
	public List<AlertData> selectAlertDataList(AlertData alertData) {
		return alertDataMapper.selectAlertDataList(alertData);
	}

	/**
	 * 新增报警数据
	 *
	 * @param alertData 报警数据
	 * @return 结果
	 */
	@Override
	public int insertAlertData(AlertData alertData) {
		return alertDataMapper.insert(alertData);
	}

	/**
	 * 修改报警数据
	 *
	 * @param alertData 报警数据
	 * @return 结果
	 */
	@Override
	public int updateAlertData(AlertData alertData) {
		return alertDataMapper.updateById(alertData);
	}

	/**
	 * 批量删除报警数据
	 *
	 * @param ids 需要删除的报警数据主键
	 * @return 结果
	 */
	@Override
	public int deleteAlertDataByIds(Long[] ids) {
		return alertDataMapper.deleteBatchIds(Arrays.asList(ids));
	}

	/**
	 * 删除报警数据信息
	 *
	 * @param id 报警数据主键
	 * @return 结果
	 */
	@Override
	public int deleteAlertDataById(Long id) {
		return alertDataMapper.deleteById(id);
	}

	/**
	 * 查询报警数据列表
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public List<AlertData> pageQueryAlertData(AlertDataPageQueryDto alertDataPageQueryDto) {
		return alertDataMapper.pageQueryAlertData(alertDataPageQueryDto);
	}

	/**
	 * 处理设备报警数据
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public void handleAlertData(HandleAlertDataDto handleAlertDataDto) {
		AlertData alertData = BeanUtil.toBean(handleAlertDataDto, AlertData.class);
		alertData.setStatus(1);
		alertData.setProcessorName(SecurityUtils.getUsername());
		alertData.setProcessorId(SecurityUtils.getUserId());

		updateById(alertData);
	}
}
