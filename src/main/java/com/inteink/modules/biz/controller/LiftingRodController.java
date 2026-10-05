package com.inteink.modules.biz.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.inteink.common.utils.Result;
import com.inteink.modules.biz.model.entity.BizLiftingRod;
import com.inteink.modules.biz.model.form.LiftingRodForm;
import com.inteink.modules.biz.service.lifting.LiftingRodLogService;
import com.inteink.modules.biz.service.lifting.LiftingRodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.inteink.common.utils.PageUtils;
import com.inteink.modules.sys.controller.AbstractController;

import java.math.BigDecimal;

@Api(tags = "升降杆管理", position = 270) // 标签+排序（跟随公司项目的position编号）
@RestController
@RequestMapping("biz/liftingRod")
public class LiftingRodController extends AbstractController {

    @Autowired
    private LiftingRodService liftingRodService;
    @Autowired
    private LiftingRodLogService liftingRodLogService;
    /**
     * 新增升降杆（表单参数传参，新增后返回整条数据）
     */
    @ApiOperation("新增升降杆")
    @ApiOperationSupport(order = 10)
    @PostMapping("/save")
    @RequiresPermissions("biz:liftingRod:save")
    public Result save( // 无需指定泛型，Result本身支持data承载任意对象
                        // 核心必填参数
                        @RequestParam(required = true) String rodName,        // 升降杆名称（必填）
                        @RequestParam(required = true) String rodAddr,        // 安装位置（必填）
                        // 非必填参数
                        @RequestParam(required = false) BigDecimal rodLongtitude, // 经度
                        @RequestParam(required = false) Boolean rodOffline,   // 是否在线（true=在线）
                        @RequestParam(required = false) BigDecimal rodLatitude,  // 纬度
                        @RequestParam(required = false) String rodRemark,     // 备注
                        @RequestParam(required = false) Integer rodState      // 升降状态（0=不显示/1=升/2=降）
    ) {
        try {
            // 1. 构造实体类（rodId自增不赋值）
            BizLiftingRod rod = new BizLiftingRod();
            rod.setRodName(rodName);
            rod.setRodAddr(rodAddr);
            rod.setRodLongtitude(rodLongtitude);
            rod.setRodOffline(rodOffline == null ? true : rodOffline); // 默认在线
            rod.setRodLatitude(rodLatitude);
            rod.setRodRemark(rodRemark);
            rod.setRodState(rodState == null ? 0 : rodState); // 默认不显示

            // 2. 后端补充固定值
            rod.setRodCreator(getUserId()); // 当前登录人ID
            rod.setRodCreatetime(System.currentTimeMillis() / 1000); // 创建时间戳
            rod.setRodUpdatetime(System.currentTimeMillis() / 1000); // 更新时间戳
            rod.setRodStatus(0L); // 默认有效

            // 3. 执行新增
            boolean saveResult = liftingRodService.save(rod);
            if (!saveResult) {
                // 失败时用error方法（保持你原有error方法的调用方式）
                return Result.error(500, "新增失败：数据库未执行插入操作", null);
            }

            // 4. 新增成功 → 调用你的ok(data, msg)方法，返回整条数据（核心优化）
            return Result.ok(rod, "新增成功，升降杆ID：" + rod.getRodId());
        } catch (Exception e) {
            // 捕获异常，返回错误信息
            return Result.error(500, "新增失败：" + e.getMessage(), null);
        }
    }

    /**
     * 修改升降杆（表单参数传参，贴合你的Result工具类）
     */
    @ApiOperation("修改升降杆")
    @ApiOperationSupport(order = 20)
    @PostMapping("/update")
    @RequiresPermissions("biz:liftingRod:update")
    public Result update(
            @RequestParam(required = true) Long rodId,           // 升降杆ID（必传）
            // 可选修改参数
            @RequestParam(required = false) String rodName,      // 升降杆名称
            @RequestParam(required = false) String rodAddr,      // 安装位置
            @RequestParam(required = false) BigDecimal rodLongtitude, // 经度
            @RequestParam(required = false) Boolean rodOffline,  // 是否在线
            @RequestParam(required = false) BigDecimal rodLatitude,  // 纬度
            @RequestParam(required = false) String rodRemark,    // 备注
            @RequestParam(required = false) Integer rodState     // 升降状态
    ) {
        try {
            // 1. 校验升降杆是否存在
            BizLiftingRod existRod = liftingRodService.getById(rodId);
            if (existRod == null) {
                return Result.error(404, "修改失败：升降杆ID=" + rodId + "不存在", null);
            }

            // 2. 构造修改对象
            BizLiftingRod updateRod = new BizLiftingRod();
            updateRod.setRodId(rodId);
            updateRod.setRodUpdatetime(System.currentTimeMillis() / 1000);

            // 按需赋值
            if (rodName != null && !rodName.isEmpty()) {
                updateRod.setRodName(rodName);
            }
            if (rodAddr != null && !rodAddr.isEmpty()) {
                updateRod.setRodAddr(rodAddr);
            }
            if (rodLongtitude != null) {
                updateRod.setRodLongtitude(rodLongtitude);
            }
            if (rodOffline != null) {
                updateRod.setRodOffline(rodOffline);
            }
            if (rodLatitude != null) {
                updateRod.setRodLatitude(rodLatitude);
            }
            if (rodRemark != null) {
                updateRod.setRodRemark(rodRemark);
            }
            if (rodState != null) {
                updateRod.setRodState(rodState);
            }

            // 3. 执行修改
            boolean updateResult = liftingRodService.updateById(updateRod);
            if (!updateResult) {
                return Result.error(500, "修改失败：数据库未执行更新操作", null);
            }

            // 4. 修改成功 → 返回修改后的完整数据（可选，和新增保持一致体验）
            BizLiftingRod updatedRod = liftingRodService.getById(rodId);
            return Result.ok(updatedRod, "修改成功");
        } catch (Exception e) {
            return Result.error(500, "修改失败：" + e.getMessage(), null);
        }
    }

