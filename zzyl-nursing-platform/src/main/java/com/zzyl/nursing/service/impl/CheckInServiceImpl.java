package com.zzyl.nursing.service.impl;

import cn.hutool.core.util.IdcardUtil;
import com.alibaba.fastjson2.JSON;

import java.util.Arrays;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.nursing.domain.*;
import com.zzyl.nursing.dto.CheckInApplyDto;
import com.zzyl.nursing.dto.CheckInElderDto;
import com.zzyl.nursing.dto.ElderFamilyDto;
import com.zzyl.nursing.mapper.*;
import com.zzyl.nursing.util.CodeGeneratorUtil;
import com.zzyl.nursing.vo.CheckInConfigVo;
import com.zzyl.nursing.vo.CheckInDetailVo;
import com.zzyl.nursing.vo.CheckInElderVo;
import com.zzyl.nursing.vo.ElderFamilyVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.service.ICheckInService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * 入住表Service业务层处理
 *
 * @Author: Zhy
 * @Date: 2025-02-25
 */
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements ICheckInService {

	private final CheckInMapper checkInMapper;
	private final ElderMapper elderMapper;
	private final BedMapper bedMapper;
	private final ContractMapper contractMapper;
	private final CheckInConfigMapper checkInConfigMapper;

	private static int sort = 0;

	/**
	 * 查询入住表
	 *
	 * @param id 入住表主键
	 * @return 入住表
	 */
	@Override
	public CheckIn selectCheckInById(Long id) {
		return checkInMapper.selectById(id);
	}

	/**
	 * 查询入住表列表
	 *
	 * @param checkIn 入住表
	 * @return 入住表
	 */
	@Override
	public List<CheckIn> selectCheckInList(CheckIn checkIn) {
		return checkInMapper.selectCheckInList(checkIn);
	}

	/**
	 * 新增入住表
	 *
	 * @param checkIn 入住表
	 * @return 结果
	 */
	@Override
	public int insertCheckIn(CheckIn checkIn) {
		return checkInMapper.insert(checkIn);
	}

	/**
	 * 修改入住表
	 *
	 * @param checkIn 入住表
	 * @return 结果
	 */
	@Override
	public int updateCheckIn(CheckIn checkIn) {
		return checkInMapper.updateById(checkIn);
	}

	/**
	 * 批量删除入住表
	 *
	 * @param ids 需要删除的入住表主键
	 * @return 结果
	 */
	@Override
	public int deleteCheckInByIds(Long[] ids) {
		return checkInMapper.deleteBatchIds(Arrays.asList(ids));
	}

	/**
	 * 删除入住表信息
	 *
	 * @param id 入住表主键
	 * @return 结果
	 */
	@Override
	public int deleteCheckInById(Long id) {
		return checkInMapper.deleteById(id);
	}

	/**
	 * 申请入住
	 *
	 * @return:
	 * @param:
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void apply(CheckInApplyDto checkInApplyDto) {
		// 1. 检验老人是否已经入住
		Elder elder = checkElderIsEnable(checkInApplyDto.getCheckInElderDto().getIdCardNo());

		// 2. 检验床位状态是否已经入住
		Bed bed = checkBedIsEnable(checkInApplyDto.getCheckInConfigDto().getBedId());

		// 3. 新增或更新老人信息
		elder = updateOrInsertElder(elder, bed, checkInApplyDto.getCheckInElderDto());

		// 4. 新增签约办理
		addContract(elder, checkInApplyDto);

		// 5. 新增入住信息
		Long checkId = addCheckIn(elder, checkInApplyDto);

		// 6. 新增入住配置
		addCheckInConfig(checkId, checkInApplyDto);
	}

	/**
	 * 新增入住配置
	 *
	 * @return:
	 * @param: checkId 入住id
	 * @param: checkInApplyDto 入住申请信息
	 */
	private void addCheckInConfig(Long checkId, CheckInApplyDto checkInApplyDto) {
		CheckInConfig checkInConfig = BeanUtil.toBean(checkInApplyDto.getCheckInConfigDto(), CheckInConfig.class);
		checkInConfig.setCheckInId(checkId);
		checkInConfig.setSortOrder(sort++);

		checkInConfigMapper.insert(checkInConfig);
	}

	/**
	 * 新增入住信息
	 *
	 * @return: Long 入住id
	 * @param: elder 老人信息
	 * @param: checkInApplyDto 入住申请信息
	 */
	private Long addCheckIn(Elder elder, CheckInApplyDto checkInApplyDto) {
		CheckIn checkIn = BeanUtil.toBean(checkInApplyDto.getCheckInConfigDto(), CheckIn.class);
		checkIn.setElderName(elder.getName());
		checkIn.setElderId(elder.getId());
		checkIn.setIdCardNo(elder.getIdCardNo());
		checkIn.setBedNumber(elder.getBedNumber());
		checkIn.setStatus(0);
		checkIn.setSortOrder(sort++);

		// 家属信息
		List<ElderFamilyDto> elderFamilyDtoList = checkInApplyDto.getElderFamilyDtoList();
		checkIn.setRemark(JSON.toJSONString(elderFamilyDtoList));

		save(checkIn);

		return checkIn.getId();
	}

	/**
	 * 新增签约办理
	 *
	 * @return:
	 * @param:
	 */
	private void addContract(Elder elder, CheckInApplyDto checkInApplyDto) {
		Contract contract = BeanUtil.toBean(checkInApplyDto.getCheckInContractDto(), Contract.class);
		contract.setElderId(elder.getId().intValue());
		contract.setContractNumber(CodeGeneratorUtil.generateContractNumber());
		contract.setElderName(elder.getName());
		contract.setStartDate(checkInApplyDto.getCheckInConfigDto().getStartDate());
		contract.setEndDate(checkInApplyDto.getCheckInConfigDto().getEndDate());
		contract.setStatus(0);
		contract.setSortOrder(sort++);
		contract.setRemark("");

		contractMapper.insert(contract);
	}

	/**
	 * 新增或更新老人信息
	 *
	 * @return: Elder 老人信息
	 * @param: checkInElderDto 前端传来的老人信息
	 * @param: bed 床位信息
	 */
	private Elder updateOrInsertElder(Elder elder, Bed bed, CheckInElderDto checkInElderDto) {
		if (ObjectUtil.isNotNull(elder)) {
			BeanUtil.copyProperties(checkInElderDto, elder);
		} else {
			elder = BeanUtil.toBean(checkInElderDto, Elder.class);
		}
		elder.setStatus(1);
		elder.setBedNumber(bed.getBedNumber());
		elder.setBedId(bed.getId());

		if (ObjectUtil.isNull(elder.getId())) {
			// 新增
			elderMapper.insert(elder);
		} else {
			// 更新
			elderMapper.updateById(elder);
		}
		return elder;
	}

	/**
	 * 检验床位状态是否已经入住
	 *
	 * @return: Bed 床位信息
	 * @param: bedId 床位id
	 * @param: i 床位状态
	 */
	private Bed checkBedIsEnable(Long bedId) {
		Bed bed = bedMapper.selectBedById(bedId);
		bed.setBedStatus(1);
		bedMapper.updateById(bed);
		return bed;
	}

	/**
	 * 检验老人是否已经入住
	 *
	 * @return: Elder 老人信息
	 * @param: idCardNo 身份证号
	 */
	private Elder checkElderIsEnable(String idCardNo) {
		Elder elder = elderMapper.selectOne(new LambdaQueryWrapper<Elder>().eq(Elder::getIdCardNo, idCardNo));
		if (ObjectUtil.isNotNull(elder) && elder.getStatus().equals(1)) {
			throw new BaseException("该老人已经入住，请勿重复申请");
		}
		return elder;
	}

	/**
	 * 入住详情
	 *
	 * @return:
	 * @param: id 入住id
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public CheckInDetailVo getCheckInInfo(Long id) {
		// 1. 查询入住配置信息
		CheckInConfigVo checkInConfigVo = checkInMapper.selectCheckInInfoById(id);

		// 2. 封装家属信息
		List<ElderFamilyVo> elderFamilyVoList = null;
		if (ObjectUtil.isNotNull(checkInConfigVo.getRemark())) {
			String remark = checkInConfigVo.getRemark();
			// 使用 parseArray 方法并指定泛型类型
			elderFamilyVoList = JSON.parseArray(remark, ElderFamilyVo.class);
		}

		// 3. 查询合同信息
		Contract contract = contractMapper.selectContractByElderId(id);

		// 4. 查询老人信息
		CheckInElderVo checkInElderVo = elderMapper.selectByCheckId(id);
		// 根据老人出生日期计算年龄
		int age = IdcardUtil.getAgeByIdCard(checkInElderVo.getIdCardNo());
		checkInElderVo.setAge(age);

		// 5. 封装入住详情信息
		CheckInDetailVo checkInDetailVo = new CheckInDetailVo();
		checkInDetailVo.setCheckInConfigVo(checkInConfigVo);
		checkInDetailVo.setElderFamilyVoList(elderFamilyVoList);
		checkInDetailVo.setContract(contract);
		checkInDetailVo.setCheckInElderVo(checkInElderVo);

		return checkInDetailVo;
	}

}
