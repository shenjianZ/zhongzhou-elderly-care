package com.zzyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.bean.BeanUtil;
import com.zzyl.common.constant.CacheConstants;
import com.zzyl.nursing.vo.NursingLevelVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingLevelMapper;
import com.zzyl.nursing.domain.NursingLevel;
import com.zzyl.nursing.service.INursingLevelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 护理等级Service业务层处理
 *
 * @Author: Zhy
 * @Date: 2024-12-30
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NursingLevelServiceImpl extends ServiceImpl<NursingLevelMapper, NursingLevel> implements INursingLevelService {

	private final NursingLevelMapper nursingLevelMapper;
	private final RedisTemplate<Object, Object> redisTemplate;

	/**
	 * 查询护理等级
	 *
	 * @param id 护理等级主键
	 * @return 护理等级
	 */
	@Override
	public NursingLevel selectNursingLevelById(Long id) {
		return nursingLevelMapper.selectById(id);
	}

	/**
	 * 查询护理等级列表
	 *
	 * @param nursingLevel 护理等级
	 * @return 护理等级
	 */
	@Override
	public List<NursingLevel> selectNursingLevelList(NursingLevel nursingLevel) {
		return nursingLevelMapper.selectNursingLevelList(nursingLevel);
	}

	/**
	 * 新增护理等级
	 *
	 * @param nursingLevel 护理等级
	 * @return 结果
	 */
	@Override
	public int insertNursingLevel(NursingLevel nursingLevel) {
		nursingLevelMapper.insert(nursingLevel);
		// 清除缓存
		redisTemplate.delete(CacheConstants.NURSING_LEVEL_ENABLE_KEY);
		return 1;
	}

	/**
	 * 修改护理等级
	 *
	 * @param nursingLevel 护理等级
	 * @return 结果
	 */
	@Override
	public int updateNursingLevel(NursingLevel nursingLevel) {
		nursingLevelMapper.updateById(nursingLevel);
		// 清除缓存
		redisTemplate.delete(CacheConstants.NURSING_LEVEL_ENABLE_KEY);
		return 1;
	}

	/**
	 * 批量删除护理等级
	 *
	 * @param ids 需要删除的护理等级主键
	 * @return 结果
	 */
	@Override
	public int deleteNursingLevelByIds(Long[] ids) {
		nursingLevelMapper.deleteBatchIds(Arrays.asList(ids));
		// 清除缓存
		redisTemplate.delete(CacheConstants.NURSING_LEVEL_ENABLE_KEY);
		return 1;
	}

	/**
	 * 删除护理等级信息
	 *
	 * @param id 护理等级主键
	 * @return 结果
	 */
	@Override
	public int deleteNursingLevelById(Long id) {
		nursingLevelMapper.deleteById(id);
		// 清除缓存
		redisTemplate.delete(CacheConstants.NURSING_LEVEL_ENABLE_KEY);
		return 1;
	}

	/**
	 * 查询护理等级Vo列表
	 *
	 * @param nursingLevel 条件
	 * @return 结果
	 */
	@Override
	public List<NursingLevelVo> selectNursingLevelVoList(NursingLevel nursingLevel) {
		return nursingLevelMapper.selectNursingLevelVoList(nursingLevel);
	}

	/**
	 * 查询所有已启用的护理等级
	 *
	 * @return:
	 * @param:
	 */
	@Override
	public List<NursingLevel> selectByEnable() {
		// 从 Redis 中获取已启用的护理等级
		List<NursingLevel> list;

		try {
			list = (List<NursingLevel>) redisTemplate.opsForValue().get(CacheConstants.NURSING_LEVEL_ENABLE_KEY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (BeanUtil.isNotEmpty(list)) {
			log.info("从 Redis 中获取已启用的护理等级");
			return list;
		}

		log.info("从数据库中查询护理等级");

		list = this.lambdaQuery().eq(NursingLevel::getStatus, 1).list();

		// 将从数据库中查询到的护理等级存入 Redis 中
		if (BeanUtil.isNotEmpty(list)) {
			// 使用 try-catch 防止 Redis 失败导致主程序崩溃
			try {
				redisTemplate.opsForValue().set(CacheConstants.NURSING_LEVEL_ENABLE_KEY, list, 60 * 60 * 24, TimeUnit.SECONDS);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return list;
	}
}
