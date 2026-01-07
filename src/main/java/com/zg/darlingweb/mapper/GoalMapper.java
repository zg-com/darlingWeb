package com.zg.darlingweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zg.darlingweb.entity.Goal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoalMapper extends BaseMapper<Goal> {
    // MyBatis Plus 已经帮你写好了增删改查，这里留空即可
}