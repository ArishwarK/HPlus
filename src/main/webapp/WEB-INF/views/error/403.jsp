<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>403 - Access Denied</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
</head>
<body style="display: flex; align-items: center; justify-content: center; min-height: 100vh; background: #f8fafc;">
    <div style="text-align: center; max-width: 480px; padding: 40px; background: #fff; border-radius: var(--radius-lg); border: 1px solid var(--gray-200); box-shadow: var(--shadow-md);">
        <h1 style="font-size: 64px; color: var(--rose-600); font-weight: 800; line-height: 1;">403</h1>
        <h2 style="font-size: 18px; color: var(--gray-800); margin: 12px 0 8px;">Access Forbidden</h2>
        <p style="font-size: 13px; color: var(--gray-500); margin-bottom: 24px;">Your user account does not possess role authorization for this department portal.</p>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">Switch User / Login</a>
    </div>
</body>
</html>
