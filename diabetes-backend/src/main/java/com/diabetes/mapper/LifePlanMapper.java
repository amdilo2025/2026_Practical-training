package com.diabetes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diabetes.entity.LifePlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LifePlanMapper extends BaseMapper<LifePlan> {

    @Select("SELECT * FROM life_plan WHERE user_id = #{userId} AND status = '有效' ORDER BY created_time DESC")
    List<LifePlan> findValidByUser(@Param("userId") Integer userId);

    @Select("SELECT * FROM life_plan WHERE user_id = #{userId} AND plan_type = #{planType} AND status = '有效' ORDER BY created_time DESC LIMIT 1")
    LifePlan findLatestByType(@Param("userId") Integer userId, @Param("planType") String planType);
}
