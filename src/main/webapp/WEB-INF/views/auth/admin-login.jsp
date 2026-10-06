<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hospital Admin Console Login - Smart Hospital</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login.css">
</head>
<body class="auth-body admin-theme">
<div class="login-wrapper">
    <div class="login-card">
        <div class="login-header">
            <div class="brand-badge admin-badge">&#x1F6E1;</div>
            <h2>Hospital Admin Console</h2>
            <p>Executive governance, queue algorithms &amp; audit logging</p>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/login" method="post" class="login-form">
            <div class="form-group">
                <label for="username">Administrator Username / Email *</label>
                <input type="text" id="username" name="username" class="form-control" placeholder="e.g. admin or sysadmin" required value="${param.username}">
            </div>

            <div class="form-group">
                <label for="password">Master Password *</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Enter administrative password" required>
            </div>

            <div class="form-group">
                <label for="adminScope">Administrative Scope</label>
                <select id="adminScope" name="adminScope" class="form-control">
                    <option value="FULL">Hospital Operations Director (Full Access)</option>
                    <option value="CMO">Chief Medical Officer (Clinical Oversight)</option>
                    <option value="IT">IT Systems &amp; Queue Algorithm Engineer</option>
                </select>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Authenticate Admin Console</button>
        </form>

        <div class="login-footer">
            <p class="security-note">Enterprise RBAC Session Audited in MySQL</p>
            <div class="role-links">
                <a href="${pageContext.request.contextPath}/patient/login">Patient Portal</a> &bull;
                <a href="${pageContext.request.contextPath}/doctor/login">Doctor Login</a> &bull;
                <a href="${pageContext.request.contextPath}/announcements">Announcement Board</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
