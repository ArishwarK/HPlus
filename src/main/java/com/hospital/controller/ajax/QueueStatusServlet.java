package com.hospital.controller.ajax;

import com.hospital.model.Patient;
import com.hospital.model.QueueStatusResponse;
import com.hospital.model.User;
import com.hospital.service.PatientService;
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
 * Real-time AJAX endpoint queried periodically (e.g. every 5 seconds)
 * by the patient queue dashboard.
 * Returns the exact JSON contract specified in Section 6.
 */
public class QueueStatusServlet extends HttpServlet {

    private PatientService patientService;
    private QueueService queueService;

    @Override
    public void init() {
        this.patientService = new PatientService();
        this.queueService = new QueueService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Patient can be identified via session or explicit patientId query param
        Integer patientId = null;

        String pidParam = request.getParameter("patientId");
        if (pidParam != null && !pidParam.trim().isEmpty()) {
            try {
                patientId = Integer.parseInt(pidParam.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        if (patientId == null) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("currentUser") != null) {
                User user = (User) session.getAttribute("currentUser");
                Patient patient = patientService.getPatientByUserId(user.getUserId());
                if (patient != null) {
                    patientId = patient.getPatientId();
                }
            }
        }

        if (patientId == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "No patient context provided or active session.");
            JsonUtil.sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, err);
            return;
        }

        QueueStatusResponse statusResponse = queueService.getQueueStatusForPatient(patientId);

        if (!statusResponse.isSuccess()) {
            JsonUtil.sendJsonResponse(response, HttpServletResponse.SC_OK, statusResponse);
            return;
        }

        JsonUtil.sendJsonResponse(response, HttpServletResponse.SC_OK, statusResponse);
    }
}
