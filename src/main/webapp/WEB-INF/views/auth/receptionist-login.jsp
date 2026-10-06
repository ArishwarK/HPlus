<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Receptionist Desk Login - Smart Hospital</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login.css">
</head>
<body class="auth-body receptionist-theme">
<div class="login-wrapper">
    <div class="login-card">
        <div class="login-header">
            <div class="brand-badge rec-badge">REC</div>
            <h2>Reception &amp; Triage Desk</h2>
            <p>Walk-in patient registration, triage scoring &amp; token dispenser</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/receptionist/login" method="post" class="login-form">
            <div class="form-group">
                <label for="username">Staff Badge ID / Username *</label>
                <input type="text" id="username" name="username" class="form-control" placeholder="e.g. REC-4012 or receptionist" required value="${param.username}">
            </div>

            <div class="form-group">
                <label for="password">Staff Password *</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Enter password" required>
            </div>

            <div class="form-group">
                <label for="counterNumber">Front Desk Registration Counter</label>
                <select id="counterNumber" name="counterNumber" class="form-control">
                    <option value="1">Counter 1 - Central OPD Registration</option>
                    <option value="2">Counter 2 - Fast Track Senior Citizen Desk</option>
                    <option value="3">Counter 3 - Pediatric &amp; Emergency Counter</option>
                </select>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Open Registration Desk</button>
        </form>

        <div class="login-footer">
            <div class="role-links">
                <a href="${pageContext.request.contextPath}/patient/login">Patient Portal</a> &bull;
                <a href="${pageContext.request.contextPath}/doctor/login">Doctor Login</a> &bull;
                <a href="${pageContext.request.contextPath}/announcements">Live Announcements</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
