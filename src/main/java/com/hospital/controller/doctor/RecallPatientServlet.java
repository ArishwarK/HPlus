package com.hospital.controller.doctor;

import com.hospital.exception.ServiceException;
import com.hospital.service.QueueService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller recalling a skipped patient back into active waiting queue.
 */
public class RecallPatientServlet extends HttpServlet {

    private QueueService queueService;

    @Override
    public void init() {
        this.queueService = new QueueService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int queueId = Integer.parseInt(request.getParameter("queueId"));

        try {
            queueService.recallPatient(queueId);
            response.sendRedirect(request.getContextPath() + "/doctor/dashboard?status=recalled");
        } catch (ServiceException e) {
            response.sendRedirect(request.getContextPath() + 
                    "/doctor/dashboard?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}
