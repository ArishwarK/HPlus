<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Queue Priority Engine Settings" />
<c:set var="activeNav" value="settings" />
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
            <c:if test="${not empty param.saved}">
                <div class="alert alert-success">
                    Algorithm parameters updated successfully!
                </div>
            </c:if>

            <div class="card">
                <div class="card-header">
                    <div class="card-title">Priority &amp; Anti-Starvation Fairness Configuration</div>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/admin/settings" method="POST">
                        <div class="form-group">
                            <label class="form-label" for="emergencyWeight">Emergency Baseline Score</label>
                            <input type="number" id="emergencyWeight" name="emergencyWeight" class="form-control" value="300" readonly>
                            <div class="form-helper">Immediate attention; bypassing normal queue while preserving logged order.</div>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="highWeight">High Priority Baseline Score</label>
                            <input type="number" id="highWeight" name="highWeight" class="form-control" value="180">
                            <div class="form-helper">Acute symptoms and urgent referrals.</div>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="normalWeight">Normal Priority Baseline Score</label>
                            <input type="number" id="normalWeight" name="normalWeight" class="form-control" value="100">
                            <div class="form-helper">Standard outpatient appointments and routine visits.</div>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="agingRate">Fairness Aging Boost (Points per minute waited)</label>
                            <input type="number" step="0.1" id="agingRate" name="agingRate" class="form-control" value="1.5">
                            <div class="form-helper">Guarantees that a normal patient waiting >35 minutes surpasses newly arrived high-priority walk-ins, eliminating indefinite starvation.</div>
                        </div>

                        <button type="submit" class="btn btn-primary" style="margin-top: 12px;">Save Algorithm Parameters</button>
                    </form>
                </div>
            </div>
        </div>

        <%@ include file="../common/footer.jsp" %>
    </div>
</div>
</body>
</html>
