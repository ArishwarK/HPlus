package com.hospital.controller.patient;

import com.hospital.model.Patient;
import com.hospital.model.QueueStatusResponse;
import com.hospital.model.User;
import com.hospital.service.PatientService;
import com.hospital.service.QueueService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller rendering the dedicated real-time Patient Queue Tracking Page.
 * Backed by periodic AJAX polling without full-page reloads.
 */
public class PatientQueueServlet extends HttpServlet {

    private PatientService patientService;
    private QueueService queueService;

    @Override
    public void init() {
        this.patientService = new PatientService();
        this.queueService = new QueueService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        Patient patient = patientService.getPatientByUserId(currentUser.getUserId());
        if (patient == null) {
            response.sendRedirect(request.getContextPath() + "/patient/dashboard");
            return;
        }

        QueueStatusResponse queueData = queueService.getQueueStatusForPatient(patient.getPatientId());

        request.setAttribute("patient", patient);
        request.setAttribute("queueData", queueData);

        request.getRequestDispatcher("/WEB-INF/views/patient/queue.jsp").forward(request, response);
    }
}
