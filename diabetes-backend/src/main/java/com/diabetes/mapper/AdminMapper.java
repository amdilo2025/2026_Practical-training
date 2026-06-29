package com.diabetes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diabetes.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin findByUsername(@Param("username") String username);
}
