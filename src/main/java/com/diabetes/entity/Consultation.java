package com.diabetes.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("consultation")
public class Consultation {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("doctor_id")
    private Integer doctorId;
    @TableField("content")
    private String content;
    @TableField("reply")
    private String reply;
    @TableField("status")
    private String status;
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    @TableField("reply_time")
    private LocalDateTime replyTime;
    @TableField(exist = false)
    private String doctorName;
    @TableField(exist = false)
    private String doctorTitle;
    @TableField(exist = false)
    private String doctorDepartment;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getReplyTime() { return replyTime; }
    public void setReplyTime(LocalDateTime replyTime) { this.replyTime = replyTime; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorTitle() { return doctorTitle; }
    public void setDoctorTitle(String doctorTitle) { this.doctorTitle = doctorTitle; }
    public String getDoctorDepartment() { return doctorDepartment; }
    public void setDoctorDepartment(String doctorDepartment) { this.doctorDepartment = doctorDepartment; }
}
