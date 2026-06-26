package com.diabetes.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("doctor")
public class Doctor {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("name")
    private String name;
    @TableField("department")
    private String department;
    @TableField("title")
    private String title;
    @TableField("avatar")
    private String avatar;
    @TableField("phone")
    private String phone;
    @TableField("introduction")
    private String introduction;
    @TableField("good_at")
    private String goodAt;
    @TableField("status")
    private String status;
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public String getGoodAt() { return goodAt; }
    public void setGoodAt(String goodAt) { this.goodAt = goodAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
