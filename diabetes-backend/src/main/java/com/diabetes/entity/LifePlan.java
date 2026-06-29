package com.diabetes.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("life_plan")
public class LifePlan {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("plan_type")
    private String planType;
    @TableField("title")
    private String title;
    @TableField("content")
    private String content;
    @TableField("status")
    private String status;
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
