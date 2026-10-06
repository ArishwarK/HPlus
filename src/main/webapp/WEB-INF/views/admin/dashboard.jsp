<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Executive Hospital Dashboard" />
<c:set var="activeNav" value="dashboard" />
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
            <!-- Executive Metrics -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-info">
                        <div class="stat-label">Total Registered Patients</div>
                        <div class="stat-value">${totalPatients}</div>
                        <div class="stat-sub">Central database records</div>
                    </div>
                    <div class="stat-icon blue">&#128101;</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <div class="stat-label">Active Doctors on Duty</div>
                        <div class="stat-value">${activeDoctorsCount}</div>
                        <div class="stat-sub">Out of ${doctors.size()} total doctors</div>
                    </div>
                    <div class="stat-icon green">&#9877;</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <div class="stat-label">Today's Appointments</div>
                        <div class="stat-value">${todayAppointments}</div>
                        <div class="stat-sub">Scheduled &amp; walk-in slots</div>
                    </div>
                    <div class="stat-icon amber">&#128197;</div>
                </div>

                <div class="stat-card">
                    <div class="stat-info">
                        <div class="stat-label">Clinical Departments</div>
                        <div class="stat-value">${departments.size()}</div>
                        <div class="stat-sub">Operational OPD units</div>
                    </div>
                    <div class="stat-icon rose">&#127973;</div>
                </div>
            </div>

            <!-- Active Doctors Roster Table -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Medical Staff &amp; Room Assignment Roster</div>
                    <a href="${pageContext.request.contextPath}/admin/doctors" class="btn btn-secondary btn-sm">Manage Doctors</a>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Doctor</th>
                            <th>Department</th>
                            <th>Specialization</th>
                            <th>Cabin / Room</th>
                            <th>Avg Consultation</th>
                            <th>Duty Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="doc" items="${doctors}">
                            <tr>
                                <td>
                                    <strong>Dr. ${doc.doctorName}</strong><br>
                                    <span style="font-size: 11px; color: var(--gray-400);">${doc.email}</span>
                                </td>
                                <td>${doc.departmentName} (${doc.departmentCode})</td>
                                <td>${doc.specialization}</td>
                                <td>Room ${doc.roomNumber}</td>
                                <td>${doc.avgConsultationMinutes} minutes</td>
                                <td>
                                    <span class="badge ${doc.status == 'AVAILABLE' ? 'status-completed' : (doc.status == 'BUSY' ? 'status-called' : 'status-waiting')}">
                                        ${doc.status}
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Clinical Departments Table -->
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Hospital Departments &amp; Waiting Benchmarks</div>
                    <a href="${pageContext.request.contextPath}/admin/departments" class="btn btn-secondary btn-sm">+ Add Department</a>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Department</th>
                            <th>Code</th>
                            <th>Building &amp; Floor</th>
                            <th>Default Consultation Time</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="dept" items="${departments}">
                            <tr>
                                <td><strong>${dept.name}</strong><br><span style="font-size: 11px; color: var(--gray-400);">${dept.description}</span></td>
                                <td><span class="badge badge-normal">${dept.code}</span></td>
                                <td>${dept.locationBuilding}, Floor ${dept.locationFloor}</td>
                                <td>${dept.defaultAvgConsultationTime} minutes</td>
                                <td><span class="badge status-completed">Active</span></td>
                            </tr>
                        </c:forEach>
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
