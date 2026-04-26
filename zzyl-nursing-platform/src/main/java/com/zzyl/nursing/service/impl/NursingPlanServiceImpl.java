package com.zzyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.util.ObjectUtil;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.utils.bean.BeanUtils;
import com.zzyl.nursing.domain.NursingProjectPlan;
import com.zzyl.nursing.dto.NursingPlanDto;
import com.zzyl.nursing.mapper.NursingProjectPlanMapper;
import com.zzyl.nursing.vo.NursingPlanVo;
import com.zzyl.nursing.vo.NursingProjectPlanVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingPlanMapper;
import com.zzyl.nursing.domain.NursingPlan;
import com.zzyl.nursing.service.INursingPlanService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * 护理计划Service业务层处理
 *
 * @Author: Zhy
 * @Date: 2024-12-30
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NursingPlanServiceImpl extends ServiceImpl<NursingPlanMapper, NursingPlan> implements INursingPlanService {

	private final NursingPlanMapper nursingPlanMapper;
	private final NursingProjectPlanMapper nursingProjectPlanMapper;
	private final RedisTemplate<Object, Object> redisTemplate;

	/**
	 * 查询护理计划
	 *
	 * @param id 护理计划主键
	 * @return 护理计划
	 */
	@Override
	public NursingPlanVo selectNursingPlanById(Long id) {
		// 1.获取护理计划基本信息
		NursingPlan nursingPlan = getById(id);

		// 2.获取当前护理计划关联的护理项目列表
		List<NursingProjectPlanVo> projectList = nursingProjectPlanMapper.getProjectListByPlanId(id);

		// 3.构建一个NursingPlanVo对象并返回
		NursingPlanVo nursingPlanVo = new NursingPlanVo();
		BeanUtils.copyProperties(nursingPlan, nursingPlanVo);
		nursingPlanVo.setProjectPlans(projectList);

		return nursingPlanVo;
	}

	/**
	 * 查询护理计划列表
	 *
	 * @param nursingPlan 护理计划
	 * @return 护理计划
	 */
	@Override
	public List<NursingPlan> selectNursingPlanList(NursingPlan nursingPlan) {
		return nursingPlanMapper.selectNursingPlanList(nursingPlan);
	}

	/**
	 * 新增护理计划
	 *
	 * @param nursingPlanDto 护理计划
	 * @return 结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int insertNursingPlan(NursingPlanDto nursingPlanDto) {
		// 1.保存护理计划基本信息
		NursingPlan nursingPlan = new NursingPlan();
		BeanUtils.copyProperties(nursingPlanDto, nursingPlan);
		save(nursingPlan);

		// 2.批量保存护理计划关联的护理项目
		List<NursingProjectPlan> projectPlans = nursingPlanDto.getProjectPlans();
		int rows = nursingProjectPlanMapper.insertBatch(projectPlans, nursingPlan.getId());

		// 删除 Redis 缓存中所有护理计划
		try {
			redisTemplate.delete(CacheConstants.NURSING_PLAN_ENABLE_KEY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// 3.返回结果
		return rows > 0 ? 1 : 0;
	}

	/**
	 * 修改护理计划
	 *
	 * @param nursingPlanDto 护理计划
	 * @return 结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateNursingPlan(NursingPlanDto nursingPlanDto) {
		// 1.修改护理计划关联的护理项目列表
		List<NursingProjectPlan> projectPlans = nursingPlanDto.getProjectPlans();
		// 判断护理计划关联的护理项目是否为空
		if (projectPlans != null && !projectPlans.isEmpty()) {
			// 1.1 删除当前护理计划关联的所有护理项目
			nursingProjectPlanMapper.deleteByPlanId(nursingPlanDto.getId());

			// 2.2 批量保存护理计划新关联的护理项目
			nursingProjectPlanMapper.insertBatch(projectPlans, nursingPlanDto.getId());
		}

		// 2.修改护理计划的基本信息
		NursingPlan nursingPlan = new NursingPlan();
		BeanUtils.copyProperties(nursingPlanDto, nursingPlan);

		// 删除 Redis 缓存中所有护理计划
		try {
			redisTemplate.delete(CacheConstants.NURSING_PLAN_ENABLE_KEY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return updateById(nursingPlan) ? 1 : 0;
	}

	/**
	 * 批量删除护理计划
	 *
	 * @param ids 需要删除的护理计划主键
	 * @return 结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteNursingPlanByIds(Long[] ids) {
		// 1.批量删除护理计划关联的护理项目
		nursingProjectPlanMapper.batchDeleteByPlanIds(Arrays.asList(ids));

		// 2.删除护理计划基本信息
		nursingPlanMapper.deleteBatchIds(Arrays.asList(ids));

		// 删除 Redis 缓存中所有护理计划
		try {
			redisTemplate.delete(CacheConstants.NURSING_PLAN_ENABLE_KEY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return 1;
	}

	/**
	 * 删除护理计划信息
	 *
	 * @param id 护理计划主键
	 * @return 结果
	 */
	@Override
	public int deleteNursingPlanById(Long id) {
		nursingPlanMapper.deleteById(id);

		// 删除 Redis 缓存中所有护理计划
		try {
			redisTemplate.delete(CacheConstants.NURSING_PLAN_ENABLE_KEY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return 1;
	}

	/**
	 * 查询所有护理计划
	 *
	 * @return 护理计划列表
	 */
	@Override
	public List<NursingPlan> getAllNursingPlans() {
		// 从 Redis 缓存中获取所有启用状态的护理计划
		List<NursingPlan> list;

		try {
			list = (List<NursingPlan>) redisTemplate.opsForValue().get(CacheConstants.NURSING_PLAN_ENABLE_KEY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (ObjectUtil.isNotNull(list)) {
			log.info("从 Redis 缓存中获取所有启用状态的护理计划");
			return list;
		}

		log.info("从数据库中获取所有启用状态的护理计划");
		list = this.lambdaQuery().eq(NursingPlan::getStatus, 1).list();

		// 将查询到的护理计划存储到 Redis 缓存中
		if (ObjectUtil.isNotNull(list)) {
			try {
				redisTemplate.opsForValue().set(CacheConstants.NURSING_PLAN_ENABLE_KEY, list, 60 * 60 * 24, TimeUnit.SECONDS);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return list;
	}
}
