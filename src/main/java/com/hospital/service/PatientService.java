package com.hospital.service;

import com.hospital.dao.PatientDAO;
import com.hospital.exception.ServiceException;
import com.hospital.model.Patient;
import com.hospital.util.ValidationUtil;

import java.util.List;

/**
 * Service managing patient profiles and desk registrations.
 */
public class PatientService {

    private final PatientDAO patientDAO;

    public PatientService() {
        this.patientDAO = new PatientDAO();
    }

    public PatientService(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    public Patient getPatientById(int patientId) {
        return patientDAO.findById(patientId);
    }

    public Patient getPatientByUserId(int userId) {
        return patientDAO.findByUserId(userId);
    }

    public Patient getPatientByUhid(String uhid) {
        return patientDAO.findByUhid(uhid);
    }

    public List<Patient> searchPatients(String query) {
        if (!ValidationUtil.isNotEmpty(query)) {
            return List.of();
        }
        return patientDAO.search(query);
    }

    public Patient registerWalkInPatient(Patient patient) throws ServiceException {
        if (!ValidationUtil.isNotEmpty(patient.getFullName())) {
            throw new ServiceException("Patient full name is required.");
        }
        if (!ValidationUtil.isValidPhone(patient.getPhoneNumber())) {
            throw new ServiceException("Valid phone number is required.");
        }

        // Auto-generate UHID
        String uhid = "UHID-" + (System.currentTimeMillis() % 1000000);
        patient.setUhid(uhid);
        patient.setActive(true);

        int id = patientDAO.create(patient);
        patient.setPatientId(id);
        return patient;
    }

    public int getTotalPatientsCount() {
        return patientDAO.countTotal();
    }
}
