package com.diabetes.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diabetes.entity.User;
import com.diabetes.mapper.UserMapper;
import com.diabetes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> listAll() {
        return userMapper.selectList(null);
    }

    @Override
    public int add(User user) {
        user.setPassword(DigestUtil.md5Hex(user.getPassword()));
        return userMapper.insert(user);
    }

    @Override
    public int update(User user) {
        return userMapper.updateById(user);
    }

    @Override
    public int delete(Integer id) {
        return userMapper.deleteById(id);
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && user.getPassword().equals(DigestUtil.md5Hex(password))) {
            return user;
        }
        return null;
    }

    @Override
    public User register(User user) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return null;
        }
        user.setPassword(DigestUtil.md5Hex(user.getPassword()));
        user.setStatus("正常");
        userMapper.insert(user);
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public Integer countActiveUsers() {
        return userMapper.countActiveUsers();
    }

    @Override
    public List<Map<String, Object>> userTrend(String startDate) {
        return userMapper.countUserTrend(startDate);
    }
}
