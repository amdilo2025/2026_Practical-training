package com.diabetes.entity.dto;

public class DifyChatDTO {
    private Integer userId;
    private String query;
    private String sessionId;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
