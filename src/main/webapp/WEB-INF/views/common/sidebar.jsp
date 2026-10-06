<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<aside class="sidebar">
    <div class="sidebar-header">
        <div class="hospital-logo-icon">H+</div>
        <div>
            <div class="hospital-title">Apex Care</div>
            <div class="hospital-subtitle">Queue System</div>
        </div>
    </div>

    <nav class="sidebar-nav">
        <!-- Patient Navigation Links -->
        <c:if test="${sessionScope.userRole == 'PATIENT'}">
            <a href="${pageContext.request.contextPath}/patient/dashboard" class="nav-item ${activeNav == 'dashboard' ? 'active' : ''}">
                Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/patient/queue" class="nav-item ${activeNav == 'queue' ? 'active' : ''}">
                Live Token Tracker
            </a>
            <a href="${pageContext.request.contextPath}/patient/book-appointment" class="nav-item ${activeNav == 'book' ? 'active' : ''}">
                Book Appointment
            </a>
            <a href="${pageContext.request.contextPath}/patient/history" class="nav-item ${activeNav == 'history' ? 'active' : ''}">
                Visit History
            </a>
        </c:if>

        <!-- Doctor Navigation Links -->
        <c:if test="${sessionScope.userRole == 'DOCTOR'}">
            <a href="${pageContext.request.contextPath}/doctor/dashboard" class="nav-item ${activeNav == 'dashboard' ? 'active' : ''}">
                Consultation Console
            </a>
        </c:if>

        <!-- Receptionist Navigation Links -->
        <c:if test="${sessionScope.userRole == 'RECEPTIONIST'}">
            <a href="${pageContext.request.contextPath}/receptionist/dashboard" class="nav-item ${activeNav == 'dashboard' ? 'active' : ''}">
                Reception Desk
            </a>
        </c:if>

        <!-- Admin Navigation Links -->
        <c:if test="${sessionScope.userRole == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item ${activeNav == 'dashboard' ? 'active' : ''}">
                Executive Overview
            </a>
            <a href="${pageContext.request.contextPath}/admin/doctors" class="nav-item ${activeNav == 'doctors' ? 'active' : ''}">
                Doctors &amp; Shifts
            </a>
            <a href="${pageContext.request.contextPath}/admin/departments" class="nav-item ${activeNav == 'departments' ? 'active' : ''}">
                Departments
            </a>
            <a href="${pageContext.request.contextPath}/admin/analytics" class="nav-item ${activeNav == 'analytics' ? 'active' : ''}">
                Queue Analytics
            </a>
            <a href="${pageContext.request.contextPath}/admin/settings" class="nav-item ${activeNav == 'settings' ? 'active' : ''}">
                Priority Rules
            </a>
        </c:if>
    </nav>

    <div class="sidebar-footer">
        <div class="user-badge">
            <div class="avatar-initial">
                ${sessionScope.userName != null ? sessionScope.userName.substring(0, 1) : 'U'}
            </div>
            <div class="user-info">
                <div class="user-name">${sessionScope.userName}</div>
                <div class="user-role">${sessionScope.userRole}</div>
            </div>
        </div>
    </div>
</aside>
