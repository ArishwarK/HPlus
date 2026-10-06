package com.hospital.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Dedicated Receptionist Desk Login Servlet.
 * Authenticates front desk staff and sets active triage registration counter.
 */
@WebServlet(name = "ReceptionistLoginServlet", urlPatterns = {"/receptionist/login"})
public class ReceptionistLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/receptionist-login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String counterNumber = request.getParameter("counterNumber");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Invalid Staff ID or password.");
            request.getRequestDispatcher("/WEB-INF/views/auth/receptionist-login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("userRole", "RECEPTIONIST");
        session.setAttribute("username", username);
        session.setAttribute("counterNumber", counterNumber);

        response.sendRedirect(request.getContextPath() + "/receptionist/dashboard");
    }
}
