package com.hospital.filter;

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
 * Filter verifying that the user has an active, authenticated session.
 * Protects /admin/*, /doctor/*, /receptionist/*, /patient/*
 */
public class AuthenticationFilter implements Filter {

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
            String uri = req.getRequestURI();
            String query = req.getQueryString();
            String redirectUrl = (query != null) ? uri + "?" + query : uri;

            // Preserve requested URL for redirection after successful login
            if (session == null) {
                session = req.getSession(true);
            }
            session.setAttribute("redirectAfterLogin", redirectUrl);

            res.sendRedirect(req.getContextPath() + "/login?error=Session+expired.+Please+login.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
