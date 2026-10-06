package com.hospital.controller.patient;

import com.hospital.model.Consultation;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.service.ConsultationService;
import com.hospital.service.PatientService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller displaying the patient's past medical visits and prescriptions.
 */
public class PatientHistoryServlet extends HttpServlet {

    private PatientService patientService;
    private ConsultationService consultationService;

    @Override
    public void init() {
        this.patientService = new PatientService();
        this.consultationService = new ConsultationService();
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

        List<Consultation> consultations = consultationService.getPatientHistory(patient.getPatientId());

        request.setAttribute("patient", patient);
        request.setAttribute("consultations", consultations);

        request.getRequestDispatcher("/WEB-INF/views/patient/history.jsp").forward(request, response);
    }
}
