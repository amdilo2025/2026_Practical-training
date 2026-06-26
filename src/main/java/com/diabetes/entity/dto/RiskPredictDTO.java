package com.diabetes.entity.dto;

public class RiskPredictDTO {
    private Integer userId;
    private Integer age;
    private Double height;
    private Double weight;
    private Double fastingBloodSugar;
    private Double postprandialBloodSugar;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Integer familyHistory;

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
}
