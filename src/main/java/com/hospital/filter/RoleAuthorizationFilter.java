package com.hospital.filter;

import com.hospital.model.Role;
import com.hospital.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter enforcing strict Role-Based Access Control (RBAC).
 * Prevents unauthorized privilege escalation.
 */
public class RoleAuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            // Let AuthenticationFilter handle redirect to login
            chain.doFilter(request, response);
            return;
        }

        String path = req.getRequestURI().substring(req.getContextPath().length());
        Role userRole = currentUser.getRole();

        boolean authorized = true;

        if (path.startsWith("/admin") && userRole != Role.ADMIN) {
            authorized = false;
        } else if (path.startsWith("/doctor") && userRole != Role.DOCTOR && userRole != Role.ADMIN) {
            authorized = false;
        } else if (path.startsWith("/receptionist") && userRole != Role.RECEPTIONIST && userRole != Role.ADMIN) {
            authorized = false;
        } else if (path.startsWith("/patient") && userRole != Role.PATIENT && userRole != Role.ADMIN) {
            authorized = false;
        }

        if (!authorized) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, 
                    "Access Denied: You do not possess the required credentials for this portal.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
