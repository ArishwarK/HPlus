package com.hospital.controller.admin;

import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.service.AppointmentService;
import com.hospital.service.DepartmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.PatientService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller rendering the Hospital Executive Admin Dashboard.
 */
public class AdminDashboardServlet extends HttpServlet {

    private DoctorService doctorService;
    private DepartmentService departmentService;
    private PatientService patientService;
    private AppointmentService appointmentService;

    @Override
    public void init() {
        this.doctorService = new DoctorService();
        this.departmentService = new DepartmentService();
        this.patientService = new PatientService();
        this.appointmentService = new AppointmentService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Doctor> doctors = doctorService.getAllDoctors();
        List<Department> departments = departmentService.getAllActiveDepartments();
        int totalPatients = patientService.getTotalPatientsCount();
        int todayAppointments = appointmentService.getTodayAppointmentsCount();

        long activeDoctorsCount = doctors.stream().filter(Doctor::isActive).count();

        request.setAttribute("doctors", doctors);
        request.setAttribute("departments", departments);
        request.setAttribute("totalPatients", totalPatients);
        request.setAttribute("todayAppointments", todayAppointments);
        request.setAttribute("activeDoctorsCount", activeDoctorsCount);

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
