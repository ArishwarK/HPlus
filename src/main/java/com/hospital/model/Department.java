package com.hospital.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Department POJO representing a hospital medical department.
 */
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    private int departmentId;
    private String name;
    private String code;
    private String description;
    private String locationBuilding;
    private int locationFloor;
    private int defaultAvgConsultationTime; // in minutes
    private boolean active;
    private Timestamp createdAt;

    public Department() {
    }

    public Department(int departmentId, String name, String code, String description,
                      String locationBuilding, int locationFloor, int defaultAvgConsultationTime) {
        this.departmentId = departmentId;
        this.name = name;
        this.code = code;
        this.description = description;
        this.locationBuilding = locationBuilding;
        this.locationFloor = locationFloor;
        this.defaultAvgConsultationTime = defaultAvgConsultationTime;
        this.active = true;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationBuilding() {
        return locationBuilding;
    }

    public void setLocationBuilding(String locationBuilding) {
        this.locationBuilding = locationBuilding;
    }

    public int getLocationFloor() {
        return locationFloor;
    }

    public void setLocationFloor(int locationFloor) {
        this.locationFloor = locationFloor;
    }

    public int getDefaultAvgConsultationTime() {
        return defaultAvgConsultationTime;
    }

    public void setDefaultAvgConsultationTime(int defaultAvgConsultationTime) {
        this.defaultAvgConsultationTime = defaultAvgConsultationTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
