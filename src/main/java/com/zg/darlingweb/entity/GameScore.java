package com.zg.darlingweb.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("game_scores")
public class GameScore {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String playerName;
    private Integer score;
    private LocalDateTime createTime;
}