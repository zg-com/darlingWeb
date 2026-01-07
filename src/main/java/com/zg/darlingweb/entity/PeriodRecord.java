package com.zg.darlingweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat; // 👈 记得引入这个包
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("period_records")
public class PeriodRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    // 强制转换为 "yyyy-MM-dd" 字符串格式
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}