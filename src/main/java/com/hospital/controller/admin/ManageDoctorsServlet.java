package com.hospital.controller.admin;

import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.Role;
import com.hospital.model.User;
import com.hospital.service.AuthService;
import com.hospital.service.DepartmentService;
import com.hospital.service.DoctorService;
import com.hospital.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for managing medical doctors, active state, and credentials.
 */
public class ManageDoctorsServlet extends HttpServlet {

    private DoctorService doctorService;
    private DepartmentService departmentService;
    private AuthService authService;

    @Override
    public void init() {
        this.doctorService = new DoctorService();
        this.departmentService = new DepartmentService();
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Doctor> doctors = doctorService.getAllDoctors();
        List<Department> departments = departmentService.getAllActiveDepartments();

        request.setAttribute("doctors", doctors);
        request.setAttribute("departments", departments);

        request.getRequestDispatcher("/WEB-INF/views/admin/doctors.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String action = request.getParameter("action");

        if ("toggleStatus".equalsIgnoreCase(action)) {
            int doctorId = Integer.parseInt(request.getParameter("doctorId"));
            String statusStr = request.getParameter("status");
            doctorService.updateDoctorStatus(doctorId, Doctor.Status.fromString(statusStr));
            response.sendRedirect(request.getContextPath() + "/admin/doctors?updated=true");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/doctors");
    }
}
