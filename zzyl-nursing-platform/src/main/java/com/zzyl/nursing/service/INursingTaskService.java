package com.zzyl.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzyl.common.core.domain.R;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.nursing.domain.Elder;
import com.zzyl.nursing.domain.NursingTask;
import com.zzyl.nursing.dto.NursingTaskCancelDto;
import com.zzyl.nursing.dto.NursingTaskDoDto;
import com.zzyl.nursing.dto.NursingTaskDto;
import com.zzyl.nursing.dto.NursingTaskUpdateDto;
import com.zzyl.nursing.vo.NursingTaskVo;

import java.util.List;

/**
 * 护理任务Service接口
 * 
 * @author ruoyi
 * @date 2024-09-27
 */
public interface INursingTaskService extends IService<NursingTask>
{
    /**
     * 查询护理任务
     * 
     * @param id 护理任务主键
     * @return 护理任务
     */
    public NursingTaskVo selectNursingTaskById(Long id);

    /**
     * 查询护理任务列表
     * 
     * @param nursingTask 护理任务
     * @return 护理任务集合
     */
    public List<NursingTask> selectNursingTaskList(NursingTask nursingTask);

    /**
     * 新增护理任务
     * 
     * @param nursingTask 护理任务
     * @return 结果
     */
    public int insertNursingTask(NursingTask nursingTask);

    /**
     * 修改护理任务
     * 
     * @param nursingTask 护理任务
     * @return 结果
     */
    public int updateNursingTask(NursingTask nursingTask);

    /**
     * 批量删除护理任务
     * 
     * @param ids 需要删除的护理任务主键集合
     * @return 结果
     */
    public int deleteNursingTaskByIds(Long[] ids);

    /**
     * 删除护理任务信息
     * 
     * @param id 护理任务主键
     * @return 结果
     */
    public int deleteNursingTaskById(Long id);

    /**
     * 生成护理任务
     * @param elder
     */
    void createMonthTask(Elder elder);

    /**
     * 查询护理任务列表
     * @param nursingTaskDto 查询参数
     * @return 结果
     */
    TableDataInfo<NursingTaskVo> list(NursingTaskDto nursingTaskDto);

    /**
     * 取消任务
     * @param nursingTaskCancelDto 取消任务原因dto
     */
    void cancel(NursingTaskCancelDto nursingTaskCancelDto);

    /**
     * 执行任务
     * @param nursingTaskDoDto 执行任务dto
     */
    void doTask(NursingTaskDoDto nursingTaskDoDto);

    /**
     * 更新时间
     * @param nursingTaskUpdateDto
     */
    void updateTime(NursingTaskUpdateDto nursingTaskUpdateDto);
}
