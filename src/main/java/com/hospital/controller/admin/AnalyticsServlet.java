package com.hospital.controller.admin;

import com.hospital.dao.AuditLogDAO;
import com.hospital.model.AuditLog;
import com.hospital.model.Doctor;
import com.hospital.service.AppointmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.QueueService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller displaying hospital operational metrics, waiting times, and queue analytics.
 */
public class AnalyticsServlet extends HttpServlet {

    private DoctorService doctorService;
    private QueueService queueService;
    private AuditLogDAO auditLogDAO;

    @Override
    public void init() {
        this.doctorService = new DoctorService();
        this.queueService = new QueueService();
        this.auditLogDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Doctor> doctors = doctorService.getAllDoctors();
        for (Doctor d : doctors) {
            d.setActiveQueueCount(queueService.getDoctorQueueStatus(d.getDoctorId()).getTotalWaiting());
            d.setCompletedTodayCount(queueService.getDoctorQueueStatus(d.getDoctorId()).getTotalCompleted());
        }

        List<AuditLog> recentLogs = auditLogDAO.findRecent(25);

        request.setAttribute("doctors", doctors);
        request.setAttribute("recentLogs", recentLogs);

        request.getRequestDispatcher("/WEB-INF/views/admin/analytics.jsp").forward(request, response);
    }
}
