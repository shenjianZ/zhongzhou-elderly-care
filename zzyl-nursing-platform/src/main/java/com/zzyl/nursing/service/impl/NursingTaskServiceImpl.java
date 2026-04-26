package com.zzyl.nursing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.common.core.domain.R;
import com.zzyl.common.core.domain.entity.SysUser;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.nursing.domain.*;
import com.zzyl.nursing.dto.NursingTaskCancelDto;
import com.zzyl.nursing.dto.NursingTaskDoDto;
import com.zzyl.nursing.dto.NursingTaskDto;
import com.zzyl.nursing.dto.NursingTaskUpdateDto;
import com.zzyl.nursing.mapper.ElderMapper;
import com.zzyl.nursing.mapper.NursingTaskMapper;
import com.zzyl.nursing.service.*;
import com.zzyl.nursing.vo.NursingPlanVo;
import com.zzyl.nursing.vo.NursingProjectPlanVo;
import com.zzyl.nursing.vo.NursingTaskVo;
import com.zzyl.system.service.ISysUserService;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 护理任务Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-09-27
 */
@Service
@Slf4j
public class NursingTaskServiceImpl extends ServiceImpl<NursingTaskMapper,NursingTask> implements INursingTaskService
{
    @Autowired
    private NursingTaskMapper nursingTaskMapper;

    /**
     * 查询护理任务
     * 
     * @param id 护理任务主键
     * @return 护理任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public NursingTaskVo selectNursingTaskById(Long id)
    {
        NursingTask nursingTask = this.lambdaQuery().eq(NursingTask::getId, id).one();
        if (ObjectUtil.isEmpty(nursingTask)) {
            throw new BaseException("查询任务不存在");
        }
        NursingTaskVo nursingTaskVo = BeanUtil.toBean(nursingTask, NursingTaskVo.class);
        if (StrUtil.isNotEmpty(nursingTask.getUpdateBy())) {
            SysUser sysUser = sysUserService.selectUserById(Long.valueOf(nursingTask.getUpdateBy()));
            nursingTaskVo.setUpdater(sysUser.getNickName());
        }
        CheckIn checkIn = checkInService.getOne(Wrappers.<CheckIn>lambdaQuery().eq(CheckIn::getElderId, nursingTask.getElderId()));
        nursingTaskVo.setAge(IdcardUtil.getAgeByIdCard(checkIn.getIdCardNo()));
        nursingTaskVo.setNursingLevelName(checkIn.getNursingLevelName());
        String nursingId = nursingTask.getNursingId();
        List<String> list = Arrays.asList(nursingId.split(","));
        List<String> nursingName = new ArrayList<>();
        list.forEach(nurser -> {
            SysUser User = sysUserService.selectUserById(Long.valueOf(nurser));
            nursingName.add(User.getNickName());
        });
        nursingTaskVo.setNursingName(nursingName);
        return nursingTaskVo;
    }

    /**
     * 查询护理任务列表
     * 
     * @param nursingTask 护理任务
     * @return 护理任务
     */
    @Override
    public List<NursingTask> selectNursingTaskList(NursingTask nursingTask)
    {
        return nursingTaskMapper.selectNursingTaskList(nursingTask);
    }

    /**
     * 新增护理任务
     * 
     * @param nursingTask 护理任务
     * @return 结果
     */
    @Override
    public int insertNursingTask(NursingTask nursingTask)
    {
        return save(nursingTask)?1:0;
    }

    /**
     * 修改护理任务
     * 
     * @param nursingTask 护理任务
     * @return 结果
     */
    @Override
    public int updateNursingTask(NursingTask nursingTask)
    {
        return updateById(nursingTask)?1:0;
    }

    /**
     * 批量删除护理任务
     * 
     * @param ids 需要删除的护理任务主键
     * @return 结果
     */
    @Override
    public int deleteNursingTaskByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除护理任务信息
     * 
     * @param id 护理任务主键
     * @return 结果
     */
    @Override
    public int deleteNursingTaskById(Long id)
    {
        return removeById(id)?1:0;
    }

    @Autowired
    private ICheckInService checkInService;

    @Autowired
    private ICheckInConfigService checkInConfigService;

    @Autowired
    private INursingLevelService nursingLevelService;

