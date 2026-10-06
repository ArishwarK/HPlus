package com.hospital.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Doctor POJO representing medical practitioners in the hospital.
 */
public class Doctor implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        AVAILABLE, BUSY, ON_BREAK, OFF_DUTY;

        public static Status fromString(String s) {
            if (s == null) return AVAILABLE;
            for (Status val : Status.values()) {
                if (val.name().equalsIgnoreCase(s.trim())) return val;
            }
            return AVAILABLE;
        }
    }

    private int doctorId;
    private int userId;
    private int departmentId;
    private String departmentName;
    private String departmentCode;
    private String doctorName;
    private String email;
    private String phoneNumber;
    private String specialization;
    private String roomNumber;
    private String qualification;
    private int experienceYears;
    private BigDecimal consultationFee;
    private int avgConsultationMinutes;
    private Status status;
    private boolean active;
    private Timestamp createdAt;

    // Runtime queue metrics
    private int activeQueueCount;
    private int completedTodayCount;
    private Integer currentServingToken;

    public Doctor() {
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public int getAvgConsultationMinutes() {
        return avgConsultationMinutes;
    }

    public void setAvgConsultationMinutes(int avgConsultationMinutes) {
        this.avgConsultationMinutes = avgConsultationMinutes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public int getActiveQueueCount() {
        return activeQueueCount;
    }

    public void setActiveQueueCount(int activeQueueCount) {
        this.activeQueueCount = activeQueueCount;
    }

    public int getCompletedTodayCount() {
        return completedTodayCount;
    }

    public void setCompletedTodayCount(int completedTodayCount) {
        this.completedTodayCount = completedTodayCount;
    }

    public Integer getCurrentServingToken() {
        return currentServingToken;
    }

    public void setCurrentServingToken(Integer currentServingToken) {
        this.currentServingToken = currentServingToken;
    }
}
