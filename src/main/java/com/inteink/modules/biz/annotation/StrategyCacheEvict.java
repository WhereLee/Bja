package com.inteink.modules.biz.annotation;

import java.lang.annotation.*;

/**
 * 缓存刷新注解（预留）
 * 标注在【新增/修改/删除/暂停/恢复】方法上，后续Redis接入后，AOP自动清理缓存
 * 解决缓存一致性问题，业务类无需手动调用缓存工具
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrategyCacheEvict {
}