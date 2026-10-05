package com.inteink.modules.biz.service.lifting;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inteink.common.utils.PageUtils;
import com.inteink.common.utils.Result;
import com.inteink.modules.biz.model.dto.StrategyAuditDTO;
import com.inteink.modules.biz.model.dto.StrategyQueryDTO;
import com.inteink.modules.biz.model.dto.StrategySaveDTO;
import com.inteink.modules.biz.model.dto.StrategyUpdateDTO;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyLog;
import com.inteink.modules.biz.model.vo.StrategyResponseVO;

import java.util.List;

/**
 * 升降策略业务接口（整合新DTO+保留旧方法注释，逐步过渡）
 * 核心设计：
 * 1. 新接口采用DTO入参+统一VO返回，符合企业级参数封装规范；
 * 2. 旧接口暂时注释（保留代码结构），待历史调用迁移后删除；
 * 3. 所有核心方法保留完整注释，提升代码可读性。
 */
public interface LiftingStrategyService extends IService<BizLiftingStrategy> {
    /**
     * 新增升降策略（含明细+升降杆绑定）
     * @param saveDTO 新增策略请求参数（封装策略名称/动作/时间明细/杆ID等）
     * @return 统一返回VO（包含策略主信息+明细+绑定杆ID）
     */
    StrategyResponseVO saveStrategyWithDetail(StrategySaveDTO saveDTO);

    /**
     * 修改升降策略（含明细+升降杆重新绑定）
     * @param updateDTO 修改策略请求参数（封装策略ID/待修改字段/新明细/新杆ID等）
     * @return 统一返回VO（包含修改后完整策略信息）
     */
    StrategyResponseVO updateStrategyWithDetail(StrategyUpdateDTO updateDTO);

    /**
     * 策略审核（通过/驳回，通过后生成成对定时任务）
     * @param auditDTO 审核策略请求参数（封装策略ID/审核状态/审核人ID等）
     * @return 统一返回VO（包含审核后策略状态+操作结果提示）
     */
//    StrategyResponseVO auditStrategy(StrategyAuditDTO auditDTO);
    Result<StrategyResponseVO> auditStrategy(StrategyAuditDTO auditDTO);
    /**
     * 分页查询策略列表
     * @param queryDTO 分页查询请求参数（封装分页参数/策略名称/动作/审核状态等查询条件）
     * @return 分页结果（框架通用PageUtils，包含总条数/分页数据）
     */
    PageUtils queryPage(StrategyQueryDTO queryDTO);

    /**
     * 查询策略详情（主表+明细+绑定的杆ID）
     * @param strategyId 策略ID（主键）
     * @return 统一返回VO（包含策略主信息+时间明细+绑定的升降杆ID列表）
     */
    StrategyResponseVO getStrategyDetailWithRods(Long strategyId);

    /**
     * 删除策略（逻辑删除+解绑杆+删除明细+清理定时任务）
     * @param strategyId 策略ID（主键）
     * @return 统一返回VO（包含删除结果提示+策略基础信息）
     */
    StrategyResponseVO removeStrategyWithDetail(Long strategyId);

    /**
     * 查询策略操作日志
     * @param strategyId 策略ID（主键）
     * @return 日志列表（直接返回实体列表，无需封装VO）
     */
    List<BizLiftingStrategyLog> getStrategyLog(Long strategyId);

}