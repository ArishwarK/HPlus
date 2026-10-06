<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="top-bar">
    <div class="page-title-wrap">
        <h1>${pageTitle}</h1>
    </div>
    <nav style="display: flex; gap: 8px; margin-left: 20px;">
        <c:if test="${sessionScope.currentUser.role == 'PATIENT' || sessionScope.currentUser.role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/patient" class="btn btn-sm btn-secondary">Patient Portal</a>
        </c:if>
        <c:if test="${sessionScope.currentUser.role == 'DOCTOR' || sessionScope.currentUser.role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/doctor" class="btn btn-sm btn-secondary">Doctor OPD</a>
        </c:if>
        <c:if test="${sessionScope.currentUser.role == 'RECEPTIONIST' || sessionScope.currentUser.role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/receptionist" class="btn btn-sm btn-secondary">Reception Desk</a>
        </c:if>
        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/admin" class="btn btn-sm btn-secondary">Admin Console</a>
        </c:if>
        <a href="${pageContext.request.contextPath}/announcements" class="btn btn-sm btn-warning">Announcement Board</a>
    </nav>
    <div style="display: flex; align-items: center; gap: 16px; margin-left: auto;">
        <span class="live-indicator-badge">
            <span class="pulse-dot"></span>
            Queue Engine Active
        </span>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm" title="End Session">
            Logout
        </a>
    </div>
</header>
