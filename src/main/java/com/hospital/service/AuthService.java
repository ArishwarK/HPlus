package com.hospital.service;

import com.hospital.dao.PatientDAO;
import com.hospital.dao.UserDAO;
import com.hospital.exception.ServiceException;
import com.hospital.exception.ValidationException;
import com.hospital.model.Patient;
import com.hospital.model.Role;
import com.hospital.model.User;
import com.hospital.util.PasswordUtil;
import com.hospital.util.ValidationUtil;

import java.sql.Date;

/**
 * Authentication and User Management Service.
 */
public class AuthService {

    private final UserDAO userDAO;
    private final PatientDAO patientDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.patientDAO = new PatientDAO();
    }

    public AuthService(UserDAO userDAO, PatientDAO patientDAO) {
        this.userDAO = userDAO;
        this.patientDAO = patientDAO;
    }

    /**
     * Authenticates a user using their username and plain password.
     */
    public User login(String username, String plainPassword) throws ServiceException {
        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(plainPassword)) {
            throw new ValidationException("Username and password are required.");
        }

        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            throw new ServiceException("Invalid username or password.");
        }

        if (!user.isActive()) {
            throw new ServiceException("This account has been deactivated. Please contact the administrator.");
        }

        boolean passwordMatches = PasswordUtil.checkPassword(plainPassword, user.getPasswordHash());
        if (!passwordMatches) {
            throw new ServiceException("Invalid username or password.");
        }

        return user;
    }

    /**
     * Registers a new self-service Patient account.
     */
    public User registerPatient(String username, String plainPassword, String fullName,
                                String email, String phone, String gender,
                                String dobStr, String bloodGroup, String address) throws ServiceException {

        // Validate
        if (!ValidationUtil.isValidUsername(username)) {
            throw new ValidationException("Username must be 3-50 alphanumeric characters.");
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("Invalid email format.");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new ValidationException("Invalid phone number format.");
        }

        if (userDAO.findByUsername(username) != null) {
            throw new ServiceException("Username is already taken.");
        }

        // Create User
        User user = new User();
        user.setUsername(username.trim());
        user.setPasswordHash(PasswordUtil.hashPassword(plainPassword));
        user.setEmail(email.trim());
        user.setFullName(fullName.trim());
        user.setPhoneNumber(phone.trim());
        user.setRole(Role.PATIENT);
        user.setActive(true);

        int userId = userDAO.create(user);

        // Auto-create linked Patient record with unique UHID
        Patient patient = new Patient();
        patient.setUserId(userId);
        String uhid = "UHID-" + System.currentTimeMillis() % 100000;
        patient.setUhid(uhid);
        patient.setFullName(fullName.trim());
        patient.setGender(Patient.Gender.fromString(gender));
        try {
            patient.setDateOfBirth(Date.valueOf(dobStr));
        } catch (Exception e) {
            patient.setDateOfBirth(Date.valueOf("1990-01-01"));
        }
        patient.setPhoneNumber(phone.trim());
        patient.setEmail(email.trim());
        patient.setBloodGroup(bloodGroup != null ? bloodGroup.trim() : "O+");
        patient.setAddress(address != null ? address.trim() : "");
        patient.setActive(true);

        patientDAO.create(patient);
        return user;
    }

    public User getUserById(int userId) {
        return userDAO.findById(userId);
    }
}
