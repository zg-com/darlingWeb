package com.zg.darlingweb.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("period_settings")
public class PeriodSetting {
    private Integer id;
    private String periodColor;
    private String predictColor;
    private Integer periodLength; // 默认持续时长
    private Integer intervalDays; // 间隔天数 (从结束推算开始)
}