package com.diabetes.service;

import com.diabetes.entity.User;
import java.util.List;
import java.util.Map;

public interface UserService {
    User getById(Integer id);
    List<User> listAll();
    int add(User user);
    int update(User user);
    int delete(Integer id);
    User login(String username, String password);
    User register(User user);
    User findByUsername(String username);
    Integer countActiveUsers();
    List<Map<String, Object>> userTrend(String startDate);
}
