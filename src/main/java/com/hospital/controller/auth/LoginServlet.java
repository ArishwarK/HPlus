package com.hospital.controller.auth;

import com.hospital.exception.ServiceException;
import com.hospital.model.Role;
import com.hospital.model.User;
import com.hospital.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller handling user authentication and role-based redirect.
 */
public class LoginServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() {
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            redirectToDashboard(user.getRole(), request, response);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = authService.login(username, password);

            // Establish secure session
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            session.setAttribute("userRole", user.getRole().name());
            session.setAttribute("userName", user.getFullName());

            // Handle prior redirection URL if any
            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                session.removeAttribute("redirectAfterLogin");
                response.sendRedirect(redirectUrl);
                return;
            }

            redirectToDashboard(user.getRole(), request, response);

        } catch (ServiceException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("username", username);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }

    private void redirectToDashboard(Role role, HttpServletRequest req, HttpServletResponse res) throws IOException {
        String ctx = req.getContextPath();
        switch (role) {
            case ADMIN:
                res.sendRedirect(ctx + "/admin/dashboard");
                break;
            case DOCTOR:
                res.sendRedirect(ctx + "/doctor/dashboard");
                break;
            case RECEPTIONIST:
                res.sendRedirect(ctx + "/receptionist/dashboard");
                break;
            case PATIENT:
            default:
                res.sendRedirect(ctx + "/patient/dashboard");
                break;
        }
    }
}
