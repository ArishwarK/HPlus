package com.hospital.controller.ajax;

import com.hospital.service.AppointmentService;
import com.hospital.service.PatientService;
import com.hospital.util.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * AJAX endpoint providing live overall hospital metrics for admin/receptionist monitor widgets.
 */
public class LiveStatsAjaxServlet extends HttpServlet {

    private PatientService patientService;
    private AppointmentService appointmentService;

    @Override
    public void init() {
        this.patientService = new PatientService();
        this.appointmentService = new AppointmentService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPatients", patientService.getTotalPatientsCount());
        stats.put("todayAppointments", appointmentService.getTodayAppointmentsCount());
        stats.put("timestamp", System.currentTimeMillis());

        JsonUtil.sendJsonResponse(response, HttpServletResponse.SC_OK, stats);
    }
}
