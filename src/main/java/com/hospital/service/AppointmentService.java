package com.hospital.service;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.exception.ServiceException;
import com.hospital.exception.ValidationException;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.PriorityLevel;
import com.hospital.util.DateTimeUtil;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * Service managing appointment bookings and scheduling rules.
 */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO;
    private final DoctorDAO doctorDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
        this.doctorDAO = new DoctorDAO();
    }

    public AppointmentService(AppointmentDAO appointmentDAO, DoctorDAO doctorDAO) {
        this.appointmentDAO = appointmentDAO;
        this.doctorDAO = doctorDAO;
    }

    public Appointment bookAppointment(int patientId, int doctorId, int departmentId,
                                      Date date, Time time, Appointment.Type type,
                                      PriorityLevel priority, String symptoms,
                                      Integer createdByUserId) throws ServiceException {

        // Validate doctor exists and is active
        Doctor doctor = doctorDAO.findById(doctorId);
        if (doctor == null || !doctor.isActive()) {
            throw new ServiceException("Selected doctor is not available for appointments.");
        }

        // Validate date is not in the past
        Date today = DateTimeUtil.today();
        if (date.before(today)) {
            throw new ValidationException("Cannot book an appointment for a past date.");
        }

        // Check for double booking conflict
        if (appointmentDAO.hasConflict(doctorId, date, time)) {
            throw new ServiceException("The doctor already has an appointment booked at " + 
                                       DateTimeUtil.formatTime(time) + " on this date. Please choose another time slot.");
        }

        // Generate unique appointment number: APT-YYYYMMDD-XXXX
        String apptNumber = "APT-" + date.toString().replace("-", "") + "-" + (System.currentTimeMillis() % 10000);

        Appointment appt = new Appointment();
        appt.setAppointmentNumber(apptNumber);
        appt.setPatientId(patientId);
        appt.setDoctorId(doctorId);
        appt.setDepartmentId(departmentId);
        appt.setAppointmentDate(date);
        appt.setAppointmentTime(time);
        appt.setType(type != null ? type : Appointment.Type.ONLINE_SCHEDULED);
        appt.setPriorityLevel(priority != null ? priority : PriorityLevel.NORMAL);
        appt.setStatus(Appointment.Status.BOOKED);
        appt.setSymptoms(symptoms != null ? symptoms.trim() : "");
        appt.setCreatedByUserId(createdByUserId);

        int id = appointmentDAO.create(appt);
        appt.setAppointmentId(id);
        return appt;
    }

    public List<Appointment> getPatientAppointments(int patientId) {
        return appointmentDAO.findByPatientId(patientId);
    }

    public Appointment getTodayActiveAppointment(int patientId) {
        return appointmentDAO.findActiveTodayAppointment(patientId, DateTimeUtil.today());
    }

    public Appointment getAppointmentById(int appointmentId) {
        return appointmentDAO.findById(appointmentId);
    }

    public boolean cancelAppointment(int appointmentId) throws ServiceException {
        Appointment appt = appointmentDAO.findById(appointmentId);
        if (appt == null) {
            throw new ServiceException("Appointment not found.");
        }
        if (appt.getStatus() == Appointment.Status.COMPLETED) {
            throw new ServiceException("Cannot cancel an already completed appointment.");
        }
        return appointmentDAO.updateStatus(appointmentId, Appointment.Status.CANCELLED);
    }

    public int getTodayAppointmentsCount() {
        return appointmentDAO.countTodayTotal(DateTimeUtil.today());
    }
}
