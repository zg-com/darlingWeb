package com.zg.darlingweb.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zg.darlingweb.entity.PeriodRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface PeriodMapper extends BaseMapper<PeriodRecord> {
    // 按开始时间倒序查，方便算最近一次
    @Select("SELECT * FROM period_records ORDER BY start_date DESC")
    List<PeriodRecord> selectAllDesc();
}