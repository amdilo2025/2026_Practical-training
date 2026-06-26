package com.diabetes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diabetes.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
}
