package com.hospital.model;

/**
 * Status lifecycle of a patient token inside a doctor's daily queue.
 */
public enum QueueStatus {
    WAITING("Waiting in Queue", "status-waiting"),
    CALLED("Patient Called", "status-called"),
    IN_CONSULTATION("In Consultation", "status-in-consultation"),
    COMPLETED("Consultation Completed", "status-completed"),
    SKIPPED("Patient Skipped / Absent", "status-skipped"),
    CANCELLED("Token Cancelled", "status-cancelled");

    private final String description;
    private final String cssClass;

    QueueStatus(String description, String cssClass) {
        this.description = description;
        this.cssClass = cssClass;
    }

    public String getDescription() {
        return description;
    }

    public String getCssClass() {
        return cssClass;
    }

    public static QueueStatus fromString(String str) {
        if (str == null) return WAITING;
        for (QueueStatus s : QueueStatus.values()) {
            if (s.name().equalsIgnoreCase(str.trim())) {
                return s;
            }
        }
        return WAITING;
    }
}
