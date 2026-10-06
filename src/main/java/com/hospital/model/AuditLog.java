package com.hospital.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Audit log POJO for tracking security and queue state changes.
 */
public class AuditLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private int logId;
    private Integer userId;
    private String action;
    private String details;
    private String ipAddress;
    private Timestamp timestamp;

    private String username;

    public AuditLog() {
    }

    public AuditLog(Integer userId, String action, String details, String ipAddress) {
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.ipAddress = ipAddress;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
