package com.zzyl.nursing.service;

import java.util.List;
import java.util.Map;

import com.zzyl.nursing.domain.FamilyMember;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzyl.nursing.dto.ElderDto;
import com.zzyl.nursing.dto.QueryDevicePropertyDto;
import com.zzyl.nursing.dto.UserLoginRequestDto;
import com.zzyl.nursing.vo.DevicePropertyVo;
import com.zzyl.nursing.vo.FamilyMemberElderVo;
import com.zzyl.nursing.vo.LoginVo;

/**
 * 老人家属Service接口
 *
 * @author Zhy
 * @date 2025-03-03
 */
public interface IFamilyMemberService extends IService<FamilyMember> {
	/**
	 * 查询老人家属
	 *
	 * @param id 老人家属主键
	 * @return 老人家属
	 */
	public FamilyMember selectFamilyMemberById(Long id);

	/**
	 * 查询老人家属列表
	 *
	 * @param familyMember 老人家属
	 * @return 老人家属集合
	 */
	public List<FamilyMember> selectFamilyMemberList(FamilyMember familyMember);

	/**
	 * 新增老人家属
	 *
	 * @param familyMember 老人家属
	 * @return 结果
	 */
	public int insertFamilyMember(FamilyMember familyMember);

	/**
	 * 修改老人家属
	 *
	 * @param familyMember 老人家属
	 * @return 结果
	 */
	public int updateFamilyMember(FamilyMember familyMember);

	/**
	 * 批量删除老人家属
	 *
	 * @param ids 需要删除的老人家属主键集合
	 * @return 结果
	 */
	public int deleteFamilyMemberByIds(Long[] ids);

	/**
	 * 删除老人家属信息
	 *
	 * @param id 老人家属主键
	 * @return 结果
	 */
	public int deleteFamilyMemberById(Long id);

	/**
	 * 小程序登录
	 *
	 * @return:
	 * @param:
	 */
	LoginVo login(UserLoginRequestDto userLoginRequestDto);

    void addMember(ElderDto elderDto);

	List<ElderDto> getMyElder();

	List<FamilyMemberElderVo> listByPage(Integer pageNum, Integer pageSize);

	DevicePropertyVo queryDevicePropertyStatus(QueryDevicePropertyDto queryDevicePropertyDto);

	List<Map<String, Object>> queryDeviceDataListByDay(String functionId, String iotId, Long startTime, Long endTime);

	List<Map<String, Object>> queryDeviceDataListByWeek(String functionId, String iotId, Long startTime, Long endTime);
}
