package com.zg.darlingweb.controller;

import com.zg.darlingweb.entity.PeriodRecord;
import com.zg.darlingweb.entity.PeriodSetting;
import com.zg.darlingweb.mapper.PeriodMapper;
import com.zg.darlingweb.mapper.PeriodSettingMapper;
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

    @GetMapping("/list")
    public List<PeriodRecord> list() {
        return periodMapper.selectAllDesc();
    }

    @GetMapping("/config")
    public PeriodSetting getConfig() {
        return settingMapper.selectById(1);
    }

    @PostMapping("/config/save")
    public String saveConfig(@RequestBody PeriodSetting setting) {
        setting.setId(1);
        settingMapper.updateById(setting);
        return "success";
    }

    // --- 新逻辑：设置经期开始 ---
    @PostMapping("/setStart")
    public String setStart(@RequestBody DateRequest req) {
        LocalDate start = req.getDate();

        // 1. 获取默认持续天数 (比如8天)
        PeriodSetting setting = settingMapper.selectById(1);
        int duration = (setting != null && setting.getPeriodLength() != null) ? setting.getPeriodLength() : 7;
        LocalDate end = start.plusDays(duration - 1); // 减1是因为包含当天

        // 2. 检查冲突 (简单处理：如果有重叠的，删掉旧的)
        // 实际项目可能需要更复杂的合并逻辑，这里为了演示稳定性，采用“覆盖”策略
        List<PeriodRecord> overlaps = periodMapper.selectAllDesc(); // 这里应该用SQL查重叠，为了省事在内存处理
        for (PeriodRecord r : overlaps) {
            // 如果新记录和旧记录有重叠
            if (!(end.isBefore(r.getStartDate()) || start.isAfter(r.getEndDate()))) {
                periodMapper.deleteById(r.getId());
            }
        }

        // 3. 创建新记录
        PeriodRecord newRecord = new PeriodRecord();
        newRecord.setStartDate(start);
        newRecord.setEndDate(end);
        periodMapper.insert(newRecord);

        return "Started";
    }

    // --- 新逻辑：设置经期结束 ---
    @PostMapping("/setEnd")
    public String setEnd(@RequestBody DateRequest req) {
        LocalDate targetDate = req.getDate();

        // 找到包含这一天的记录
        List<PeriodRecord> all = periodMapper.selectAllDesc();
        for (PeriodRecord r : all) {
            // 如果这一天在某个记录范围内
            if (!targetDate.isBefore(r.getStartDate()) && !targetDate.isAfter(r.getEndDate())) {
                // 修改结束日期为今天
                r.setEndDate(targetDate);
                periodMapper.updateById(r);
                return "Ended";
            }
        }

        // 如果没找到（比如用户手滑点错了），可以考虑创建一个单日记录，或者报错
        // 这里我们做一个容错：如果没找到，就创建一个单日记录
        PeriodRecord r = new PeriodRecord();
        r.setStartDate(targetDate);
        r.setEndDate(targetDate);
        periodMapper.insert(r);

        return "Ended (New)";
    }

    @lombok.Data
    static class DateRequest {
        private LocalDate date;
    }
}