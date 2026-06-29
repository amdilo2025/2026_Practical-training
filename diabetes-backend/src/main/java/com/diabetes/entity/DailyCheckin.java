package com.diabetes.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("daily_checkin")
public class DailyCheckin {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("checkin_date")
    private LocalDate checkinDate;
    @TableField("checkin_type")
    private String checkinType;
    @TableField("status")
    private Integer status;
    @TableField("content")
    private String content;
    @TableField("remark")
    private String remark;
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public LocalDate getCheckinDate() { return checkinDate; }
    public void setCheckinDate(LocalDate checkinDate) { this.checkinDate = checkinDate; }
    public String getCheckinType() { return checkinType; }
    public void setCheckinType(String checkinType) { this.checkinType = checkinType; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
