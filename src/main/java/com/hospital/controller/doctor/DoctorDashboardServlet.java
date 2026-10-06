package com.hospital.controller.doctor;

import com.hospital.model.Doctor;
import com.hospital.model.QueueEntry;
import com.hospital.model.QueueStatusResponse;
import com.hospital.model.User;
import com.hospital.service.DoctorService;
import com.hospital.service.QueueService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller rendering the Doctor consultation console and active queue.
 */
public class DoctorDashboardServlet extends HttpServlet {

    private DoctorService doctorService;
    private QueueService queueService;

    @Override
    public void init() {
        this.doctorService = new DoctorService();
        this.queueService = new QueueService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        Doctor doctor = doctorService.getDoctorByUserId(currentUser.getUserId());
        if (doctor == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=Doctor+profile+not+found.");
            return;
        }

        QueueStatusResponse queueData = queueService.getDoctorQueueStatus(doctor.getDoctorId());

        request.setAttribute("doctor", doctor);
        request.setAttribute("queueData", queueData);

        request.getRequestDispatcher("/WEB-INF/views/doctor/dashboard.jsp").forward(request, response);
    }
}
