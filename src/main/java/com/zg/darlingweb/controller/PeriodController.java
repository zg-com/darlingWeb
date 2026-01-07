package com.zg.darlingweb.controller;

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

    /**
     * 设置【经期开始】
     * 逻辑：
     * 1. 如果这天附近(比如未来7天内)已经有一条记录的开始时间，说明用户想微调由于那条记录的开始时间 -> 更新它。
     * 2. 否则，视为一次全新的经期 -> 新建一条(长度读配置)。
     */
    @PostMapping("/setStart")
    public String setStart(@RequestBody DateRequest req) {
        LocalDate newStart = req.getDate();

        List<PeriodRecord> all = periodMapper.selectAllDesc();
        PeriodRecord target = null;

        // 寻找“附近”的记录 (允许误差范围：例如 这天之后的 10 天内如果有记录，说明可能是同一条)
        // 比如记录是 5号~10号，用户点了 3号说开始，应该把 5号改为 3号
        for (PeriodRecord r : all) {
            // 如果 记录开始时间 在 [用户点击时间, 用户点击时间+10天] 范围内
            long diff = ChronoUnit.DAYS.between(newStart, r.getStartDate());
            if (diff >= 0 && diff <= 10) {
                target = r;
                break;
            }
            // 或者用户点击的时间 就在这条记录中间，那也可以视为修改开始时间
            if (!newStart.isBefore(r.getStartDate()) && !newStart.isAfter(r.getEndDate())) {
                target = r;
                break;
            }
        }

        if (target != null) {
            // --- 修正模式 ---
            // 修改开始时间
            target.setStartDate(newStart);
            // 保护机制：如果修改后，开始时间跑到了结束时间后面（非法），则把结束时间自动往后推默认长度
            if (target.getEndDate().isBefore(newStart)) {
                int duration = getPeriodLength();
                target.setEndDate(newStart.plusDays(duration - 1));
            }
            periodMapper.updateById(target);
            return "Updated Start";
        } else {
            // --- 新建模式 ---
            PeriodRecord newRecord = new PeriodRecord();
            newRecord.setStartDate(newStart);
            int duration = getPeriodLength();
            newRecord.setEndDate(newStart.plusDays(duration - 1)); // 减1是因为包含当天
            periodMapper.insert(newRecord);
            return "Created New";
        }
    }

    /**
     * 设置【经期结束】
     * 逻辑：
     * 1. 往回找最近的一个“开始时间”。
     * 2. 只要这个开始时间在合理的范围内（比如45天内），就认为这一天是那次经期的结束。
     */
    @PostMapping("/setEnd")
    public String setEnd(@RequestBody DateRequest req) {
        LocalDate newEnd = req.getDate();

        List<PeriodRecord> all = periodMapper.selectAllDesc();
        PeriodRecord target = null;

        // 遍历找“最近的一个开始时间在今天之前的记录”
        for (PeriodRecord r : all) {
            // 记录的开始时间 必须 <= 选中的结束时间
            if (!r.getStartDate().isAfter(newEnd)) {
                // 且距离不能太离谱 (比如不能把去年的经期延长到今天)，限制在 45 天内
                long diff = ChronoUnit.DAYS.between(r.getStartDate(), newEnd);
                if (diff >= 0 && diff < 45) {
                    target = r;
                    break; // 找到了最近的一个
                }
            }
        }

        if (target != null) {
            target.setEndDate(newEnd);
            periodMapper.updateById(target);
            return "Updated End";
        } else {
            // 如果实在找不到匹配的开始时间（比如这是第一次用），就创建单日记录
            PeriodRecord r = new PeriodRecord();
            r.setStartDate(newEnd); // 既然找不到开始，就把今天同时也当做开始
            r.setEndDate(newEnd);
            periodMapper.insert(r);
            return "Created Single Day (Fallback)";
        }
    }

    // 辅助方法：读取默认经期长度，如果没配置默认7天
    private int getPeriodLength() {
        PeriodSetting s = settingMapper.selectById(1);
        if (s != null && s.getPeriodLength() != null && s.getPeriodLength() > 0) {
            return s.getPeriodLength();
        }
        return 7; // 默认值改为用户期望的近似值，当然最好去后台配置
    }

    @lombok.Data
    static class DateRequest {
        private LocalDate date;
    }
}