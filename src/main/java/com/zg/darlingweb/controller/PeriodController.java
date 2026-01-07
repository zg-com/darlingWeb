package com.zg.darlingweb.controller;

import com.zg.darlingweb.entity.PeriodRecord;
import com.zg.darlingweb.entity.PeriodSetting;
import com.zg.darlingweb.mapper.PeriodMapper;
import com.zg.darlingweb.mapper.PeriodSettingMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/period")
public class PeriodController {

    @Autowired
    private PeriodMapper periodMapper;
    @Autowired
    private PeriodSettingMapper settingMapper;

    // 1. 获取所有记录
    @GetMapping("/list")
    public List<PeriodRecord> list() {
        return periodMapper.selectAllDesc();
    }

    // 2. 获取配置
    @GetMapping("/config")
    public PeriodSetting getConfig() {
        return settingMapper.selectById(1);
    }

    // 3. 更新配置
    @PostMapping("/config/save")
    public String saveConfig(@RequestBody PeriodSetting setting) {
        setting.setId(1);
        settingMapper.updateById(setting);
        return "配置已更新";
    }

    // 4. 核心：切换某一天是否为经期
    // 前端传来的 date: yyyy-MM-dd, isPeriod: true/false
    @PostMapping("/toggle")
    public String toggleDate(@RequestBody ToggleRequest req) {
        LocalDate targetDate = req.getDate();
        boolean isPeriod = req.getIsPeriod();

        // 查出这一天是否已经在某个记录里
        List<PeriodRecord> records = periodMapper.selectAllDesc();
        PeriodRecord hitRecord = null;

        for (PeriodRecord r : records) {
            if (!targetDate.isBefore(r.getStartDate()) && !targetDate.isAfter(r.getEndDate())) {
                hitRecord = r;
                break;
            }
        }

        if (isPeriod) {
            // 用户说今天是！
            if (hitRecord != null) return "Success"; // 已经在记录里了，不用动

            // 检查是否能合并到前一段或后一段
            // 这里为了简化逻辑，如果当天不是经期，我们直接创建一条单日记录
            // 或者：检查昨天是不是经期？是的话延长昨天。检查明天是不是？是的话合并。
            // 简单处理：直接新建，前端会看到，如果用户连续点，我们可以在后端做一个合并逻辑，但比较复杂。
            // 我们采用最稳健的方式：新建一条。
            // 进阶优化：检查有没有结束日期是昨天的？
            boolean merged = false;
            for (PeriodRecord r : records) {
                if (r.getEndDate().plusDays(1).equals(targetDate)) {
                    r.setEndDate(targetDate);
                    periodMapper.updateById(r);
                    merged = true;
                    break;
                }
                if (r.getStartDate().minusDays(1).equals(targetDate)) {
                    r.setStartDate(targetDate);
                    periodMapper.updateById(r);
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                PeriodRecord newRecord = new PeriodRecord();
                newRecord.setStartDate(targetDate);
                newRecord.setEndDate(targetDate);
                periodMapper.insert(newRecord);
            }
        } else {
            // 用户说今天不是！(但数据库里有) -> 需要切断
            if (hitRecord != null) {
                if (hitRecord.getStartDate().equals(targetDate) && hitRecord.getEndDate().equals(targetDate)) {
                    // 就是只有这一天，删掉
                    periodMapper.deleteById(hitRecord.getId());
                } else if (hitRecord.getStartDate().equals(targetDate)) {
                    // 是第一天，开始日期+1
                    hitRecord.setStartDate(targetDate.plusDays(1));
                    periodMapper.updateById(hitRecord);
                } else if (hitRecord.getEndDate().equals(targetDate)) {
                    // 是最后一天，结束日期-1
                    hitRecord.setEndDate(targetDate.minusDays(1));
                    periodMapper.updateById(hitRecord);
                } else {
                    // 在中间！裂开成两段
                    PeriodRecord newTail = new PeriodRecord();
                    newTail.setStartDate(targetDate.plusDays(1));
                    newTail.setEndDate(hitRecord.getEndDate());
                    periodMapper.insert(newTail);

                    hitRecord.setEndDate(targetDate.minusDays(1));
                    periodMapper.updateById(hitRecord);
                }
            }
        }
        return "Updated";
    }

    @Data
    static class ToggleRequest {
        private LocalDate date;
        private Boolean isPeriod;
    }
}