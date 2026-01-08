package com.zg.darlingweb.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zg.darlingweb.entity.GameScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface GameScoreMapper extends BaseMapper<GameScore> {
    // 查前10名
    @Select("SELECT * FROM game_scores ORDER BY score DESC LIMIT 10")
    List<GameScore> selectTop10();
}