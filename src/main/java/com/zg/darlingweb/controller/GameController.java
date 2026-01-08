package com.zg.darlingweb.controller;

import com.zg.darlingweb.entity.GameScore;
import com.zg.darlingweb.mapper.GameScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameScoreMapper gameScoreMapper;

    // 获取排行榜
    // 修改 rank 接口，接收 type 参数
    @GetMapping("/rank")
    public List<GameScore> getRank(@RequestParam(defaultValue = "1") Integer type) {
        return gameScoreMapper.selectTop10(type);
    }

// submit 接口不用改，因为前端传的 JSON 会自动映射到实体类的 gameType 字段

    // 上传分数
    @PostMapping("/submit")
    public String submitScore(@RequestBody GameScore score) {
        gameScoreMapper.insert(score);
        return "上传成功";
    }
}