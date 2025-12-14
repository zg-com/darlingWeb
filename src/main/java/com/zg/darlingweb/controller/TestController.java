package com.zg.darlingweb.controller;

import com.zg.darlingweb.entity.Coupon;
import com.zg.darlingweb.mapper.CouponMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 这是一个测试用的控制器
 */
@RestController // 1. 告诉 Spring Boot：我是个服务员，专门负责处理 HTTP 请求
public class TestController {
    @Autowired
    private CouponMapper couponMapper;

    @GetMapping("/hello") // 2. 告诉服务员：如果有人在浏览器访问 "/hello"，就执行下面这个方法
    public String sayHello() {
        // 3. 这里是具体的业务逻辑
        return "亲爱的，这是我们的第一个后端接口，成功连通啦！❤️";
    }

    @GetMapping("/coupons")
    public List<Coupon> getAllCoupons() {
        // 3. 直接调用 mapper 的 selectList 方法
        //    参数 null 表示没有任何查询条件（即：查询所有）
        //    它会自动执行 SQL：SELECT id, title, count, icon FROM coupons
        List<Coupon> list = couponMapper.selectList(null);

        // 4. Spring Boot 会自动把这个 List<Coupon> 对象转换成 JSON 格式返回给浏览器
        return list;
    }

    /**
     * 3. 新增一个接口：使用兑换券
     * 访问地址：POST http://localhost:8080/coupons/{id}/use
     * @PathVariable: 告诉 Spring Boot，把路径里 {id} 的位置的数字，赋值给方法参数 id
     */
    @org.springframework.web.bind.annotation.PostMapping("/coupons/{id}/use")
    public String useCoupon(@org.springframework.web.bind.annotation.PathVariable Long id) {
        // 第一步：先去数据库查一下，这张券还存不存在？
        Coupon coupon = couponMapper.selectById(id);

        if (coupon == null) {
            return "哎呀，这张券找不到啦！";
        }

        // 第二步：检查次数够不够
        if (coupon.getCount() <= 0) {
            return "次数用光啦！不能再用咯~";
        }

        // 第三步：扣减次数 (内存中修改)
        coupon.setCount(coupon.getCount() - 1);

        // 第四步：保存回数据库 (这一步最关键！)
        // updateById 会根据 id 自动生成 SQL: UPDATE coupons SET count = 4 WHERE id = 1
        couponMapper.updateById(coupon);

        return "使用成功！剩余次数：" + coupon.getCount();
    }
}