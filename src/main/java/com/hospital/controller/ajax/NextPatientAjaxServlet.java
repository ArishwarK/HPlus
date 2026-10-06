package com.hospital.controller.ajax;

import com.hospital.exception.ServiceException;
import com.hospital.model.QueueEntry;
import com.hospital.service.QueueService;
import com.hospital.util.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * AJAX endpoint for calling the next patient asynchronously.
 */
public class NextPatientAjaxServlet extends HttpServlet {

    private QueueService queueService;

    @Override
    public void init() {
        this.queueService = new QueueService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Map<String, Object> result = new HashMap<>();

        try {
            int doctorId = Integer.parseInt(request.getParameter("doctorId"));
            QueueEntry called = queueService.callNextPatient(doctorId);

            result.put("success", true);
            result.put("message", "Next patient called successfully");
            result.put("calledToken", called.getTokenNumber());
            result.put("tokenDisplay", called.getTokenDisplay());
            result.put("patientName", called.getPatientName());
            result.put("uhid", called.getPatientUhid());
            result.put("roomNumber", called.getRoomNumber());

            JsonUtil.sendJsonResponse(response, HttpServletResponse.SC_OK, result);

        } catch (ServiceException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            JsonUtil.sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Unexpected error calling next patient.");
            JsonUtil.sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, result);
        }
    }
}
