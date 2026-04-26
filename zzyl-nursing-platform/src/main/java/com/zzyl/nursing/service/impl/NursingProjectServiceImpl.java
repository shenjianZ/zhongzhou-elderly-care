package com.zzyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.util.ObjectUtil;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.vo.NursingProjectVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingProjectMapper;
import com.zzyl.nursing.domain.NursingProject;
import com.zzyl.nursing.service.INursingProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 护理项目Service业务层处理
 *
 * @Author: Zhy
 * @Date: 2024-12-30
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NursingProjectServiceImpl extends ServiceImpl<NursingProjectMapper, NursingProject> implements INursingProjectService {

	private final NursingProjectMapper nursingProjectMapper;
	private final RedisTemplate<Object, Object> redisTemplate;

	/**
	 * 查询护理项目
	 *
	 * @param id 护理项目主键
	 * @return 护理项目
	 */
	@Override
	public NursingProject selectNursingProjectById(Long id) {
		return nursingProjectMapper.selectById(id);
	}

	/**
	 * 查询护理项目列表
	 *
	 * @param nursingProject 护理项目
	 * @return 护理项目
	 */
	@Override
	public List<NursingProject> selectNursingProjectList(NursingProject nursingProject) {
		return nursingProjectMapper.selectNursingProjectList(nursingProject);
	}

	/**
	 * 新增护理项目
	 *
	 * @param nursingProject 护理项目
	 * @return 结果
	 */
	@Override
	public int insertNursingProject(NursingProject nursingProject) {
		return nursingProjectMapper.insert(nursingProject);
	}

	/**
	 * 修改护理项目
	 *
	 * @param nursingProject 护理项目
	 * @return 结果
	 */
	@Override
	public int updateNursingProject(NursingProject nursingProject) {
		nursingProjectMapper.updateById(nursingProject);

		// 清除缓存
		try {
			redisTemplate.delete(CacheConstants.NURSING_PROJECT_ENABLE_KEY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return 1;
	}

	/**
	 * 批量删除护理项目
	 *
	 * @param ids 需要删除的护理项目主键
	 * @return 结果
	 */
	@Override
	public int deleteNursingProjectByIds(Long[] ids) {
		nursingProjectMapper.deleteBatchIds(Arrays.asList(ids));

		// 清除缓存
		try {
			redisTemplate.delete(CacheConstants.NURSING_PROJECT_ENABLE_KEY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return 1;
	}

	/**
	 * 删除护理项目信息
	 *
	 * @param id 护理项目主键
	 * @return 结果
	 */
	@Override
	public int deleteNursingProjectById(Long id) {
		nursingProjectMapper.deleteById(id);

		// 清除缓存
		try {
			redisTemplate.delete(CacheConstants.NURSING_PROJECT_ENABLE_KEY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return 1;
	}

	/**
	 * 查询所有护理项目VO列表
	 *
	 * @return 结果
	 */
	@Override
	public List<NursingProjectVo> getAllProjects() {
		// 从缓存中获取数据
		List<NursingProjectVo> list;


		try {
			list = (List<NursingProjectVo>) redisTemplate.opsForValue().get(CacheConstants.NURSING_PROJECT_ENABLE_KEY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (ObjectUtil.isNotEmpty(list)) {
			log.info("从 Redis 缓存中获取护理项目数据");
			return list;
		}

		log.info("从数据库中获取以启用的护理项目数据");
		list = nursingProjectMapper.getAllProjects();

		// 缓存数据
		if (ObjectUtil.isNotEmpty(list)) {
			try {
				redisTemplate.opsForValue().set(CacheConstants.NURSING_PROJECT_ENABLE_KEY,
					list,
					60 * 60 * 24,
					TimeUnit.SECONDS
				);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return list;
	}
}
