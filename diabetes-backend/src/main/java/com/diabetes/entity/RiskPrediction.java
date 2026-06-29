package com.diabetes.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("risk_prediction")
public class RiskPrediction {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("age")
    private Integer age;
    @TableField("height")
    private Double height;
    @TableField("weight")
    private Double weight;
    @TableField("fasting_blood_sugar")
    private Double fastingBloodSugar;
    @TableField("postprandial_blood_sugar")
    private Double postprandialBloodSugar;
    @TableField("blood_pressure_systolic")
    private Integer bloodPressureSystolic;
    @TableField("blood_pressure_diastolic")
    private Integer bloodPressureDiastolic;
    @TableField("family_history")
    private Integer familyHistory;
    @TableField("bmi")
    private Double bmi;
    @TableField("risk_level")
    private String riskLevel;
    @TableField("risk_score")
    private Integer riskScore;
    @TableField("advice")
    private String advice;
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getFastingBloodSugar() { return fastingBloodSugar; }
    public void setFastingBloodSugar(Double fastingBloodSugar) { this.fastingBloodSugar = fastingBloodSugar; }
    public Double getPostprandialBloodSugar() { return postprandialBloodSugar; }
    public void setPostprandialBloodSugar(Double postprandialBloodSugar) { this.postprandialBloodSugar = postprandialBloodSugar; }
    public Integer getBloodPressureSystolic() { return bloodPressureSystolic; }
    public void setBloodPressureSystolic(Integer bloodPressureSystolic) { this.bloodPressureSystolic = bloodPressureSystolic; }
    public Integer getBloodPressureDiastolic() { return bloodPressureDiastolic; }
    public void setBloodPressureDiastolic(Integer bloodPressureDiastolic) { this.bloodPressureDiastolic = bloodPressureDiastolic; }
    public Integer getFamilyHistory() { return familyHistory; }
    public void setFamilyHistory(Integer familyHistory) { this.familyHistory = familyHistory; }
    public Double getBmi() { return bmi; }
    public void setBmi(Double bmi) { this.bmi = bmi; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
