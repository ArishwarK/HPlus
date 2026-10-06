package com.hospital.controller.receptionist;

import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.service.AppointmentService;
import com.hospital.service.DepartmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.PatientService;
import com.hospital.service.QueueService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Receptionist Desk dashboard.
 */
public class ReceptionistDashboardServlet extends HttpServlet {

    private DepartmentService departmentService;
    private DoctorService doctorService;
    private PatientService patientService;
    private AppointmentService appointmentService;
    private QueueService queueService;

    @Override
    public void init() {
        this.departmentService = new DepartmentService();
        this.doctorService = new DoctorService();
        this.patientService = new PatientService();
        this.appointmentService = new AppointmentService();
        this.queueService = new QueueService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Department> departments = departmentService.getAllActiveDepartments();
        List<Doctor> doctors = doctorService.getAllDoctors();
        int totalPatients = patientService.getTotalPatientsCount();
        int todayAppts = appointmentService.getTodayAppointmentsCount();

        // Enrich doctors with current queue metrics
        for (Doctor d : doctors) {
            d.setActiveQueueCount(queueService.getDoctorQueueStatus(d.getDoctorId()).getTotalWaiting());
            d.setCurrentServingToken(queueService.getDoctorQueueStatus(d.getDoctorId()).getCurrentToken());
        }

        request.setAttribute("departments", departments);
        request.setAttribute("doctors", doctors);
        request.setAttribute("totalPatients", totalPatients);
        request.setAttribute("todayAppointments", todayAppts);

        request.getRequestDispatcher("/WEB-INF/views/receptionist/dashboard.jsp").forward(request, response);
    }
}
