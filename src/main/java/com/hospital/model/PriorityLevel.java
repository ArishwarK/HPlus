package com.hospital.model;

/**
 * Queue priority levels with baseline score weights.
 * Higher priority score guarantees faster evaluation, but waiting-time fairness
 * boosts normal patients after prolonged waiting to eliminate starvation.
 */
public enum PriorityLevel {
    NORMAL(100.0, "Normal Priority", "badge-normal"),
    HIGH(180.0, "High Priority", "badge-high"),
    EMERGENCY(300.0, "Emergency Priority", "badge-emergency");

    private final double baseScore;
    private final String displayName;
    private final String badgeClass;

    PriorityLevel(double baseScore, String displayName, String badgeClass) {
        this.baseScore = baseScore;
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public double getBaseScore() {
        return baseScore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public static PriorityLevel fromString(String str) {
        if (str == null) return NORMAL;
        for (PriorityLevel p : PriorityLevel.values()) {
            if (p.name().equalsIgnoreCase(str.trim())) {
                return p;
            }
        }
        return NORMAL;
    }
}
