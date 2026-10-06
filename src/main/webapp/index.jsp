<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    // Standard MVC Root Dispatcher
    if (session.getAttribute("currentUser") != null) {
        String role = (String) session.getAttribute("userRole");
        if ("ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else if ("DOCTOR".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/doctor/dashboard");
        } else if ("RECEPTIONIST".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/receptionist/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/patient/dashboard");
        }
    } else {
        response.sendRedirect(request.getContextPath() + "/login");
    }
%>
