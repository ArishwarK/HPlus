<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Clinical History &amp; Prescriptions" />
<c:set var="activeNav" value="history" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
</head>
<body>
<div class="app-layout">
    <%@ include file="../common/sidebar.jsp" %>

    <div class="main-content">
        <%@ include file="../common/header.jsp" %>

        <div class="content-container">
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Completed Consultations &amp; Medical Notes</div>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Date</th>
                            <th>Doctor &amp; Dept</th>
                            <th>Token #</th>
                            <th>Diagnosis</th>
                            <th>Prescription &amp; Advice</th>
                            <th>Duration</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="c" items="${consultations}">
                            <tr>
                                <td><strong>${c.consultationDate}</strong></td>
                                <td>${c.doctorName} <br><span style="font-size: 11px; color: var(--gray-400);">${c.departmentName}</span></td>
                                <td>Token #${c.tokenNumber}</td>
                                <td>${c.diagnosis}</td>
                                <td style="font-family: var(--font-mono); font-size: 12px; color: var(--primary-700);">${c.prescription}</td>
                                <td>${c.durationMinutes} mins</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty consultations}">
                            <tr>
                                <td colspan="6" class="empty-state">No prior consultations recorded.</td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <%@ include file="../common/footer.jsp" %>
    </div>
</div>
</body>
</html>
