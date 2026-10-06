package com.hospital.model;

import java.io.Serializable;

/**
 * Breakdown of the waiting-time estimation algorithm.
 */
public class WaitingTimeEstimate implements Serializable {
    private static final long serialVersionUID = 1L;

    private int tokenNumber;
    private int queuePosition;
    private int patientsAhead;
    private int doctorAvgConsultationMinutes;
    private int currentConsultationElapsedMinutes;
    private int estimatedRemainingCurrentPatient;
    private double priorityFactor;
    private int totalEstimatedMinutes;
    private String estimatedConsultationTimeFormatted;

    public WaitingTimeEstimate() {
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(int queuePosition) {
        this.queuePosition = queuePosition;
    }

    public int getPatientsAhead() {
        return patientsAhead;
    }

    public void setPatientsAhead(int patientsAhead) {
        this.patientsAhead = patientsAhead;
    }

    public int getDoctorAvgConsultationMinutes() {
        return doctorAvgConsultationMinutes;
    }

    public void setDoctorAvgConsultationMinutes(int doctorAvgConsultationMinutes) {
        this.doctorAvgConsultationMinutes = doctorAvgConsultationMinutes;
    }

    public int getCurrentConsultationElapsedMinutes() {
        return currentConsultationElapsedMinutes;
    }

    public void setCurrentConsultationElapsedMinutes(int currentConsultationElapsedMinutes) {
        this.currentConsultationElapsedMinutes = currentConsultationElapsedMinutes;
    }

    public int getEstimatedRemainingCurrentPatient() {
        return estimatedRemainingCurrentPatient;
    }

    public void setEstimatedRemainingCurrentPatient(int estimatedRemainingCurrentPatient) {
        this.estimatedRemainingCurrentPatient = estimatedRemainingCurrentPatient;
    }

    public double getPriorityFactor() {
        return priorityFactor;
    }

    public void setPriorityFactor(double priorityFactor) {
        this.priorityFactor = priorityFactor;
    }

    public int getTotalEstimatedMinutes() {
        return totalEstimatedMinutes;
    }

    public void setTotalEstimatedMinutes(int totalEstimatedMinutes) {
        this.totalEstimatedMinutes = totalEstimatedMinutes;
    }

    public String getEstimatedConsultationTimeFormatted() {
        return estimatedConsultationTimeFormatted;
    }

    public void setEstimatedConsultationTimeFormatted(String estimatedConsultationTimeFormatted) {
        this.estimatedConsultationTimeFormatted = estimatedConsultationTimeFormatted;
    }
}
