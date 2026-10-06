package com.hospital.controller.ajax;

import com.hospital.model.Doctor;
import com.hospital.model.QueueStatusResponse;
import com.hospital.model.User;
import com.hospital.service.DoctorService;
import com.hospital.service.QueueService;
import com.hospital.util.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Real-time AJAX endpoint returning live queue state for doctor console.
 */
public class DoctorQueueAjaxServlet extends HttpServlet {

    private DoctorService doctorService;
    private QueueService queueService;

    @Override
    public void init() {
        this.doctorService = new DoctorService();
        this.queueService = new QueueService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Integer doctorId = null;
        String didParam = request.getParameter("doctorId");
        if (didParam != null && !didParam.trim().isEmpty()) {
            try {
                doctorId = Integer.parseInt(didParam.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        if (doctorId == null) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("currentUser") != null) {
                User user = (User) session.getAttribute("currentUser");
                Doctor doc = doctorService.getDoctorByUserId(user.getUserId());
                if (doc != null) {
                    doctorId = doc.getDoctorId();
                }
            }
        }

        if (doctorId == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Doctor ID not specified");
            JsonUtil.sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, err);
            return;
        }

        QueueStatusResponse status = queueService.getDoctorQueueStatus(doctorId);
        JsonUtil.sendJsonResponse(response, HttpServletResponse.SC_OK, status);
    }
}
