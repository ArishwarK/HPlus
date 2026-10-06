package com.hospital.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * QueueEntry POJO representing an active token in a doctor's daily queue.
 */
public class QueueEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private int queueId;
    private int tokenNumber;
    private String tokenDisplay;
    private int doctorId;
    private int patientId;
    private Integer appointmentId;
    private Date queueDate;
    private PriorityLevel priorityLevel;
    private double calculatedPriorityScore;
    private QueueStatus status;
    private Timestamp arrivalTime;
    private Timestamp calledTime;
    private Timestamp consultationStartTime;
    private Timestamp consultationEndTime;
    private int skippedCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // View Joins & Dynamic Estimates
    private String patientName;
    private String patientUhid;
    private String patientPhone;
    private String doctorName;
    private String departmentName;
    private String roomNumber;
    private int patientsAhead;
    private int estimatedWaitMinutes;
    private String symptoms;

    public QueueEntry() {
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public String getTokenDisplay() {
        return tokenDisplay;
    }

    public void setTokenDisplay(String tokenDisplay) {
        this.tokenDisplay = tokenDisplay;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Date getQueueDate() {
        return queueDate;
    }

    public void setQueueDate(Date queueDate) {
        this.queueDate = queueDate;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public double getCalculatedPriorityScore() {
        return calculatedPriorityScore;
    }

    public void setCalculatedPriorityScore(double calculatedPriorityScore) {
        this.calculatedPriorityScore = calculatedPriorityScore;
    }

    public QueueStatus getStatus() {
        return status;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
    }

    public Timestamp getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Timestamp arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Timestamp getCalledTime() {
        return calledTime;
    }

    public void setCalledTime(Timestamp calledTime) {
        this.calledTime = calledTime;
    }

    public Timestamp getConsultationStartTime() {
        return consultationStartTime;
    }

    public void setConsultationStartTime(Timestamp consultationStartTime) {
        this.consultationStartTime = consultationStartTime;
    }

    public Timestamp getConsultationEndTime() {
        return consultationEndTime;
    }

    public void setConsultationEndTime(Timestamp consultationEndTime) {
        this.consultationEndTime = consultationEndTime;
    }

    public int getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(int skippedCount) {
        this.skippedCount = skippedCount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientUhid() {
        return patientUhid;
    }

    public void setPatientUhid(String patientUhid) {
        this.patientUhid = patientUhid;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getPatientsAhead() {
        return patientsAhead;
    }

    public void setPatientsAhead(int patientsAhead) {
        this.patientsAhead = patientsAhead;
    }

    public int getEstimatedWaitMinutes() {
        return estimatedWaitMinutes;
    }

    public void setEstimatedWaitMinutes(int estimatedWaitMinutes) {
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
}
