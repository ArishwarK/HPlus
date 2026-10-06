package com.hospital.controller.doctor;

import com.hospital.exception.ServiceException;
import com.hospital.service.ConsultationService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller transitioning a token from CALLED to IN_CONSULTATION.
 */
public class StartConsultationServlet extends HttpServlet {

    private ConsultationService consultationService;

    @Override
    public void init() {
        this.consultationService = new ConsultationService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int queueId = Integer.parseInt(request.getParameter("queueId"));

        try {
            consultationService.startConsultation(queueId);
            response.sendRedirect(request.getContextPath() + "/doctor/dashboard?status=started");
        } catch (ServiceException e) {
            response.sendRedirect(request.getContextPath() + 
                    "/doctor/dashboard?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}
