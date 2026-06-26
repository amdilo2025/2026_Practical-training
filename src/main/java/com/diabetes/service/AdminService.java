package com.diabetes.service;

import com.diabetes.entity.Admin;

public interface AdminService {
    Admin getById(Integer id);
    Admin login(String username, String password);
    Admin findByUsername(String username);
    int update(Admin admin);
}
