package com.hospital.controller.doctor;

import com.hospital.exception.ServiceException;
import com.hospital.model.Doctor;
import com.hospital.model.QueueEntry;
import com.hospital.model.User;
import com.hospital.service.DoctorService;
import com.hospital.service.QueueService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller advancing the doctor's queue to the next eligible patient.
 */
public class CallNextServlet extends HttpServlet {

    private DoctorService doctorService;
    private QueueService queueService;

    @Override
    public void init() {
        this.doctorService = new DoctorService();
        this.queueService = new QueueService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        Doctor doctor = doctorService.getDoctorByUserId(currentUser.getUserId());

        if (doctor == null) {
            response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
            return;
        }

        try {
            QueueEntry called = queueService.callNextPatient(doctor.getDoctorId());
            response.sendRedirect(request.getContextPath() + 
                    "/doctor/dashboard?calledToken=" + called.getTokenNumber());
        } catch (ServiceException e) {
            response.sendRedirect(request.getContextPath() + 
                    "/doctor/dashboard?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}
