package com.diabetes.controller;

import com.diabetes.common.R;
import com.diabetes.entity.HealthArticle;
import com.diabetes.service.HealthArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/article")
public class HealthArticleController {

    @Autowired
    private HealthArticleService healthArticleService;

    @GetMapping("/list")
    public R<List<HealthArticle>> list(@RequestParam(required = false) String status) {
        return R.success(healthArticleService.listAll(status));
    }

    @GetMapping("/listPublished")
    public R<List<HealthArticle>> listPublished() {
        return R.success(healthArticleService.listAll("已发布"));
    }

    @GetMapping("/get/{id}")
    public R<HealthArticle> getById(@PathVariable Integer id) {
        HealthArticle article = healthArticleService.getById(id);
        if (article != null) {
            healthArticleService.incrementViews(id);
        }
        return article != null ? R.success(article) : R.error("文章不存在");
    }

    @GetMapping("/category/{category}")
    public R<List<HealthArticle>> findByCategory(@PathVariable String category) {
        return R.success(healthArticleService.findByCategory(category));
    }

    @GetMapping("/countByCategory")
    public R<List<Map<String, Object>>> countByCategory() {
        return R.success(healthArticleService.countByCategory());
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody HealthArticle article) {
        healthArticleService.add(article);
        return R.success("发布成功");
    }

    @PutMapping("/update")
    public R<String> update(@RequestBody HealthArticle article) {
        healthArticleService.update(article);
        return R.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public R<String> delete(@PathVariable Integer id) {
        healthArticleService.delete(id);
        return R.success("删除成功");
    }
}
