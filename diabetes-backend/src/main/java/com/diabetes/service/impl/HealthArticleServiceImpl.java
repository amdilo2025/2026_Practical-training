package com.diabetes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diabetes.entity.HealthArticle;
import com.diabetes.mapper.HealthArticleMapper;
import com.diabetes.service.HealthArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HealthArticleServiceImpl implements HealthArticleService {

    @Autowired
    private HealthArticleMapper healthArticleMapper;

    @Override
    public List<HealthArticle> listAll(String status) {
        LambdaQueryWrapper<HealthArticle> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(HealthArticle::getStatus, status);
        }
        wrapper.orderByDesc(HealthArticle::getCreatedTime);
        return healthArticleMapper.selectList(wrapper);
    }

    @Override
    public HealthArticle getById(Integer id) {
        return healthArticleMapper.selectById(id);
    }

    @Override
    public int add(HealthArticle article) {
        if (article.getViews() == null) article.setViews(0);
        return healthArticleMapper.insert(article);
    }

    @Override
    public int update(HealthArticle article) {
        return healthArticleMapper.updateById(article);
    }

    @Override
    public int delete(Integer id) {
        return healthArticleMapper.deleteById(id);
    }

    @Override
    public int incrementViews(Integer id) {
        HealthArticle article = healthArticleMapper.selectById(id);
        if (article != null) {
            article.setViews(article.getViews() == null ? 1 : article.getViews() + 1);
            return healthArticleMapper.updateById(article);
        }
        return 0;
    }

    @Override
    public List<HealthArticle> findByCategory(String category) {
        return healthArticleMapper.findByCategory(category);
    }

    @Override
    public List<Map<String, Object>> countByCategory() {
        return healthArticleMapper.countByCategory();
    }
}
