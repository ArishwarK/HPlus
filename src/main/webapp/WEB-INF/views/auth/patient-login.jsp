<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient Outpatient Portal Login - Smart Hospital</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login.css">
</head>
<body class="auth-body">
<div class="login-wrapper">
    <div class="login-card">
        <div class="login-header">
            <div class="brand-badge">H+</div>
            <h2>Patient Queue Portal</h2>
            <p>Track your token number, position in queue, and live wait time</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/patient/login" method="post" class="login-form">
            <div class="form-group">
                <label for="tokenNumber">Daily Token Number / Display Code *</label>
                <input type="text" id="tokenNumber" name="tokenNumber" class="form-control" placeholder="e.g. CARD-45 or 45" required value="${param.tokenNumber}">
            </div>

            <div class="form-group">
                <label for="phoneNumber">Registered Mobile Number *</label>
                <input type="tel" id="phoneNumber" name="phoneNumber" class="form-control" placeholder="e.g. +1 555-234-5678" required>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Track My Live Token</button>
        </form>

        <div class="login-footer">
            <p>New walk-in patient? Visit Central Reception Desk to register and receive a token.</p>
            <div class="role-links">
                <a href="${pageContext.request.contextPath}/doctor/login">Doctor OPD Login</a> &bull;
                <a href="${pageContext.request.contextPath}/receptionist/login">Receptionist Desk</a> &bull;
                <a href="${pageContext.request.contextPath}/announcements">Public Announcements</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
