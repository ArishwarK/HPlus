package com.hospital.controller.doctor;

import com.hospital.exception.ServiceException;
import com.hospital.service.ConsultationService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller concluding a consultation, recording clinical diagnosis and prescription.
 */
public class CompleteConsultationServlet extends HttpServlet {

    private ConsultationService consultationService;

    @Override
    public void init() {
        this.consultationService = new ConsultationService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int queueId = Integer.parseInt(request.getParameter("queueId"));
        String complaints = request.getParameter("chiefComplaints");
        String diagnosis = request.getParameter("diagnosis");
        String prescription = request.getParameter("prescription");

        try {
            consultationService.completeConsultation(queueId, complaints, diagnosis, prescription);
            response.sendRedirect(request.getContextPath() + 
                    "/doctor/dashboard?completed=true&msg=Consultation+recorded+successfully");
        } catch (ServiceException e) {
            response.sendRedirect(request.getContextPath() + 
                    "/doctor/dashboard?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}
