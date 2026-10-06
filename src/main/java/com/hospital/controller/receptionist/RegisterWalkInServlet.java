package com.hospital.controller.receptionist;

import com.hospital.exception.ServiceException;
import com.hospital.model.Patient;
import com.hospital.model.PriorityLevel;
import com.hospital.model.QueueEntry;
import com.hospital.service.PatientService;
import com.hospital.service.QueueService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;

/**
 * Controller registering walk-in patients directly at the front desk
 * and optionally issuing an immediate queue token.
 */
public class RegisterWalkInServlet extends HttpServlet {

    private PatientService patientService;
    private QueueService queueService;

    @Override
    public void init() {
        this.patientService = new PatientService();
        this.queueService = new QueueService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String gender = request.getParameter("gender");
        String dob = request.getParameter("dob");
        String phone = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String bloodGroup = request.getParameter("bloodGroup");
        String address = request.getParameter("address");
        String doctorIdStr = request.getParameter("doctorId");
        String priorityStr = request.getParameter("priorityLevel");

        Patient p = new Patient();
        p.setFullName(fullName);
        p.setGender(Patient.Gender.fromString(gender));
        try {
            p.setDateOfBirth(Date.valueOf(dob));
        } catch (Exception e) {
            p.setDateOfBirth(Date.valueOf("1990-01-01"));
        }
        p.setPhoneNumber(phone);
        p.setEmail(email);
        p.setBloodGroup(bloodGroup);
        p.setAddress(address);

        try {
            Patient registered = patientService.registerWalkInPatient(p);

            // If a doctor was selected, immediately generate token
            if (doctorIdStr != null && !doctorIdStr.isEmpty()) {
                int doctorId = Integer.parseInt(doctorIdStr);
                PriorityLevel priority = PriorityLevel.fromString(priorityStr);
                QueueEntry token = queueService.generateToken(doctorId, registered.getPatientId(), null, priority);
                response.sendRedirect(request.getContextPath() + 
                        "/receptionist/dashboard?walkInSuccess=true&token=" + token.getTokenDisplay() + 
                        "&uhid=" + registered.getUhid());
                return;
            }

            response.sendRedirect(request.getContextPath() + 
                    "/receptionist/dashboard?registered=true&uhid=" + registered.getUhid());

        } catch (ServiceException e) {
            response.sendRedirect(request.getContextPath() + 
                    "/receptionist/dashboard?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}
