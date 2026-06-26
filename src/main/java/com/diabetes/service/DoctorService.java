package com.diabetes.service;

import com.diabetes.entity.Doctor;
import java.util.List;

public interface DoctorService {
    List<Doctor> listAll();
    Doctor getById(Integer id);
    int add(Doctor doctor);
    int update(Doctor doctor);
    int delete(Integer id);
}
