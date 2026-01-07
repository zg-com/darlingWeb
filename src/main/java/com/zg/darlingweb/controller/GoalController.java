package com.zg.darlingweb.controller;

import com.zg.darlingweb.entity.Goal;
import com.zg.darlingweb.mapper.GoalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goals") // 统一前缀，访问时用 /goals/xxx
public class GoalController {

    @Autowired
    private GoalMapper goalMapper;

    // 1. 获取所有目标
    @GetMapping("/list")
    public List<Goal> list() {
        return goalMapper.selectList(null);
    }

    // 2. 新增或修改目标
    // 前端如果传了 id 就是修改，没传 id 就是新增
    @PostMapping("/save")
    public String save(@RequestBody Goal goal) {
        if (goal.getId() != null) {
            goalMapper.updateById(goal);
            return "更新成功！";
        } else {
            goalMapper.insert(goal);
            return "添加成功！";
        }
    }

    // 3. 删除目标
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        goalMapper.deleteById(id);
        return "删除成功！";
    }
}