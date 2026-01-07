package com.zg.darlingweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("goals")
public class Goal {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;      // 标题
    private Double currentVal; // 当前值
    private Double targetVal;  // 目标值
    private String unit;       // 单位
    // --- 新增这个字段 ---
    private String color;      // 进度条颜色
    // --- 新增字段 ---
    /**
     * 类型：
     * 1 = 数值累积型 (攒黄金)
     * 2 = 日期纪念日型 (在一起XX天)
     */
    private Integer type;

    /**
     * 起始日期，格式 "2025-09-25"
     * 只有 type=2 时才用这个
     */
    private String startDate;
}