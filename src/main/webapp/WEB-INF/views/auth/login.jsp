<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hospital Portal Login - Smart Hospital Queue</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo-badge">H+</div>
            <h2>Hospital Queue Portal</h2>
            <p>Real-Time Smart Queue &amp; Waiting-Time Estimation</p>
        </div>

        <div class="auth-body">
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">
                    ${errorMessage}
                </div>
            </c:if>
            <c:if test="${not empty param.message}">
                <div class="alert alert-success">
                    ${param.message}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="POST">
                <div class="form-group">
                    <label class="form-label" for="username">Username</label>
                    <input type="text" id="username" name="username" class="form-control" 
                           placeholder="Enter your username" value="${username}" required autofocus>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <input type="password" id="password" name="password" class="form-control" 
                           placeholder="••••••••" required>
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; padding: 12px; font-size: 15px;">
                    Secure Sign In
                </button>
            </form>

            <div style="margin-top: 16px; text-align: center; font-size: 13px; color: var(--gray-500);">
                New patient? <a href="${pageContext.request.contextPath}/register">Create an Account</a>
            </div>

            <!-- Fast-Fill College Evaluation Credentials -->
            <div class="auth-demo-accounts">
                <div class="auth-demo-title">Quick Demo Login (Click to Fill)</div>
                <div class="demo-account-grid">
                    <button type="button" class="demo-btn" onclick="fillCredentials('admin', 'password123')">
                        <span class="demo-role-tag">ADMIN</span>
                        Dr. Sarah (Chief)
                    </button>
                    <button type="button" class="demo-btn" onclick="fillCredentials('dr.kumar', 'password123')">
                        <span class="demo-role-tag">DOCTOR</span>
                        Dr. Kumar (Cardio)
                    </button>
                    <button type="button" class="demo-btn" onclick="fillCredentials('receptionist1', 'password123')">
                        <span class="demo-role-tag">RECEPTIONIST</span>
                        Nancy Cooper
                    </button>
                    <button type="button" class="demo-btn" onclick="fillCredentials('patient.john', 'password123')">
                        <span class="demo-role-tag">PATIENT</span>
                        Johnathan Doe
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>
