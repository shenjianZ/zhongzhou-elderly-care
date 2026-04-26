package com.zzyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.nursing.dto.ElderPageQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.ElderMapper;
import com.zzyl.nursing.domain.Elder;
import com.zzyl.nursing.service.IElderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 老人Service业务层处理
 *
 * @Author: Zhy
 * @Date: 2025-02-25
 */
@Service
@RequiredArgsConstructor
public class ElderServiceImpl extends ServiceImpl<ElderMapper, Elder> implements IElderService {

	private final ElderMapper elderMapper;

	/**
	 * 查询老人
	 *
	 * @param id 老人主键
	 * @return 老人
	 */
	@Override
	public Elder selectElderById(Long id) {
		return elderMapper.selectById(id);
	}

	/**
	 * 查询老人列表
	 *
	 * @param elder 老人
	 * @return 老人
	 */
	@Override
	public List<Elder> selectElderList(Elder elder) {
		return elderMapper.selectElderList(elder);
	}

	/**
	 * 新增老人
	 *
	 * @param elder 老人
	 * @return 结果
	 */
	@Override
	public int insertElder(Elder elder) {
		return elderMapper.insert(elder);
	}

	/**
	 * 修改老人
	 *
	 * @param elder 老人
	 * @return 结果
	 */
	@Override
	public int updateElder(Elder elder) {
		return elderMapper.updateById(elder);
	}

	/**
	 * 批量删除老人
	 *
	 * @param ids 需要删除的老人主键
	 * @return 结果
	 */
	@Override
	public int deleteElderByIds(Long[] ids) {
		return elderMapper.deleteBatchIds(Arrays.asList(ids));
	}

	/**
	 * 删除老人信息
	 *
	 * @param id 老人主键
	 * @return 结果
	 */
	@Override
	public int deleteElderById(Long id) {
		return elderMapper.deleteById(id);
	}

	/**
	 * 查询已经入住的老人列表
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public TableDataInfo<Elder> pageQuery(ElderPageQuery elderPageQuery) {
		LambdaQueryWrapper<Elder> queryWrapper = new LambdaQueryWrapper<>();

		// 创建分页对象
		Page<Elder> page = new Page<>(elderPageQuery.getPageNum(), elderPageQuery.getPageSize());

		// 安装状态查询
		queryWrapper.select(Elder::getId, Elder::getName, Elder::getIdCardNo, Elder::getBedNumber);
		// 状态查询
		if (ObjUtil.isNotEmpty(elderPageQuery.getName())) {
			queryWrapper.like(Elder::getName, elderPageQuery.getName());
		}
		if (ObjUtil.isNotEmpty(elderPageQuery.getIdCardNo())) {
			queryWrapper.like(Elder::getIdCardNo, elderPageQuery.getIdCardNo());
		}


		// 执行分页
		page = page(page, queryWrapper);

		return new TableDataInfo(page.getRecords(), (int) page.getTotal());
	}
}
