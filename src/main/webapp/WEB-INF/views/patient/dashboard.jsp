<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Patient Portal - Overview" />
<c:set var="activeNav" value="dashboard" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/queue.css">
</head>
<body>
<div class="app-layout">
    <%@ include file="../common/sidebar.jsp" %>

    <div class="main-content">
        <%@ include file="../common/header.jsp" %>

        <div class="content-container">
            <c:if test="${not empty param.booked}">
                <div class="alert alert-success">
                    Appointment successfully booked! Your reference: <strong>${param.apptNo}</strong>
                </div>
            </c:if>

            <!-- Patient Demographic Banner -->
            <div style="background: #fff; padding: 20px 24px; border-radius: var(--radius-md); border: 1px solid var(--gray-200); margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center;">
                <div>
                    <h2 style="font-size: 20px; font-weight: 700; color: var(--gray-900);">Welcome, ${patient.fullName}</h2>
                    <p style="font-size: 13px; color: var(--gray-500); margin-top: 2px;">
                        Unique Hospital ID: <strong style="color: var(--primary-600);">${patient.uhid}</strong> &bull; Blood Group: <strong>${patient.bloodGroup}</strong>
                    </p>
                </div>
                <a href="${pageContext.request.contextPath}/patient/book-appointment" class="btn btn-primary">
                    + Book New Appointment
                </a>
            </div>

            <!-- Active Queue Widget if Token is active -->
            <c:if test="${queueStatus != null && queueStatus.success}">
                <div class="token-hero-card">
                    <div class="token-hero-header">
                        <div class="token-hero-doctor">
                            <h2>Live Queue Status &bull; ${queueStatus.departmentName}</h2>
                            <p>Attending: <strong>${queueStatus.doctorName}</strong> &bull; Room <strong>${queueStatus.roomNumber}</strong></p>
                        </div>
                        <span id="queueStatusBadge" class="badge status-${queueStatus.queueStatus.toLowerCase()}">
                            ${queueStatus.queueStatus}
                        </span>
                    </div>

                    <div class="token-showcase-grid">
                        <div class="token-box active-serving">
                            <div class="token-box-label">Doctor Currently Serving</div>
                            <div class="token-box-number" id="currentTokenDisplay">${queueStatus.currentToken > 0 ? queueStatus.currentToken : '--'}</div>
                            <div class="token-box-meta">Inside Consultation Room</div>
                        </div>

                        <div class="token-box">
                            <div class="token-box-label">Your Allocated Token</div>
                            <div class="token-box-number" id="yourTokenDisplay">${queueStatus.yourToken}</div>
                            <div class="token-box-meta">Please wait in waiting area</div>
                        </div>
                    </div>

                    <div class="live-estimate-strip">
                        <div class="estimate-item">
                            <span class="estimate-label">Patients Ahead</span>
                            <span class="estimate-val" id="patientsAheadDisplay">${queueStatus.patientsAhead}</span>
                        </div>
                        <div class="estimate-item">
                            <span class="estimate-label">Estimated Waiting Time</span>
                            <span class="estimate-val highlight" id="estimatedWaitDisplay">${queueStatus.estimatedWaitMinutes} mins</span>
                        </div>
                        <div class="estimate-item">
                            <span class="estimate-label">Doctor Status</span>
                            <span class="estimate-val" style="font-size: 16px;">${queueStatus.doctorStatus}</span>
                        </div>
                    </div>
                </div>
            </c:if>

            <c:if test="${queueStatus == null || !queueStatus.success}">
                <div class="card">
                    <div class="card-body" style="text-align: center; padding: 40px;">
                        <h3 style="font-size: 16px; font-weight: 600; color: var(--gray-700); margin-bottom: 8px;">No Active Queue Token Today</h3>
                        <p style="font-size: 13px; color: var(--gray-400); margin-bottom: 20px;">Book an appointment or visit the front desk to join today's consultation queue.</p>
                        <a href="${pageContext.request.contextPath}/patient/book-appointment" class="btn btn-primary">Book Consultation</a>
                    </div>
                </div>
            </c:if>

            <!-- Recent Appointments Table -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">My Scheduled Appointments</div>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Appt #</th>
                            <th>Date &amp; Time</th>
                            <th>Doctor</th>
                            <th>Department</th>
                            <th>Priority</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="appt" items="${recentAppointments}">
                            <tr>
                                <td><strong>${appt.appointmentNumber}</strong></td>
                                <td>${appt.appointmentDate} at ${appt.appointmentTime}</td>
                                <td>${appt.doctorName}</td>
                                <td>${appt.departmentName} (Room ${appt.roomNumber})</td>
                                <td><span class="badge ${appt.priorityLevel.badgeClass}">${appt.priorityLevel.displayName}</span></td>
                                <td><span class="badge status-${appt.status.name().toLowerCase()}">${appt.status}</span></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty recentAppointments}">
                            <tr>
                                <td colspan="6" class="empty-state">No appointments recorded yet.</td>
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

<script src="${pageContext.request.contextPath}/js/queue.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        new QueueMonitor({
            contextPath: "${pageContext.request.contextPath}",
            patientId: ${patient.patientId},
            pollInterval: 5000
        });
    });
</script>
</body>
</html>
