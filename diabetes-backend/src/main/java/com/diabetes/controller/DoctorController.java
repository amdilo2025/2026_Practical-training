package com.diabetes.controller;

import com.diabetes.common.R;
import com.diabetes.entity.Doctor;
import com.diabetes.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/list")
    public R<List<Doctor>> list() {
        return R.success(doctorService.listAll());
    }

    @GetMapping("/get/{id}")
    public R<Doctor> getById(@PathVariable Integer id) {
        Doctor doctor = doctorService.getById(id);
        return doctor != null ? R.success(doctor) : R.error("医师不存在");
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody Doctor doctor) {
        doctorService.add(doctor);
        return R.success("添加成功");
    }

    @PutMapping("/update")
    public R<String> update(@RequestBody Doctor doctor) {
        doctorService.update(doctor);
        return R.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public R<String> delete(@PathVariable Integer id) {
        doctorService.delete(id);
        return R.success("删除成功");
    }
}