    // 3. 删除升降杆（逻辑删除：修改rod_status为1）
    @ApiOperation("删除升降杆")
    @ApiOperationSupport(order = 30)
    @PostMapping("/delete")
//    @RequiresPermissions("biz:liftingRod:delete")
    public Result<String> delete(@RequestParam Long rodId) {
        BizLiftingRod rod = new BizLiftingRod();
        rod.setRodId(rodId);
        rod.setRodStatus(1L); // 逻辑删除：1-无效
        rod.setRodUpdatetime(System.currentTimeMillis() / 1000);
        liftingRodService.updateById(rod);
        return Result.ok("删除成功");
    }

    // 4. 升降杆详情（调用带data的error方法，避免data=null）
    @ApiOperation("升降杆详情")
    @ApiOperationSupport(order = 40)
    @GetMapping("/info/{rodId}")
    @RequiresPermissions("biz:liftingRod:info")
    public Result<BizLiftingRod> info(@PathVariable Long rodId) {
        try {
            BizLiftingRod rod = liftingRodService.getById(rodId);
            if (rod == null) {
                // 场景1：杆不存在 → code建议用非0（比如500/404），data传null（合理）
                return Result.error(404, "升降杆不存在", null);
            }
            // 场景2：杆已逻辑删除 → code用自定义错误码（比如501），data传完整杆数据（避免null）
            if (rod.getRodStatus() != 0L) {
                // 给备注追加“已删除”标识，前端能直观看到
                rod.setRodRemark("[已删除] " + (rod.getRodRemark() == null ? "" : rod.getRodRemark()));
                // 调用带data的error方法：code（自定义）+ 提示语 + 杆数据（避免data=null）
                return Result.error(501, "该升降杆已被删除（无效状态）", rod);
            }
            // 场景3：杆有效 → 正常返回ok（保持原有逻辑）
            return Result.ok(rod); // 假设你的ok方法也带data，格式和error统一
        } catch (Exception e) {
            // 场景4：查询异常 → code用500，data传null
            return Result.error(500, "详情查询失败：" + e.getMessage(), null);
        }
    }

    // 5. 升降杆分页查询
    @ApiOperation("升降杆列表")
    @ApiOperationSupport(
            order = 50,
            ignoreParameters = {"sqlFilter", "userId"}
    )
    @PostMapping("/list")
//    @RequiresPermissions("biz:liftingRod:list")
    public Result<PageUtils> list(LiftingRodForm form) {
        try {
            PageUtils page = liftingRodService.queryRodPage(form);
            return Result.ok(page);
        } catch (Exception e) {
            return Result.error("列表查询失败：" + e.getMessage());
        }
    }


    /**
     * 升降杆操作（升/降）
     */
    @ApiOperation(value = "升降杆操作", notes = "执行升降杆升/降动作；权限说明：biz:liftingRod:operate")
    @ApiOperationSupport(order = 10, ignoreParameters = {"userId", "createTime"}) // 排序+忽略无需前端传的参数
    @PostMapping("/operate")
//    @RequiresPermissions("biz:liftingRod:operate") // 权限注解（对齐公司项目的权限规范）
    public Result<String> operate(
            @ApiParam(value = "升降杆ID", required = true, example = "1") @RequestParam Long rodId,
            @ApiParam(value = "操作动作：1-升，2-降", required = true, example = "1") @RequestParam Integer action
    ) {
        try {
            // 从AbstractController获取当前操作人ID（对齐公司项目的用户上下文）
            Long operatorId = getUserId();
            // 调用Service执行升降操作
            liftingRodService.liftRod(rodId, action, operatorId);
            // 构建返回结果（对齐公司Result工具类）
            String actionDesc = action == 1 ? "升" : "降";
            return Result.ok("升降杆[" + rodId + "]" + actionDesc + "操作成功");
        } catch (Exception e) {
            // 异常返回（对齐公司异常处理风格）
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 升降杆操作日志列表查询
     */
    @ApiOperation(value = "操作日志列表", notes = "查询升降杆操作日志；权限说明：biz:liftingRod:logList")
    @ApiOperationSupport(order = 20, ignoreParameters = {"sqlFilter", "operatorId"})
    @PostMapping("/logList")
//    @RequiresPermissions("biz:liftingRod:logList")
    public Result<PageUtils> logList(LiftingRodForm form) {
        try {
            PageUtils page = liftingRodLogService.queryLogPage(form, getUserId());
            return Result.ok(page);
        } catch (Exception e) {
            return Result.error("日志查询失败：" + e.getMessage());
        }
    }
}