<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Live Token Queue &amp; Waiting Time" />
<c:set var="activeNav" value="queue" />
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
            <!-- Patient Called Alert Banner -->
            <div id="patientCalledBanner" style="display: none; background: #fef08a; border: 2px solid #eab308; padding: 20px 24px; border-radius: var(--radius-md); margin-bottom: 24px; box-shadow: 0 4px 15px rgba(234, 179, 8, 0.3);">
                <div style="display: flex; align-items: center; justify-content: space-between;">
                    <div>
                        <h2 style="font-size: 20px; font-weight: 800; color: #854d0e;">IT IS YOUR TURN!</h2>
                        <p style="font-size: 14px; color: #a16207; margin-top: 2px;">Your token has been called. Please proceed directly to Room <strong id="roomNumberDisplay">${queueData.roomNumber}</strong>.</p>
                    </div>
                    <span class="badge badge-emergency" style="font-size: 14px; padding: 6px 12px;">NOW CALLING</span>
                </div>
            </div>

            <!-- Hero Live Token Board -->
            <div class="token-hero-card">
                <div class="token-hero-header">
                    <div class="token-hero-doctor">
                        <h2>Live Queue Display &bull; ${queueData.departmentName != null ? queueData.departmentName : 'Cardiology'}</h2>
                        <p>Doctor: <strong>${queueData.doctorName != null ? queueData.doctorName : 'Dr. Kumar'}</strong> &bull; Room <strong>${queueData.roomNumber != null ? queueData.roomNumber : 'OPD-204'}</strong></p>
                    </div>
                    <span id="queueStatusBadge" class="badge status-${queueData.queueStatus != null ? queueData.queueStatus.toLowerCase() : 'waiting'}">
                        ${queueData.queueStatus != null ? queueData.queueStatus : 'WAITING'}
                    </span>
                </div>

                <div class="token-showcase-grid">
                    <div class="token-box active-serving">
                        <div class="token-box-label">Current Serving Token</div>
                        <div class="token-box-number" id="currentTokenDisplay">${queueData.currentToken > 0 ? queueData.currentToken : '--'}</div>
                        <div class="token-box-meta">Inside Consulting Room</div>
                    </div>

                    <div class="token-box">
                        <div class="token-box-label">Your Queue Token</div>
                        <div class="token-box-number" id="yourTokenDisplay">${queueData.yourToken != null ? queueData.yourToken : '--'}</div>
                        <div class="token-box-meta">Assigned Patient Token</div>
                    </div>
                </div>

                <div class="live-estimate-strip">
                    <div class="estimate-item">
                        <span class="estimate-label">Patients Ahead</span>
                        <span class="estimate-val" id="patientsAheadDisplay">${queueData.patientsAhead}</span>
                    </div>
                    <div class="estimate-item">
                        <span class="estimate-label">Estimated Wait Time</span>
                        <span class="estimate-val highlight" id="estimatedWaitDisplay">${queueData.estimatedWaitMinutes} mins</span>
                    </div>
                    <div class="estimate-item">
                        <span class="estimate-label">Total Waiting In Queue</span>
                        <span class="estimate-val" id="totalWaitingDisplay">${queueData.totalWaiting}</span>
                    </div>
                    <div class="estimate-item">
                        <span class="estimate-label">Consultations Finished</span>
                        <span class="estimate-val" id="totalCompletedDisplay">${queueData.totalCompleted}</span>
                    </div>
                </div>
            </div>

            <!-- Algorithm Explanation & Technical Verification Card -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Transparent Waiting-Time Estimation Engine</div>
                    <span class="live-indicator-badge"><span class="pulse-dot"></span> Polling every 5s via AJAX</span>
                </div>
                <div class="card-body">
                    <p style="font-size: 13px; color: var(--gray-600); margin-bottom: 12px;">
                        The hospital queue engine computes waiting times using real-time factors:
                    </p>
                    <div style="background: var(--gray-50); border: 1px solid var(--gray-200); padding: 14px 18px; border-radius: var(--radius-sm); font-family: var(--font-mono); font-size: 12px; color: var(--gray-800); line-height: 1.6;">
                        EstimatedWait = (PatientsAhead &times; DoctorAvgConsultationTime) + Max(1, AvgDuration - ElapsedMinutesCurrentPatient)<br>
                        Anti-Starvation Aging: Normal priority tokens gain priority score boost of 1.5 pts/min to guarantee fair progression.
                    </div>
                </div>
            </div>
        </div>

        <%@ include file="../common/footer.jsp" %>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/queue.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        // Automatically start real-time AJAX polling
        new QueueMonitor({
            contextPath: "${pageContext.request.contextPath}",
            patientId: ${patient.patientId},
            pollInterval: 5000
        });
    });
</script>
</body>
</html>
