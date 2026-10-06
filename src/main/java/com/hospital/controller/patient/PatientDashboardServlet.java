package com.hospital.controller.patient;

import com.hospital.model.Appointment;
import com.hospital.model.Patient;
import com.hospital.model.QueueStatusResponse;
import com.hospital.model.User;
import com.hospital.service.AppointmentService;
import com.hospital.service.PatientService;
import com.hospital.service.QueueService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller rendering the Patient overview dashboard.
 */
public class PatientDashboardServlet extends HttpServlet {

    private PatientService patientService;
    private AppointmentService appointmentService;
    private QueueService queueService;

    @Override
    public void init() {
        this.patientService = new PatientService();
        this.appointmentService = new AppointmentService();
        this.queueService = new QueueService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        Patient patient = patientService.getPatientByUserId(currentUser.getUserId());
        if (patient == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=Patient+record+not+found.");
            return;
        }

        // Fetch today's appointment and queue status
        Appointment todayAppt = appointmentService.getTodayActiveAppointment(patient.getPatientId());
        QueueStatusResponse queueStatus = queueService.getQueueStatusForPatient(patient.getPatientId());
        List<Appointment> recentAppointments = appointmentService.getPatientAppointments(patient.getPatientId());

        request.setAttribute("patient", patient);
        request.setAttribute("todayAppointment", todayAppt);
        request.setAttribute("queueStatus", queueStatus);
        request.setAttribute("recentAppointments", recentAppointments);

        request.getRequestDispatcher("/WEB-INF/views/patient/dashboard.jsp").forward(request, response);
    }
}
