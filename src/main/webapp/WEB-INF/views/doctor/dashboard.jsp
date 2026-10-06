<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Doctor Consultation Console" />
<c:set var="activeNav" value="dashboard" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - ${doctor.doctorName}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/queue.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
</head>
<body>
<div class="app-layout">
    <%@ include file="../common/sidebar.jsp" %>

    <div class="main-content">
        <%@ include file="../common/header.jsp" %>

        <div class="content-container">
            <c:if test="${not empty param.error}">
                <div class="alert alert-error">
                    ${param.error}
                </div>
            </c:if>
            <c:if test="${not empty param.msg}">
                <div class="alert alert-success">
                    ${param.msg}
                </div>
            </c:if>
            <c:if test="${not empty param.calledToken}">
                <div class="alert alert-success">
                    Successfully called Token <strong>#${param.calledToken}</strong> into consultation.
                </div>
            </c:if>

            <!-- Doctor Header Info & Key Daily Stats -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-info">
                        <div class="stat-label">Currently Serving Token</div>
                        <div class="stat-value" id="docCurrentTokenDisplay">${queueData.currentToken > 0 ? queueData.currentToken : '--'}</div>
                        <div class="stat-sub">Room ${doctor.roomNumber}</div>
                    </div>
                    <div class="stat-icon amber">&#9654;</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <div class="stat-label">Waiting In Queue</div>
                        <div class="stat-value" id="docTotalWaitingDisplay">${queueData.totalWaiting}</div>
                        <div class="stat-sub">Patients outside cabin</div>
                    </div>
                    <div class="stat-icon blue">&#9203;</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <div class="stat-label">Completed Consultations</div>
                        <div class="stat-value" id="docTotalCompletedDisplay">${queueData.totalCompleted}</div>
                        <div class="stat-sub">Treated today</div>
                    </div>
                    <div class="stat-icon green">&#10003;</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <div class="stat-label">Avg Duration</div>
                        <div class="stat-value">${doctor.avgConsultationMinutes}m</div>
                        <div class="stat-sub">Dynamically calculated</div>
                    </div>
                    <div class="stat-icon rose">&#9201;</div>
                </div>
            </div>

            <!-- Doctor Operational Actions Bar -->
            <div style="background: #ffffff; padding: 18px 24px; border-radius: var(--radius-md); border: 1px solid var(--gray-200); margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
                <div>
                    <h3 style="font-size: 16px; font-weight: 700;">Dr. ${doctor.doctorName}</h3>
                    <p style="font-size: 12px; color: var(--gray-500);">${doctor.specialization} &bull; ${doctor.departmentName} &bull; Status: <span class="badge ${doctor.status == 'AVAILABLE' ? 'status-completed' : 'status-called'}">${doctor.status}</span></p>
                </div>

                <div style="display: flex; gap: 10px;">
                    <!-- Call Next Patient Button -->
                    <form action="${pageContext.request.contextPath}/doctor/call-next" method="POST" style="display: inline;">
                        <button type="submit" class="btn btn-primary btn-lg" style="box-shadow: 0 4px 14px rgba(37, 99, 235, 0.4);">
                            &#128226; Call Next Patient
                        </button>
                    </form>
                </div>
            </div>

            <!-- Queue Patient Grid -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Live Patient Queue for Today (${doctor.departmentCode})</div>
                    <span class="live-indicator-badge"><span class="pulse-dot"></span> AJAX Synchronized</span>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Token #</th>
                            <th>Patient Name &amp; UHID</th>
                            <th>Priority Tier</th>
                            <th>Status</th>
                            <th>Arrival Time</th>
                            <th>Wait Est.</th>
                            <th>Action Controls</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="entry" items="${queueData.queueEntries}">
                            <tr class="${entry.status == 'CALLED' || entry.status == 'IN_CONSULTATION' ? 'row-active' : ''}">
                                <td>
                                    <strong style="font-size: 16px; font-family: var(--font-mono); color: var(--primary-700);">${entry.tokenDisplay}</strong>
                                </td>
                                <td>
                                    <strong>${entry.patientName}</strong><br>
                                    <span style="font-size: 11px; color: var(--gray-400);">${entry.patientUhid} &bull; Phone: ${entry.patientPhone}</span>
                                </td>
                                <td>
                                    <span class="badge ${entry.priorityLevel.badgeClass}">${entry.priorityLevel.displayName}</span>
                                    <c:if test="${entry.calculatedPriorityScore > 100.0}">
                                        <span style="font-size: 10px; color: var(--gray-500); display: block;">Score: ${entry.calculatedPriorityScore}</span>
                                    </c:if>
                                </td>
                                <td>
                                    <span class="badge ${entry.status.cssClass}">${entry.status.description}</span>
                                </td>
                                <td>
                                    <span style="font-size: 12px; color: var(--gray-600);">${entry.arrivalTime}</span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${entry.status == 'CALLED' || entry.status == 'IN_CONSULTATION'}">
                                            <span style="color: var(--amber-600); font-weight: 700;">In Cabin</span>
                                        </c:when>
                                        <c:when test="${entry.status == 'COMPLETED'}">
                                            <span style="color: var(--emerald-600);">Finished</span>
                                        </c:when>
                                        <c:otherwise>
                                            ${entry.estimatedWaitMinutes} mins
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div style="display: flex; gap: 6px; align-items: center;">
                                        <!-- Actions based on current status -->
                                        <c:if test="${entry.status == 'CALLED'}">
                                            <form action="${pageContext.request.contextPath}/doctor/start-consultation" method="POST" style="display: inline;">
                                                <input type="hidden" name="queueId" value="${entry.queueId}">
                                                <button type="submit" class="btn btn-primary btn-sm">Begin Exam</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/doctor/skip-patient" method="POST" style="display: inline;">
                                                <input type="hidden" name="queueId" value="${entry.queueId}">
                                                <button type="submit" class="btn btn-warning btn-sm" onclick="return confirm('Skip this patient token?')">Skip</button>
                                            </form>
                                        </c:if>

                                        <c:if test="${entry.status == 'IN_CONSULTATION'}">
                                            <button type="button" class="btn btn-success btn-sm" onclick="openCompleteModal(${entry.queueId}, '${entry.tokenDisplay}', '${entry.patientName}')">
                                                Complete &amp; Prescribe
                                            </button>
                                        </c:if>

                                        <c:if test="${entry.status == 'SKIPPED'}">
                                            <form action="${pageContext.request.contextPath}/doctor/recall-patient" method="POST" style="display: inline;">
                                                <input type="hidden" name="queueId" value="${entry.queueId}">
                                                <button type="submit" class="btn btn-secondary btn-sm">Recall</button>
                                            </form>
                                        </c:if>

                                        <c:if test="${entry.status == 'COMPLETED'}">
                                            <span style="font-size: 12px; color: var(--emerald-600);">&#10003; Done</span>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty queueData.queueEntries}">
                            <tr>
                                <td colspan="7" class="empty-state">No patients queued for today yet.</td>
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

