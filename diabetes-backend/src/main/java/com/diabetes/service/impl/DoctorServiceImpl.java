package com.diabetes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diabetes.entity.Doctor;
import com.diabetes.mapper.DoctorMapper;
import com.diabetes.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public List<Doctor> listAll() {
        // AI 医生（赵晓峰）置顶，其余按 id 升序
        return doctorMapper.selectList(new QueryWrapper<Doctor>().orderByDesc("is_ai").orderByAsc("id"));
    }

    @Override
    public Doctor getById(Integer id) {
        return doctorMapper.selectById(id);
    }

    @Override
    public int add(Doctor doctor) {
        return doctorMapper.insert(doctor);
    }

    @Override
    public int update(Doctor doctor) {
        return doctorMapper.updateById(doctor);
    }

    @Override
    public int delete(Integer id) {
        return doctorMapper.deleteById(id);
    }
}
