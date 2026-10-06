<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Book Doctor Consultation" />
<c:set var="activeNav" value="book" />
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

        <div class="content-container" style="max-width: 800px;">
            <div class="card">
                <div class="card-header">
                    <div class="card-title">Schedule a Consultation</div>
                </div>
                <div class="card-body">
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-error">
                            ${errorMessage}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/patient/book-appointment" method="POST" onsubmit="return validateBookingForm(this)">
                        <div class="form-grid-2">
                            <div class="form-group">
                                <label class="form-label" for="departmentId">Department <span class="required">*</span></label>
                                <select id="departmentId" name="departmentId" class="form-control" 
                                        onchange="onDepartmentChange(this, 'doctorId', '${pageContext.request.contextPath}')" required>
                                    <option value="">-- Choose Department --</option>
                                    <c:forEach var="dept" items="${departments}">
                                        <option value="${dept.departmentId}">${dept.name} (${dept.code}) - ${dept.locationBuilding}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="doctorId">Attending Doctor <span class="required">*</span></label>
                                <select id="doctorId" name="doctorId" class="form-control" required>
                                    <option value="">-- First Select a Department --</option>
                                    <c:forEach var="doc" items="${doctors}">
                                        <option value="${doc.doctorId}">Dr. ${doc.doctorName} (${doc.specialization}) - Room ${doc.roomNumber}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-grid-2">
                            <div class="form-group">
                                <label class="form-label" for="appointmentDate">Appointment Date <span class="required">*</span></label>
                                <input type="date" id="appointmentDate" name="appointmentDate" class="form-control" required>
                            </div>

                            <div class="form-group">
                                <label class="form-label" for="appointmentTime">Preferred Time Slot <span class="required">*</span></label>
                                <select id="appointmentTime" name="appointmentTime" class="form-control" required>
                                    <option value="09:00">09:00 AM</option>
                                    <option value="09:30">09:30 AM</option>
                                    <option value="10:00">10:00 AM</option>
                                    <option value="10:30">10:30 AM</option>
                                    <option value="11:00">11:00 AM</option>
                                    <option value="11:30">11:30 AM</option>
                                    <option value="12:00">12:00 PM</option>
                                    <option value="14:00">02:00 PM</option>
                                    <option value="14:30">02:30 PM</option>
                                    <option value="15:00">03:00 PM</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="priorityLevel">Clinical Priority</label>
                            <select id="priorityLevel" name="priorityLevel" class="form-control">
                                <option value="NORMAL">Normal Priority (Routine Checkup)</option>
                                <option value="HIGH">High Priority (Urgent / Acute Discomfort)</option>
                                <option value="EMERGENCY">Emergency (Critical Immediate Triage)</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="symptoms">Chief Complaints / Presenting Symptoms</label>
                            <textarea id="symptoms" name="symptoms" class="form-control" placeholder="Describe symptoms or reasons for visit..."></textarea>
                        </div>

                        <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px;">
                            <a href="${pageContext.request.contextPath}/patient/dashboard" class="btn btn-secondary">Cancel</a>
                            <button type="submit" class="btn btn-primary">Confirm Appointment</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <%@ include file="../common/footer.jsp" %>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/appointment.js"></script>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        // Set minimum date to today
        const todayStr = new Date().toISOString().split("T")[0];
        document.getElementById("appointmentDate").min = todayStr;
        document.getElementById("appointmentDate").value = todayStr;
    });
</script>
</body>
</html>