<!-- Modal Dialog for Completing Consultation -->
<div id="completeModal" class="modal-overlay">
    <div class="modal-dialog">
        <form action="${pageContext.request.contextPath}/doctor/complete-consultation" method="POST">
            <input type="hidden" id="modalQueueId" name="queueId" value="">
            <div class="modal-header">
                <div class="modal-title">Complete Consultation: <span id="modalPatientInfo"></span></div>
                <button type="button" class="modal-close-btn" onclick="closeModal('completeModal')">&times;</button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label" for="chiefComplaints">Chief Complaints</label>
                    <input type="text" id="chiefComplaints" name="chiefComplaints" class="form-control" placeholder="e.g. Chest tightness, fatigue...">
                </div>
                <div class="form-group">
                    <label class="form-label" for="diagnosis">Clinical Diagnosis <span class="required">*</span></label>
                    <input type="text" id="diagnosis" name="diagnosis" class="form-control" required placeholder="e.g. Mild Hypertension, Clear ECG">
                </div>
                <div class="form-group">
                    <label class="form-label" for="prescription">Rx Prescription &amp; Orders <span class="required">*</span></label>
                    <textarea id="prescription" name="prescription" class="form-control" required placeholder="e.g. Tab. Amlodipine 5mg OD, low sodium diet"></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeModal('completeModal')">Cancel</button>
                <button type="submit" class="btn btn-success">Save Record &amp; Mark Completed</button>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/queue.js"></script>
<script>
    function openCompleteModal(queueId, tokenDisplay, patientName) {
        document.getElementById("modalQueueId").value = queueId;
        document.getElementById("modalPatientInfo").textContent = tokenDisplay + " - " + patientName;
        openModal("completeModal");
    }

    document.addEventListener("DOMContentLoaded", function () {
        new QueueMonitor({
            contextPath: "${pageContext.request.contextPath}",
            doctorId: ${doctor.doctorId},
            pollInterval: 5000
        });
    });
</script>
</body>
</html>
