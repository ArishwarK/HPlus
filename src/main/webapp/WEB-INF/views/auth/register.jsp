<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient Registration - Smart Hospital Queue</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-card auth-card-wide">
        <div class="auth-header">
            <div class="auth-logo-badge">H+</div>
            <h2>Patient Registration</h2>
            <p>Enroll for smart token tracking and appointment bookings</p>
        </div>

        <div class="auth-body">
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">
                    ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="POST" onsubmit="return validateRegisterForm(this)">
                <div class="form-grid-2">
                    <div class="form-group">
                        <label class="form-label" for="username">Username <span class="required">*</span></label>
                        <input type="text" id="username" name="username" class="form-control" value="${username}" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="password">Password <span class="required">*</span></label>
                        <input type="password" id="password" name="password" class="form-control" placeholder="Min 6 characters" required>
                    </div>
                </div>

                <div class="form-grid-2">
                    <div class="form-group">
                        <label class="form-label" for="fullName">Full Name <span class="required">*</span></label>
                        <input type="text" id="fullName" name="fullName" class="form-control" value="${fullName}" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="gender">Gender <span class="required">*</span></label>
                        <select id="gender" name="gender" class="form-control" required>
                            <option value="MALE">Male</option>
                            <option value="FEMALE">Female</option>
                            <option value="OTHER">Other</option>
                        </select>
                    </div>
                </div>

                <div class="form-grid-2">
                    <div class="form-group">
                        <label class="form-label" for="email">Email Address <span class="required">*</span></label>
                        <input type="email" id="email" name="email" class="form-control" value="${email}" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="phone">Phone Number <span class="required">*</span></label>
                        <input type="text" id="phone" name="phone" class="form-control" value="${phone}" required>
                    </div>
                </div>

                <div class="form-grid-2">
                    <div class="form-group">
                        <label class="form-label" for="dob">Date of Birth</label>
                        <input type="date" id="dob" name="dob" class="form-control" value="1995-01-01" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="bloodGroup">Blood Group</label>
                        <select id="bloodGroup" name="bloodGroup" class="form-control">
                            <option value="O+">O+</option>
                            <option value="O-">O-</option>
                            <option value="A+">A+</option>
                            <option value="A-">A-</option>
                            <option value="B+">B+</option>
                            <option value="B-">B-</option>
                            <option value="AB+">AB+</option>
                            <option value="AB-">AB-</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="address">Residential Address</label>
                    <input type="text" id="address" name="address" class="form-control" placeholder="Street, City, Postal Code">
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; padding: 12px; font-size: 15px;">
                    Complete Registration &amp; Get UHID
                </button>
            </form>

            <div style="margin-top: 16px; text-align: center; font-size: 13px; color: var(--gray-500);">
                Already have an account? <a href="${pageContext.request.contextPath}/login">Sign In</a>
            </div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>
</body>
</html>
