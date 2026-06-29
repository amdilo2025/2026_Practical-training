package com.diabetes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diabetes.entity.DailyCheckin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DailyCheckinMapper extends BaseMapper<DailyCheckin> {

    @Select("SELECT checkin_date, checkin_type, status, content FROM daily_checkin " +
            "WHERE user_id = #{userId} AND checkin_date >= #{startDate} ORDER BY checkin_date DESC")
    List<Map<String, Object>> selectByUserAndDate(@Param("userId") Integer userId, @Param("startDate") String startDate);

    @Select("SELECT checkin_type, COUNT(*) as total_count, SUM(status) as completed_count " +
            "FROM daily_checkin WHERE user_id = #{userId} AND checkin_date >= #{startDate} " +
            "GROUP BY checkin_type")
    List<Map<String, Object>> selectCheckinStats(@Param("userId") Integer userId, @Param("startDate") String startDate);

    @Select("SELECT checkin_date, COUNT(*) as items, SUM(status) as completed " +
            "FROM daily_checkin WHERE user_id = #{userId} AND checkin_date >= #{startDate} " +
            "GROUP BY checkin_date ORDER BY checkin_date")
    List<Map<String, Object>> selectDailyTrend(@Param("userId") Integer userId, @Param("startDate") String startDate);

    @Select("SELECT COUNT(DISTINCT checkin_date) FROM daily_checkin " +
            "WHERE user_id = #{userId} AND status = 1 AND checkin_date >= #{startDate}")
    Integer countActiveDays(@Param("userId") Integer userId, @Param("startDate") String startDate);
}
