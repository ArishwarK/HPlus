<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Queue Analytics &amp; Audit Logs" />
<c:set var="activeNav" value="analytics" />
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
            <!-- Doctor Daily Queue Breakdown -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Doctor Queue Throughput &amp; Waiting Load</div>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Doctor</th>
                            <th>Department</th>
                            <th>Currently Waiting</th>
                            <th>Completed Today</th>
                            <th>Recorded Avg Duration</th>
                            <th>Throughput Efficiency</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="doc" items="${doctors}">
                            <tr>
                                <td><strong>Dr. ${doc.doctorName}</strong></td>
                                <td>${doc.departmentName}</td>
                                <td><span class="badge ${doc.activeQueueCount > 3 ? 'badge-high' : 'badge-normal'}">${doc.activeQueueCount} patients</span></td>
                                <td><span class="badge status-completed">${doc.completedTodayCount} finished</span></td>
                                <td>${doc.avgConsultationMinutes} minutes</td>
                                <td>
                                    <div style="background: var(--gray-200); border-radius: var(--radius-full); height: 8px; width: 120px; overflow: hidden;">
                                        <div style="background: var(--primary-600); height: 100%; width: ${doc.completedTodayCount * 15 > 100 ? 100 : doc.completedTodayCount * 15}%;"></div>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Audit Trail Table -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">System Audit Trail &amp; Transaction Logs</div>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Timestamp</th>
                            <th>Operator</th>
                            <th>Action</th>
                            <th>Event Details</th>
                            <th>Source IP</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="log" items="${recentLogs}">
                            <tr>
                                <td>${log.timestamp}</td>
                                <td>${log.username != null ? log.username : 'SYSTEM'}</td>
                                <td><span class="badge badge-normal">${log.action}</span></td>
                                <td>${log.details}</td>
                                <td style="font-family: var(--font-mono); font-size: 11px;">${log.ipAddress}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty recentLogs}">
                            <tr>
                                <td colspan="5" class="empty-state">No audit logs recorded yet.</td>
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
