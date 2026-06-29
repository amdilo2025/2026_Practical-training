package com.diabetes.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.diabetes.entity.Admin;
import com.diabetes.mapper.AdminMapper;
import com.diabetes.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin getById(Integer id) {
        return adminMapper.selectById(id);
    }

    @Override
    public Admin login(String username, String password) {
        Admin admin = adminMapper.findByUsername(username);
        if (admin != null && admin.getPassword().equals(DigestUtil.md5Hex(password))) {
            return admin;
        }
        return null;
    }

    @Override
    public Admin findByUsername(String username) {
        return adminMapper.findByUsername(username);
    }

    @Override
    public int update(Admin admin) {
        return adminMapper.updateById(admin);
    }
}
