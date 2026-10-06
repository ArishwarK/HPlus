package com.hospital.controller.receptionist;

import com.hospital.exception.ServiceException;
import com.hospital.model.PriorityLevel;
import com.hospital.model.QueueEntry;
import com.hospital.service.QueueService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller generating a new queue token for an existing patient.
 */
public class GenerateTokenServlet extends HttpServlet {

    private QueueService queueService;

    @Override
    public void init() {
        this.queueService = new QueueService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int patientId = Integer.parseInt(request.getParameter("patientId"));
        int doctorId = Integer.parseInt(request.getParameter("doctorId"));
        String priorityStr = request.getParameter("priorityLevel");
        PriorityLevel priority = PriorityLevel.fromString(priorityStr);

        try {
            QueueEntry token = queueService.generateToken(doctorId, patientId, null, priority);
            response.sendRedirect(request.getContextPath() + 
                    "/receptionist/dashboard?tokenGenerated=" + token.getTokenDisplay());
        } catch (ServiceException e) {
            response.sendRedirect(request.getContextPath() + 
                    "/receptionist/dashboard?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}
