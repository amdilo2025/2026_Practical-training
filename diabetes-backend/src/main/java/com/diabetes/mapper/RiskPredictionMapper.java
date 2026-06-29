package com.diabetes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diabetes.entity.RiskPrediction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RiskPredictionMapper extends BaseMapper<RiskPrediction> {

    @Select("SELECT * FROM risk_prediction WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<RiskPrediction> findByUserId(@Param("userId") Integer userId);

    @Select("SELECT DATE(created_time) as date, AVG(risk_score) as avg_score " +
            "FROM risk_prediction WHERE user_id = #{userId} GROUP BY DATE(created_time) ORDER BY date")
    List<Map<String, Object>> selectRiskTrend(@Param("userId") Integer userId);
}
