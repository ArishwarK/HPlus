package com.hospital.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Patient POJO representing patient demographic records.
 */
public class Patient implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Gender {
        MALE, FEMALE, OTHER;

        public static Gender fromString(String str) {
            if (str == null) return OTHER;
            for (Gender g : Gender.values()) {
                if (g.name().equalsIgnoreCase(str.trim())) return g;
            }
            return OTHER;
        }
    }

    private int patientId;
    private Integer userId; // Optional (Null for desk walk-in patients)
    private String uhid;    // Unique Hospital Identification Number (e.g. UHID-2026-0041)
    private String fullName;
    private Gender gender;
    private Date dateOfBirth;
    private String phoneNumber;
    private String email;
    private String bloodGroup;
    private String address;
    private String emergencyContact;
    private boolean active;
    private Timestamp createdAt;

    public Patient() {
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUhid() {
        return uhid;
    }

    public void setUhid(String uhid) {
        this.uhid = uhid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
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
