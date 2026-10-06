<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>500 - System Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
</head>
<body style="display: flex; align-items: center; justify-content: center; min-height: 100vh; background: #f8fafc;">
    <div style="text-align: center; max-width: 520px; padding: 40px; background: #fff; border-radius: var(--radius-lg); border: 1px solid var(--gray-200); box-shadow: var(--shadow-md);">
        <h1 style="font-size: 64px; color: var(--amber-600); font-weight: 800; line-height: 1;">500</h1>
        <h2 style="font-size: 18px; color: var(--gray-800); margin: 12px 0 8px;">Internal Server Notice</h2>
        <p style="font-size: 13px; color: var(--gray-500); margin-bottom: 24px;">An internal server issue occurred while processing your request. Database transactions have been safely rolled back.</p>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Return to Portal</a>
    </div>
</body>
</html>
