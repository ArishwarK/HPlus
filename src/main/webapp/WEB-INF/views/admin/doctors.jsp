<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Manage Medical Staff &amp; Doctors" />
<c:set var="activeNav" value="doctors" />
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
            <c:if test="${not empty param.updated}">
                <div class="alert alert-success">
                    Doctor clinical status updated successfully!
                </div>
            </c:if>

            <div class="card">
                <div class="card-header">
                    <div class="card-title">Doctor List &amp; Duty Controls</div>
                </div>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Doctor Name</th>
                            <th>Specialization</th>
                            <th>Department</th>
                            <th>Room</th>
                            <th>Consultation Fee</th>
                            <th>Status Control</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="doc" items="${doctors}">
                            <tr>
                                <td><strong>Dr. ${doc.doctorName}</strong></td>
                                <td>${doc.specialization} (${doc.qualification})</td>
                                <td>${doc.departmentName}</td>
                                <td>${doc.roomNumber}</td>
                                <td>$${doc.consultationFee}</td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/admin/doctors" method="POST" style="display: flex; gap: 8px;">
                                        <input type="hidden" name="action" value="toggleStatus">
                                        <input type="hidden" name="doctorId" value="${doc.doctorId}">
                                        <select name="status" class="form-control" style="width: auto; padding: 4px 8px; font-size: 12px;" onchange="this.form.submit()">
                                            <option value="AVAILABLE" ${doc.status == 'AVAILABLE' ? 'selected' : ''}>Available</option>
                                            <option value="BUSY" ${doc.status == 'BUSY' ? 'selected' : ''}>Busy</option>
                                            <option value="ON_BREAK" ${doc.status == 'ON_BREAK' ? 'selected' : ''}>On Break</option>
                                            <option value="OFF_DUTY" ${doc.status == 'OFF_DUTY' ? 'selected' : ''}>Off Duty</option>
                                        </select>
                                    </form>
                                </td>
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
