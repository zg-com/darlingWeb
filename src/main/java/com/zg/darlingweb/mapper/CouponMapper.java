package com.zg.darlingweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zg.darlingweb.entity.Coupon;
import com.zg.darlingweb.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * 1. @Mapper 注解：
 * 告诉 Spring Boot，启动时要扫描这个接口，并自动生成它的实现类（代理对象）。
 *
 * 2. extends BaseMapper<Coupon>：
 * 这就是“芋道”框架的威力！
 * 只要继承了这个接口，你不需要写任何 SQL，
 * 你就自动拥有了 selectById, insert, delete, update 等几十个方法。
 * <Coupon> 泛型告诉它：我们要操作的是 Coupon 这个实体。
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
    // 里面目前什么都不用写，因为 BaseMapper 已经全帮你写好了！
}
