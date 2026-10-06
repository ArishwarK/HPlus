<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Manage Departments" />
<c:set var="activeNav" value="departments" />
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
            <c:if test="${not empty param.created}">
                <div class="alert alert-success">
                    Department successfully created!
                </div>
            </c:if>

            <div class="dashboard-grid">
                <!-- Left: List -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">Active Clinical Departments</div>
                    </div>
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                            <tr>
                                <th>Name &amp; Code</th>
                                <th>Location</th>
                                <th>Default Avg Time</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="dept" items="${departments}">
                                <tr>
                                    <td><strong>${dept.name}</strong> (${dept.code})</td>
                                    <td>${dept.locationBuilding}, Floor ${dept.locationFloor}</td>
                                    <td>${dept.defaultAvgConsultationTime} mins</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Right: Create Form -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">Add New Department</div>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/admin/departments" method="POST">
                            <div class="form-group">
                                <label class="form-label" for="name">Department Name <span class="required">*</span></label>
                                <input type="text" id="name" name="name" class="form-control" required placeholder="e.g. Oncology">
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="code">Prefix Code (Max 5 chars) <span class="required">*</span></label>
                                <input type="text" id="code" name="code" class="form-control" required placeholder="e.g. ONCO" maxlength="5">
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="locationBuilding">Building</label>
                                <input type="text" id="locationBuilding" name="locationBuilding" class="form-control" value="Wing A - Main Block">
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="locationFloor">Floor</label>
                                <input type="number" id="locationFloor" name="locationFloor" class="form-control" value="2">
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="defaultAvgConsultationTime">Avg Consultation (Minutes)</label>
                                <input type="number" id="defaultAvgConsultationTime" name="defaultAvgConsultationTime" class="form-control" value="10">
                            </div>
                            <div class="form-group">
                                <label class="form-label" for="description">Description</label>
                                <textarea id="description" name="description" class="form-control"></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary" style="width: 100%;">Create Department</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <%@ include file="../common/footer.jsp" %>
    </div>
</div>
</body>
</html>
