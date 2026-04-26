package com.zzyl.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.zzyl.nursing.domain.Contract;

/**
 * 合同表Mapper接口
 * 
 * @author Zhy
 * @date 2025-02-25
 */
@Mapper
public interface ContractMapper extends BaseMapper<Contract>
{
    /**
     * 查询合同表
     * 
     * @param id 合同表主键
     * @return 合同表
     */
    public Contract selectContractById(Integer id);

    /**
     * 查询合同表列表
     * 
     * @param contract 合同表
     * @return 合同表集合
     */
    public List<Contract> selectContractList(Contract contract);

    /**
     * 新增合同表
     * 
     * @param contract 合同表
     * @return 结果
     */
    public int insertContract(Contract contract);

    /**
     * 修改合同表
     * 
     * @param contract 合同表
     * @return 结果
     */
    public int updateContract(Contract contract);

    /**
     * 删除合同表
     * 
     * @param id 合同表主键
     * @return 结果
     */
    public int deleteContractById(Integer id);

    /**
     * 批量删除合同表
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteContractByIds(Integer[] ids);

    /**
     * 根据入住ID查询合同信息
     *
     * @return:
     * @param:
     */
	Contract selectContractByElderId(Long id);
}
