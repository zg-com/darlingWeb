package com.zg.darlingweb.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("period_settings")
public class PeriodSetting {
    private Integer id;
    private String periodColor;
    private String predictColor;
    private Integer periodLength;
    private Integer cycleLength;
}