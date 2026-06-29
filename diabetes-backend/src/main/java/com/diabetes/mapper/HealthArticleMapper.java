package com.diabetes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diabetes.entity.HealthArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface HealthArticleMapper extends BaseMapper<HealthArticle> {

    @Select("SELECT category, COUNT(*) as count FROM health_article WHERE status = '已发布' GROUP BY category")
    List<Map<String, Object>> countByCategory();

    @Select("SELECT * FROM health_article WHERE status = '已发布' AND category = #{category} ORDER BY created_time DESC")
    List<HealthArticle> findByCategory(@Param("category") String category);
}
