package com.diabetes.controller;

import com.diabetes.common.R;
import com.diabetes.entity.Consultation;
import com.diabetes.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/consultation")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @GetMapping("/listByUser/{userId}")
    public R<?> listByUser(@PathVariable Integer userId) {
        return R.success(consultationService.listByUser(userId));
    }

    @GetMapping("/get/{id}")
    public R<Consultation> getById(@PathVariable Integer id) {
        return R.success(consultationService.getById(id));
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody Consultation consultation) {
        consultationService.add(consultation);
        return R.success("咨询已提交");
    }

    @PutMapping("/reply")
    public R<String> reply(@RequestBody Map<String, Object> params) {
        Integer id = Integer.parseInt(params.get("id").toString());
        String reply = params.get("reply").toString();
        consultationService.reply(id, reply);
        return R.success("回复成功");
    }

    @GetMapping("/listAll")
    public R<?> listAll() {
        return R.success(consultationService.listAll());
    }
}
