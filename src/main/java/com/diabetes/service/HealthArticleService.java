package com.diabetes.service;

import com.diabetes.entity.HealthArticle;
import java.util.List;
import java.util.Map;

public interface HealthArticleService {
    List<HealthArticle> listAll(String status);
    HealthArticle getById(Integer id);
    int add(HealthArticle article);
    int update(HealthArticle article);
    int delete(Integer id);
    int incrementViews(Integer id);
    List<HealthArticle> findByCategory(String category);
    List<Map<String, Object>> countByCategory();
}
