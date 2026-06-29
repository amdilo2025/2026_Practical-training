package com.diabetes.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("health_article")
public class HealthArticle {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("title")
    private String title;
    @TableField("summary")
    private String summary;
    @TableField("content")
    private String content;
    @TableField("cover")
    private String cover;
    @TableField("category")
    private String category;
    @TableField("author")
    private String author;
    @TableField("views")
    private Integer views;
    @TableField("status")
    private String status;
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
}
