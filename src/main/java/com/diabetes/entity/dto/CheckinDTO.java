package com.diabetes.entity.dto;

public class CheckinDTO {
    private Integer userId;
    private String checkinDate;
    private String checkinType;
    private String content;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getCheckinDate() { return checkinDate; }
    public void setCheckinDate(String checkinDate) { this.checkinDate = checkinDate; }
    public String getCheckinType() { return checkinType; }
    public void setCheckinType(String checkinType) { this.checkinType = checkinType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
