package com.hospital.service;

import com.hospital.dao.DoctorDAO;
import com.hospital.model.Doctor;

import java.util.List;

/**
 * Service managing doctors and their clinical duty status.
 */
public class DoctorService {

    private final DoctorDAO doctorDAO;

    public DoctorService() {
        this.doctorDAO = new DoctorDAO();
    }

    public DoctorService(DoctorDAO doctorDAO) {
        this.doctorDAO = doctorDAO;
    }

    public List<Doctor> getAllDoctors() {
        return doctorDAO.findAll();
    }

    public List<Doctor> getDoctorsByDepartment(int departmentId) {
        return doctorDAO.findByDepartmentId(departmentId);
    }

    public Doctor getDoctorById(int doctorId) {
        return doctorDAO.findById(doctorId);
    }

    public Doctor getDoctorByUserId(int userId) {
        return doctorDAO.findByUserId(userId);
    }

    public boolean updateDoctorStatus(int doctorId, Doctor.Status status) {
        return doctorDAO.updateStatus(doctorId, status);
    }

    public int createDoctor(Doctor doctor) {
        return doctorDAO.create(doctor);
    }
}
