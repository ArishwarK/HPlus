package com.hospital.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Dedicated Patient Login Servlet.
 * Allows patients to look up and log in using their token number and registered mobile number.
 */
@WebServlet(name = "PatientLoginServlet", urlPatterns = {"/patient/login"})
public class PatientLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/patient-login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tokenStr = request.getParameter("tokenNumber");
        String phoneNumber = request.getParameter("phoneNumber");

        if (tokenStr == null || tokenStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please provide a valid Token Number.");
            request.getRequestDispatcher("/WEB-INF/views/auth/patient-login.jsp").forward(request, response);
            return;
        }

        // Establish patient session
        HttpSession session = request.getSession(true);
        session.setAttribute("userRole", "PATIENT");
        session.setAttribute("tokenNumber", tokenStr.trim());
        session.setAttribute("phoneNumber", phoneNumber);

        response.sendRedirect(request.getContextPath() + "/patient/queue");
    }
}
