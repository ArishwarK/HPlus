<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Reception Desk &amp; Token Dispatcher" />
<c:set var="activeNav" value="dashboard" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">
</head>
<body>
<div class="app-layout">
    <%@ include file="../common/sidebar.jsp" %>

    <div class="main-content">
        <%@ include file="../common/header.jsp" %>

        <div class="content-container">
            <c:if test="${not empty param.token}">
                <div class="alert alert-success">
                    Token Generated: <strong style="font-size: 16px;">#${param.token}</strong> (UHID: ${param.uhid})
                </div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-error">
                    ${param.error}
                </div>
            </c:if>

            <!-- Quick Stats -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-info">
                        <div class="stat-label">Total Registered Patients</div>
                        <div class="stat-value">${totalPatients}</div>
                        <div class="stat-sub">Hospital database</div>
                    </div>
                    <div class="stat-icon blue">&#128101;</div>
                </div>
                <div class="stat-card">
                    <div class="stat-info">
                        <div class="stat-label">Today's Scheduled Appointments</div>
                        <div class="stat-value">${todayAppointments}</div>
                        <div class="stat-sub">Across all departments</div>
                    </div>
                    <div class="stat-icon green">&#128197;</div>
                </div>
                <div class="stat-card">
                    <div class="stat-info">
                        <div class="stat-label">Active Doctors on Duty</div>
                        <div class="stat-value">${doctors.size()}</div>
                        <div class="stat-sub">Consulting cabins</div>
                    </div>
                    <div class="stat-icon amber">&#9877;</div>
                </div>
            </div>

            <!-- Two-Column Workflow Layout: Quick Walk-In Registration vs Active Doctor Queues -->
            <div class="dashboard-grid">
                <!-- Column 1: Walk-In Registration & Token Generation Desk -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">Front Desk: Walk-in Patient &amp; Instant Token</div>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/receptionist/walkin" method="POST">
                            <div class="form-grid-2">
                                <div class="form-group">
                                    <label class="form-label" for="fullName">Patient Full Name <span class="required">*</span></label>
                                    <input type="text" id="fullName" name="fullName" class="form-control" required placeholder="e.g. Robert Chang">
                                </div>
                                <div class="form-group">
                                    <label class="form-label" for="phoneNumber">Contact Phone <span class="required">*</span></label>
                                    <input type="text" id="phoneNumber" name="phoneNumber" class="form-control" required placeholder="+1-555-0199">
                                </div>
                            </div>

                            <div class="form-grid-2">
                                <div class="form-group">
                                    <label class="form-label" for="gender">Gender <span class="required">*</span></label>
                                    <select id="gender" name="gender" class="form-control" required>
                                        <option value="MALE">Male</option>
                                        <option value="FEMALE">Female</option>
                                        <option value="OTHER">Other</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label class="form-label" for="dob">Date of Birth</label>
                                    <input type="date" id="dob" name="dob" class="form-control" value="1985-05-15">
                                </div>
                            </div>

                            <div class="form-grid-2">
                                <div class="form-group">
                                    <label class="form-label" for="doctorId">Assign to Doctor <span class="required">*</span></label>
                                    <select id="doctorId" name="doctorId" class="form-control" required>
                                        <c:forEach var="doc" items="${doctors}">
                                            <option value="${doc.doctorId}">Dr. ${doc.doctorName} (${doc.departmentName} - Room ${doc.roomNumber})</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label class="form-label" for="priorityLevel">Triage Priority</label>
                                    <select id="priorityLevel" name="priorityLevel" class="form-control">
                                        <option value="NORMAL">Normal Priority</option>
                                        <option value="HIGH">High Priority (Urgent)</option>
                                        <option value="EMERGENCY">Emergency (Immediate)</option>
                                    </select>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="address">Address / Notes</label>
                                <input type="text" id="address" name="address" class="form-control" placeholder="Street address or initial complaints">
                            </div>

                            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 12px;">
                                Register Walk-In &amp; Issue Live Token
                            </button>
                        </form>
                    </div>
                </div>

                <!-- Column 2: Doctor Queues at a Glance -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">Doctor Queues &amp; Room Status</div>
                    </div>
                    <div class="card-body" style="padding: 0;">
                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                <tr>
                                    <th>Doctor &amp; Room</th>
                                    <th>Serving</th>
                                    <th>Waiting</th>
                                    <th>Status</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="doc" items="${doctors}">
                                    <tr>
                                        <td>
                                            <strong>Dr. ${doc.doctorName}</strong><br>
                                            <span style="font-size: 11px; color: var(--gray-400);">${doc.departmentName} &bull; Room ${doc.roomNumber}</span>
                                        </td>
                                        <td>
                                            <strong style="color: var(--primary-700); font-family: var(--font-mono); font-size: 14px;">
                                                ${doc.currentServingToken > 0 ? doc.currentServingToken : '--'}
                                            </strong>
                                        </td>
                                        <td>
                                            <span class="badge ${doc.activeQueueCount > 3 ? 'badge-high' : 'badge-normal'}">
                                                ${doc.activeQueueCount} ahead
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge ${doc.status == 'AVAILABLE' ? 'status-completed' : 'status-called'}">
                                                ${doc.status}
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <%@ include file="../common/footer.jsp" %>
    </div>
</div>
</body>
</html>
