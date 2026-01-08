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
    @GetMapping("/rank")
    public List<GameScore> getRank() {
        return gameScoreMapper.selectTop10();
    }

    // 上传分数
    @PostMapping("/submit")
    public String submitScore(@RequestBody GameScore score) {
        gameScoreMapper.insert(score);
        return "上传成功";
    }
}