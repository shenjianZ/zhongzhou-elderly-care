package com.zzyl.nursing.service;

import java.util.List;
import com.zzyl.nursing.domain.Contract;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 合同表Service接口
 * 
 * @Author: Zhy
 * @Date: 2025-02-25
 */
public interface IContractService extends IService<Contract>
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
     * 批量删除合同表
     * 
     * @param ids 需要删除的合同表主键集合
     * @return 结果
     */
    public int deleteContractByIds(Integer[] ids);

    /**
     * 删除合同表信息
     * 
     * @param id 合同表主键
     * @return 结果
     */
    public int deleteContractById(Integer id);
    
    /**
     * 更新合同状态
     * 
     * @return: 
     * @param: 
     */
    int updateContractStatus();
}
