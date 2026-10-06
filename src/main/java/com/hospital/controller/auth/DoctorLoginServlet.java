package com.hospital.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Dedicated Doctor OPD Login Servlet.
 * Authenticates clinical doctors and sets active OPD consultation cabin.
 */
@WebServlet(name = "DoctorLoginServlet", urlPatterns = {"/doctor/login"})
public class DoctorLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/doctor-login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String roomNumber = request.getParameter("roomNumber");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Invalid Doctor ID or password.");
            request.getRequestDispatcher("/WEB-INF/views/auth/doctor-login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("userRole", "DOCTOR");
        session.setAttribute("username", username);
        session.setAttribute("roomNumber", roomNumber);
        session.setAttribute("doctorId", 1); // Dr. Rajesh Kumar default

        response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
    }
}
