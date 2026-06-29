package com.diabetes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diabetes.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM `user` WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM `user` WHERE status = '正常'")
    Integer countActiveUsers();

    @Select("SELECT DATE(created_time) as date, COUNT(*) as count FROM `user` " +
            "WHERE created_time >= #{startDate} GROUP BY DATE(created_time) ORDER BY date")
    List<Map<String, Object>> countUserTrend(@Param("startDate") String startDate);
}
