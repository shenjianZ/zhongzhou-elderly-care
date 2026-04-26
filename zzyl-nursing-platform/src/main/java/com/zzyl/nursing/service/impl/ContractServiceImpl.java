package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.bean.BeanException;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.ContractMapper;
import com.zzyl.nursing.domain.Contract;
import com.zzyl.nursing.service.IContractService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * 合同表Service业务层处理
 *
 * @Author: Zhy
 * @Date: 2025-02-25
 */
@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements IContractService {
	@Autowired
	private ContractMapper contractMapper;

	/**
	 * 查询合同表
	 *
	 * @param id 合同表主键
	 * @return 合同表
	 */
	@Override
	public Contract selectContractById(Integer id) {
		return contractMapper.selectById(id);
	}

	/**
	 * 查询合同表列表
	 *
	 * @param contract 合同表
	 * @return 合同表
	 */
	@Override
	public List<Contract> selectContractList(Contract contract) {
		return contractMapper.selectContractList(contract);
	}

	/**
	 * 新增合同表
	 *
	 * @param contract 合同表
	 * @return 结果
	 */
	@Override
	public int insertContract(Contract contract) {
		return contractMapper.insert(contract);
	}

	/**
	 * 修改合同表
	 *
	 * @param contract 合同表
	 * @return 结果
	 */
	@Override
	public int updateContract(Contract contract) {
		return contractMapper.updateById(contract);
	}

	/**
	 * 批量删除合同表
	 *
	 * @param ids 需要删除的合同表主键
	 * @return 结果
	 */
	@Override
	public int deleteContractByIds(Integer[] ids) {
		return contractMapper.deleteBatchIds(Arrays.asList(ids));
	}

	/**
	 * 删除合同表信息
	 *
	 * @param id 合同表主键
	 * @return 结果
	 */
	@Override
	public int deleteContractById(Integer id) {
		return contractMapper.deleteById(id);
	}

	/**
	 * 更新合同状态
	 *
	 * @return:
	 * @param:
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateContractStatus() {
		// 当前时间大于开始时间，小于结束时间，状态为0，则更新状态为1
		LocalDateTime now = LocalDateTime.now();
		List<Contract> notStartList  = this.lambdaQuery()
			.eq(Contract::getStatus, 0)
			.le(Contract::getStartDate, now)
			.ge(Contract::getEndDate, now)
			.list();
		notStartList .forEach(v -> v.setStatus(1));
		boolean flag = this.updateBatchById(notStartList );
		if (!flag && ObjectUtil.isNotEmpty(notStartList )) {
			throw new BeanException("将合同状态由 “未生效” 更新为 “已生效” 失败！");
		}

		// 当前时间大于结束时间，状态为1，则更新状态为3
		List<Contract> endList = this.lambdaQuery().eq(Contract::getStatus, 1).lt(Contract::getEndDate, now).list();
		endList.forEach(v -> v.setStatus(3));
		flag = this.updateBatchById(endList);
		if (!flag && ObjectUtil.isNotEmpty(endList)) {
			throw new BeanException("将合同状态由 “已生效” 更新为 “已失效” 失败！");
		}

		return 1;
	}
}
