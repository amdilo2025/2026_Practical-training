package com.diabetes.controller;

import com.diabetes.common.R;
import com.diabetes.service.DailyCheckinService;
import com.diabetes.entity.dto.CheckinDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
public class DailyCheckinController {

    @Autowired
    private DailyCheckinService dailyCheckinService;

    @GetMapping("/list/{userId}")
    public R<?> list(@PathVariable Integer userId,
                     @RequestParam(defaultValue = "30") int days) {
        String startDate = LocalDate.now().minusDays(days).toString();
        return R.success(dailyCheckinService.listByUserAndDate(userId, startDate));
    }

    @PostMapping("/do")
    public R<String> doCheckin(@RequestBody CheckinDTO dto) {
        boolean result = dailyCheckinService.checkin(
                dto.getUserId(),
                LocalDate.parse(dto.getCheckinDate()),
                dto.getCheckinType(),
                dto.getContent()
        );
        return result ? R.success("打卡成功") : R.error("今日已打卡");
    }

    @GetMapping("/stats/{userId}")
    public R<Map<String, Object>> stats(@PathVariable Integer userId,
                                        @RequestParam(defaultValue = "30") int days) {
        String startDate = LocalDate.now().minusDays(days).toString();
        return R.success(dailyCheckinService.stats(userId, startDate));
    }

    @GetMapping("/trend/{userId}")
    public R<?> trend(@PathVariable Integer userId,
                      @RequestParam(defaultValue = "30") int days) {
        String startDate = LocalDate.now().minusDays(days).toString();
        return R.success(dailyCheckinService.dailyTrend(userId, startDate));
    }
}