    @Autowired
    private INursingPlanService nursingPlanService;

    @Autowired
    private INursingElderService nursingElderService;

    @Autowired
    private INursingProjectService nursingProjectService;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 生成护理任务
     *
     * @param elder
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMonthTask(Elder elder) {
        //校验
        if (elder == null || elder.getId() == null) {
            log.info("老人不能为空");
            return;
        }

        //获取入住信息
        CheckIn checkIn = checkInService.getOne(Wrappers.<CheckIn>lambdaQuery().eq(CheckIn::getElderId, elder.getId()));

        //获取入住及入住配置信息
        CheckInConfig checkInConfig = checkInConfigService.getOne(Wrappers.<CheckInConfig>lambdaQuery().eq(CheckInConfig::getCheckInId, checkIn.getId()));
        if (ObjectUtil.isEmpty(checkInConfig)) {
            return;
        }
        //开始时间
        LocalDateTime startTime = LocalDateTime.now();
        //判断开始是在当天时间
        if (checkInConfig.getFeeStartDate().toLocalDate().isEqual(LocalDateTime.now().toLocalDate())) {
            startTime = checkInConfig.getFeeStartDate();
        }

        //护理等级
        NursingLevel nursingLevel = nursingLevelService.selectNursingLevelById(checkInConfig.getNursingLevelId());
        //护理计划
        NursingPlanVo nursingPlanVo = nursingPlanService.selectNursingPlanById(nursingLevel.getLplanId());
        //查询老人对应的护理员列表
        List<NursingElder> nursingElderList = nursingElderService.list(Wrappers.<NursingElder>lambdaQuery().eq(NursingElder::getElderId, elder.getId()));
        String nursingIds;
        if (CollUtil.isNotEmpty(nursingElderList)) {
            List<Long> list = nursingElderList.stream().map(NursingElder::getNursingId).collect(Collectors.toList());
            //多个id用逗号分隔，转换为字符串
            nursingIds = StringUtils.join(list, ",");
        } else {
            nursingIds = "";
        }

        //查询所有的护理项目
        List<NursingProject> list = nursingProjectService.list();
        //转换为map key是id,value是name
        Map<Long, String> nursingProjectMap = list.stream().collect(Collectors.toMap(NursingProject::getId, NursingProject::getName));

        //组装护理任务
        List<NursingTask> nursingTasks = new ArrayList<>();

        LocalDateTime finalStartTime = startTime;
        nursingPlanVo.getProjectPlans().forEach(v -> {
            //执行频次
            Integer executeFrequency = Math.toIntExact(v.getExecuteFrequency());
            //执行时间
            String executeTime = v.getExecuteTime();
            LocalTime localTime = LocalTime.parse(executeTime);
            //开始执行时间
            LocalDateTime firstExecutionTime = LocalDateTime.of(finalStartTime.toLocalDate(), localTime);
            // 计算相差天数

            int year = finalStartTime.getYear();
            Month month = checkInConfig.getFeeStartDate().getMonth();
            int i = finalStartTime.toLocalDate().lengthOfMonth();

            LocalDateTime monthEndTime = LocalDateTime.of(finalStartTime.getYear(), checkInConfig.getFeeStartDate().getMonth(), finalStartTime.toLocalDate().lengthOfMonth(), 23, 59);
            // 间隔天数
            long diffDays = monthEndTime.toLocalDate().toEpochDay() - finalStartTime.toLocalDate().toEpochDay() + 1;
            if (v.getExecuteCycle().equals(0)) {
                // 日
                createTaskByDay(firstExecutionTime, diffDays, nursingTasks, executeFrequency, elder, v, nursingIds,nursingProjectMap);
            } else if (v.getExecuteCycle().equals(1)) {
                // 周
                createTaskByWeek(firstExecutionTime, diffDays, nursingTasks, executeFrequency, elder, v, monthEndTime, nursingIds,nursingProjectMap);
            } else {
                // 月
                createTaskByMonth(firstExecutionTime, monthEndTime, nursingTasks, executeFrequency, elder, v, nursingIds,nursingProjectMap);
            }
        });

        if (CollUtil.isEmpty(nursingTasks)) {
            return;
        }
        nursingTasks.forEach(v -> {
            v.setTaskType("1");
        });
        saveBatch(nursingTasks);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableDataInfo<NursingTaskVo> list(NursingTaskDto nursingTaskDto) {
        Page<NursingTask> page = new Page<>(nursingTaskDto.getPageNum(), nursingTaskDto.getPageSize());
        LocalDateTime start = nursingTaskDto.getStartTime();
        LocalDateTime end = nursingTaskDto.getEndTime();
        page(page, Wrappers.<NursingTask>lambdaQuery()
                .like(StrUtil.isNotEmpty(nursingTaskDto.getElderName()), NursingTask::getElderName, nursingTaskDto.getElderName())
                .eq(ObjectUtil.isNotEmpty(nursingTaskDto.getNurseId()), NursingTask::getNursingId, nursingTaskDto.getNurseId())
                .eq(ObjectUtil.isNotEmpty(nursingTaskDto.getProjectId()), NursingTask::getProjectId, nursingTaskDto.getProjectId())
                .between(ObjectUtil.isNotEmpty(start) && ObjectUtil.isNotEmpty(end), NursingTask::getEstimatedServerTime, start, end));
        List<NursingTask> records = page.getRecords();
        List<NursingTaskVo> collect = records.stream().map(v -> {
            NursingTaskVo nursingTaskVo = BeanUtil.toBean(v, NursingTaskVo.class);
            NursingProject nursingProject = nursingProjectService.getOne(Wrappers.<NursingProject>lambdaQuery().eq(NursingProject::getId, v.getProjectId()));
            nursingTaskVo.setProjectName(nursingProject.getName());
            String nursingId = nursingTaskVo.getNursingId();
            List<String> list = Arrays.asList(nursingId.split(","));
            List<String> nursingName = new ArrayList<>();
            list.forEach(nurser -> {
                SysUser User = sysUserService.selectUserById(Long.valueOf(nurser));
                nursingName.add(User.getNickName());
            });
            nursingTaskVo.setNursingName(nursingName);
            v.setProjectName(nursingProject.getName());
            updateById(v);
            return nursingTaskVo;
        }).collect(Collectors.toList());

        TableDataInfo<NursingTaskVo> dataInfo = new TableDataInfo<>();
        dataInfo.setCode(200);
        dataInfo.setMsg("查询成功");
        dataInfo.setRows(collect);
        dataInfo.setTotal(page.getTotal());
        return dataInfo;
    }

    /**
     * 取消任务
     * @param nursingTaskCancelDto 取消任务原因dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(NursingTaskCancelDto nursingTaskCancelDto) {
        NursingTask nursingTask = this.lambdaQuery().eq(NursingTask::getId, nursingTaskCancelDto.getTaskId()).one();
        if (ObjectUtil.isEmpty(nursingTask)) {
            throw new BaseException("取消任务不存在");
        }
        nursingTask.setCancelReason(nursingTaskCancelDto.getReason());
        nursingTask.setStatus(3);
        updateById(nursingTask);
    }

    /**
     * 执行任务
     * @param nursingTaskDoDto 执行任务dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doTask(NursingTaskDoDto nursingTaskDoDto) {
        NursingTask nursingTask = this.lambdaQuery().eq(NursingTask::getId, nursingTaskDoDto.getTaskId()).one();
        if (ObjectUtil.isEmpty(nursingTask)) {
            throw new BaseException("执行任务不存在");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        nursingTask.setRealServerTime(LocalDateTime.parse(nursingTaskDoDto.getEstimatedServerTime(), formatter));
        nursingTask.setMark(nursingTaskDoDto.getMark());
        nursingTask.setTaskImage(nursingTaskDoDto.getTaskImage());
        nursingTask.setStatus(2);
        updateById(nursingTask);
    }

    /**
     * 更新时间
     * @param nursingTaskUpdateDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTime(NursingTaskUpdateDto nursingTaskUpdateDto) {
        NursingTask nursingTask = this.lambdaQuery().eq(NursingTask::getId, nursingTaskUpdateDto.getTaskId()).one();
        if (ObjectUtil.isEmpty(nursingTask)) {
            throw new BaseException("执行任务不存在");
        }
        // 创建一个自定义的 DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        nursingTask.setEstimatedServerTime(LocalDateTime.parse(nursingTaskUpdateDto.getEstimatedServerTime(), formatter));
        updateById(nursingTask);
    }

    /**
     * 按月创建任务
     *
     * @param firstExecutionTime
     * @param monthEndTime
     * @param nursingTasks
     * @param executeFrequency
     * @param elder
     * @param v
     */
    private void createTaskByMonth(LocalDateTime firstExecutionTime, LocalDateTime monthEndTime, List<NursingTask> nursingTasks, Integer executeFrequency, Elder elder, NursingProjectPlanVo v, String nursingIds, Map<Long, String> nursingProjectMap) {
        LocalDateTime executionTime = firstExecutionTime;
        Integer diffDay = (monthEndTime.plusSeconds(1).getDayOfMonth() - executionTime.getDayOfMonth()) / executeFrequency;
        for (int x = 0; x < executeFrequency; x++) {
            // 根据时间差和执行顺序计算每个任务的具体时间
            LocalDateTime seconds = executionTime.plusDays(diffDay * x);
            // 初始化护理任务对象
            NursingTask nursingTask = getNursingTask(elder, v, nursingIds, seconds,nursingProjectMap);
            // 将生成的任务添加到任务列表中
            nursingTasks.add(nursingTask);
        }
    }

    /**
     * 根据周为单位创建护理任务
     *
     * 此方法旨在根据给定的开始时间、差异天数、执行频率等参数，为指定的老人生成护理任务列表
     * 它考虑了跨年情况以及每月结束时间的限制，避免生成越过月末的有效任务
     *
     * @param firstExecutionTime 首次执行时间，用于计算后续任务的时间点
     * @param diffDays 任务之间的时间差，以天为单位，此方法专注于周级别，故此值应与7的倍数有关
     * @param nursingTasks 护理任务列表，方法将新生成的任务添加到此列表中
     * @param executeFrequency 执行频率，决定每周内任务执行的次数
     * @param elder 老人信息对象，任务相关的老人信息由此对象提供
     * @param v 护理项目计划的视图对象，包含项目ID等信息
     * @param monthEndTime 每月结束时间的限制，确保任务不会跨月生成
     * @param nursingIds 护理员ID列表，以字符串形式传递，用于分配任务给护理员
     */
    private void createTaskByWeek(LocalDateTime firstExecutionTime, long diffDays, List<NursingTask> nursingTasks, Integer executeFrequency, Elder elder, NursingProjectPlanVo v, LocalDateTime monthEndTime, String nursingIds,Map<Long, String> nursingProjectMap) {
        int i;
        // 以7天为步长遍历差异天数，创建每周的任务
        for (i = 0; i < diffDays - 7; i = i + 7) {
            // 计算每周结束时间
            LocalDateTime dayEndTime = LocalDateTime.of(firstExecutionTime.plusDays(i + 7).toLocalDate(), LocalTime.of(23, 59));
            // 计算本周的执行起始时间
            LocalDateTime executionTime = firstExecutionTime.plusDays(i);
            // 根据执行频率计算时间差，用于确定本周内各任务的时间点
            Integer diffDay = (dayEndTime.plusSeconds(1).getDayOfYear() - executionTime.getDayOfYear()) / executeFrequency;
            // 根据执行频率生成本周的任务
            for (int x = 0; x < executeFrequency; x++) {
                // 根据时间差和执行顺序计算每个任务的具体时间
                LocalDateTime seconds = executionTime.plusDays(diffDay * x);
                // 初始化护理任务对象
                NursingTask nursingTask = getNursingTask(elder, v, nursingIds, seconds,nursingProjectMap);
                // 将生成的任务添加到任务列表中
                nursingTasks.add(nursingTask);
            }
        }

        // 处理边界情况，当i超过diffDays-7且小于diffDays时
        if (i > diffDays - 7 && i < diffDays) {
            // 计算到达diffDays时的结束时间
            LocalDateTime dayEndTime = LocalDateTime.of(firstExecutionTime.plusDays(i + 7).toLocalDate(), LocalTime.of(23, 59));
            // 如果结束时间与开始时间年份不同，则返回，避免跨年任务
            if (ObjectUtil.notEqual(dayEndTime.getYear(), firstExecutionTime.getYear())) {
                return;
            }
            // 计算执行时间
            LocalDateTime executionTime = firstExecutionTime.plusDays(i);
            // 计算时间差，确定任务时间点
            Integer diffDay = (dayEndTime.plusSeconds(1).getDayOfYear() - executionTime.getDayOfYear()) / executeFrequency;
            // 根据执行频率生成任务，直到月末
            for (int x = 0; x < executeFrequency; x++) {
                // 计算每个任务的时间
                LocalDateTime seconds = executionTime.plusDays(diffDay * x);
                // 如果任务时间超过月末结束时间，则停止生成
                if (seconds.isAfter(monthEndTime)) {
                    break;
                }
                // 初始化护理任务对象
                NursingTask nursingTask = getNursingTask(elder, v, nursingIds, seconds,nursingProjectMap);
                // 将生成的任务添加到任务列表中
                nursingTasks.add(nursingTask);
            }
        }

    }




    /**
     * 按日创建任务
     * @param firstExecutionTime
     * @param diffDays
     * @param nursingTasks
     * @param executeFrequency
     * @param elder
     * @param v
     */
    /**
     * 根据天数创建护理任务
     * <p>
     * 该方法根据首次执行时间、任务间隔天数、护理任务列表、执行频率、老人信息和护理项目计划数据，
     * 创建一系列的护理任务。任务的创建以天为单位，直到达到任务间隔天数为止。
     *
     * @param firstExecutionTime 首次执行时间，标志着任务创建的起始时间
     * @param diffDays           任务间隔天数，确定需要创建任务的天数范围
     * @param nursingTasks       护理任务列表，创建的任务将被添加到此列表中
     * @param executeFrequency   执行频率，决定一天内任务执行的次数
     * @param elder              老人对象，包含执行任务的老人的信息
     * @param v                  护理项目计划数据对象，包含护理项目的相关信息
     */
    private void createTaskByDay(LocalDateTime firstExecutionTime, long diffDays, List<NursingTask> nursingTasks, Integer executeFrequency, Elder elder, NursingProjectPlanVo v, String nursingIds,Map<Long, String> nursingProjectMap) {
        // 遍历每一天，从首次执行时间开始，直到达到任务间隔天数
        for (int i = 0; i < diffDays; i++) {
            // 计算每一天的任务执行起始时间
            LocalDateTime executionTime = firstExecutionTime.plusDays(i);
            // 确定当天任务执行的结束时间（当天的23:59）
            LocalDateTime dayEndTime = LocalDateTime.of(executionTime.toLocalDate(), LocalTime.of(23, 59));
            // 计算当天内每次执行任务之间的小时差，用于确定执行时间点
            Integer diffHour = (dayEndTime.plusSeconds(1).getHour() - executionTime.getHour()) / executeFrequency;
            // 根据执行频率创建任务
            for (int x = 0; x < executeFrequency; x++) {
                // 计算每个任务的具体执行时间
                LocalDateTime seconds = executionTime.plusHours(diffHour * x);
                // 创建新的护理任务对象
                NursingTask nursingTask = getNursingTask(elder, v, nursingIds, seconds,nursingProjectMap);
                // 将生成的任务添加到任务列表中
                nursingTasks.add(nursingTask);
            }
        }
    }

    /**
     * 获取护理任务对象
     * @param elder
     * @param v
     * @param nursingIds
     * @param seconds
     * @return
     */
    private NursingTask getNursingTask(Elder elder, NursingProjectPlanVo v, String nursingIds, LocalDateTime seconds,Map<Long, String> nursingProjectMap) {
        NursingTask nursingTask = new NursingTask();
        // 设置任务状态
        nursingTask.setStatus(1);
        // 设置护理员ID列表
        nursingTask.setNursingId(nursingIds);
        // 老人姓名
        nursingTask.setElderName(elder.getName());
        // 设置床位号
        nursingTask.setBedNumber(elder.getBedNumber());
        // 设置预估服务时间
        nursingTask.setEstimatedServerTime(seconds);
        // 设置项目ID
        nursingTask.setProjectId(Integer.valueOf(v.getProjectId()));
        // 设置老人ID
        nursingTask.setElderId(elder.getId());
        //匹配护理项目
        nursingTask.setProjectName(nursingProjectMap.get(nursingTask.getProjectId()));

        return nursingTask;
    }

}
