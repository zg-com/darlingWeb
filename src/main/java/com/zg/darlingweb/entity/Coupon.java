package com.zg.darlingweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 1. @Data 是 Lombok 的注解。
 * 它帮我们自动生成 get/set/toString 等方法，虽然你看不到，但它们真实存在。
 * 这样代码看起来就很清爽。
 */
@Data
/**
 * 2. @TableName("coupons") 是 MyBatis Plus 的注解。
 * 它告诉程序：这个 Java 类对应数据库里的 "coupons" 这张表。
 */
@TableName("coupons")
public class Coupon {

    /**
     * 3. @TableId 告诉程序：这个字段是主键。
     * type = IdType.AUTO 表示数据库里设置了自增，Java这边不用管，让数据库自己生成。
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    // 下面这些属性名，必须和数据库表的字段名一模一样！
    private String title; // 对应 title 字段
    private Integer count; // 对应 count 字段
    private String icon;   // 对应 icon 字段



}
