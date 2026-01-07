package com.zg.darlingweb.controller;

import com.fasterxml.jackson.annotation.JsonFormat; // 引入这个包
import com.zg.darlingweb.entity.PeriodRecord;
import com.zg.darlingweb.entity.PeriodSetting;
import com.zg.darlingweb.mapper.PeriodMapper;
import com.zg.darlingweb.mapper.PeriodSettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    // 设置【经期开始】
    @PostMapping("/setStart")
    public String setStart(@RequestBody DateRequest req) {
        LocalDate newStart = req.getDate();

        List<PeriodRecord> all = periodMapper.selectAllDesc();
        PeriodRecord target = null;

        // 寻找“附近”的记录 (允许误差范围：前后10天)
        for (PeriodRecord r : all) {
            long diff = ChronoUnit.DAYS.between(newStart, r.getStartDate());
            if (Math.abs(diff) <= 10) {
                target = r;
                break;
            }
            if (!newStart.isBefore(r.getStartDate()) && !newStart.isAfter(r.getEndDate())) {
                target = r;
                break;
            }
        }

        if (target != null) {
            // 修改开始时间
            target.setStartDate(newStart);
            // 逻辑修正：如果开始时间改到了结束时间之后，重置结束时间
            if (target.getEndDate().isBefore(newStart)) {
                int duration = getPeriodLength();
                target.setEndDate(newStart.plusDays(duration - 1));
            }
            periodMapper.updateById(target);
            return "Updated Start";
        } else {
            // 新建
            PeriodRecord newRecord = new PeriodRecord();
            newRecord.setStartDate(newStart);
            int duration = getPeriodLength();
            newRecord.setEndDate(newStart.plusDays(duration - 1));
            periodMapper.insert(newRecord);
            return "Created New";
        }
    }

    // 设置【经期结束】
    @PostMapping("/setEnd")
    public String setEnd(@RequestBody DateRequest req) {
        LocalDate newEnd = req.getDate();

        List<PeriodRecord> all = periodMapper.selectAllDesc();
        PeriodRecord target = null;

        // 往回找最近的一个“开始时间”
        for (PeriodRecord r : all) {
            // 记录的开始时间 必须 <= 选中的结束时间
            if (!r.getStartDate().isAfter(newEnd)) {
                // 且距离在 45 天内 (防止匹配到去年的记录)
                long diff = ChronoUnit.DAYS.between(r.getStartDate(), newEnd);
                if (diff >= 0 && diff < 45) {
                    target = r;
                    break;
                }
            }
        }

        if (target != null) {
            target.setEndDate(newEnd);
            periodMapper.updateById(target);
            return "Updated End";
        } else {
            // 容错：创建单日记录
            PeriodRecord r = new PeriodRecord();
            r.setStartDate(newEnd);
            r.setEndDate(newEnd);
            periodMapper.insert(r);
            return "Created Fallback";
        }
    }

    private int getPeriodLength() {
        PeriodSetting s = settingMapper.selectById(1);
        if (s != null && s.getPeriodLength() != null && s.getPeriodLength() > 0) {
            return s.getPeriodLength();
        }
        return 7;
    }

    // 👇 关键修复在这里：给这个接收参数的类也加上格式化注解
    @lombok.Data
    static class DateRequest {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;
    }
}