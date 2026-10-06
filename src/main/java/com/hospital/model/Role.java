package com.hospital.model;

/**
 * System roles for Role-Based Access Control (RBAC).
 */
public enum Role {
    ADMIN,
    DOCTOR,
    RECEPTIONIST,
    PATIENT;

    public static Role fromString(String roleStr) {
        if (roleStr == null) return null;
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(roleStr.trim())) {
                return r;
            }
        }
        return null;
    }
}
