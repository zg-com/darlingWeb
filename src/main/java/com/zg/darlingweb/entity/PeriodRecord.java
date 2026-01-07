package com.zg.darlingweb.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("period_records")
public class PeriodRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
}