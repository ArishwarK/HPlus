<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Doctor OPD Cabin Login - Smart Hospital</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login.css">
</head>
<body class="auth-body doctor-theme">
<div class="login-wrapper">
    <div class="login-card">
        <div class="login-header">
            <div class="brand-badge doctor-badge">Dr.</div>
            <h2>Doctor OPD Cabin Portal</h2>
            <p>Clinical consultation console, token queue & electronic prescriptions</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/doctor/login" method="post" class="login-form">
            <div class="form-group">
                <label for="username">Doctor Medical ID / Username *</label>
                <input type="text" id="username" name="username" class="form-control" placeholder="e.g. dr.kumar or DOC-102" required value="${param.username}">
            </div>

            <div class="form-group">
                <label for="password">Password *</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Enter password" required>
            </div>

            <div class="form-group">
                <label for="roomNumber">Assigned OPD Cabin Room</label>
                <select id="roomNumber" name="roomNumber" class="form-control">
                    <option value="OPD-204">OPD-204 (Cardiology - Dr. Rajesh Kumar)</option>
                    <option value="OPD-112">OPD-112 (Orthopedics - Dr. Elena Rostova)</option>
                    <option value="OPD-305">OPD-305 (Pediatrics - Dr. Marcus Vance)</option>
                    <option value="OPD-101">OPD-101 (General Medicine - Dr. Aisha Patel)</option>
                </select>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Sign In to OPD Cabin</button>
        </form>

        <div class="login-footer">
            <p class="security-note">HIPAA & NABH Compliant Clinical Access</p>
            <div class="role-links">
                <a href="${pageContext.request.contextPath}/patient/login">Patient Portal</a> &bull;
                <a href="${pageContext.request.contextPath}/receptionist/login">Receptionist Desk</a> &bull;
                <a href="${pageContext.request.contextPath}/admin/login">Admin Console</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
