package com.hospital.controller.receptionist;

import com.hospital.exception.ServiceException;
import com.hospital.model.Appointment;
import com.hospital.model.QueueEntry;
import com.hospital.service.AppointmentService;
import com.hospital.service.QueueService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller marking patient arrival for a booked appointment and generating their token.
 */
public class MarkArrivalServlet extends HttpServlet {

    private AppointmentService appointmentService;
    private QueueService queueService;

    @Override
    public void init() {
        this.appointmentService = new AppointmentService();
        this.queueService = new QueueService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int appointmentId = Integer.parseInt(request.getParameter("appointmentId"));

        try {
            Appointment appt = appointmentService.getAppointmentById(appointmentId);
            if (appt == null) {
                throw new ServiceException("Appointment not found.");
            }

            QueueEntry token = queueService.generateToken(
                    appt.getDoctorId(), appt.getPatientId(), appt.getAppointmentId(), appt.getPriorityLevel());

            response.sendRedirect(request.getContextPath() + 
                    "/receptionist/dashboard?arrivalSuccess=true&token=" + token.getTokenDisplay());

        } catch (ServiceException e) {
            response.sendRedirect(request.getContextPath() + 
                    "/receptionist/dashboard?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}
