<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Live OPD Queue &amp; Hospital Announcement Board</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/queue.css">
</head>
<body class="announcement-tv-body">
<div class="announcement-container">
    <!-- Header with Digital Clock & Hospital Info -->
    <header class="tv-header">
        <div class="tv-brand">
            <div class="brand-badge">H+</div>
            <div>
                <h1>City General Hospital &bull; Central Outpatient Waiting Lounge</h1>
                <p>Real-Time Patient Calling &amp; Operational Bulletins</p>
            </div>
        </div>
        <div class="tv-clock-panel">
            <div id="liveClock" class="digital-clock">--:--:--</div>
            <div class="status-indicator">
                <span class="live-pulse"></span>
                <span>LIVE BROADCAST</span>
            </div>
        </div>
    </header>

    <!-- Main Live Screen: Now Serving & Announcements -->
    <div class="tv-main-grid">
        <!-- Now Serving Hero -->
        <div class="now-serving-banner">
            <div class="banner-title">NOW SERVING IN CONSULTATION</div>
            <div class="banner-token" id="activeTokenDisplay">CARD-42</div>
            <div class="banner-details">
                <span class="banner-patient">Patient: Maria Santos</span> &bull; 
                <span class="banner-room">Room: OPD-204</span> &bull; 
                <span class="banner-doctor">Dr. Rajesh Kumar (Cardiology)</span>
            </div>
        </div>

        <!-- Cabin Matrix -->
        <div class="cabin-grid">
            <div class="cabin-card busy">
                <div class="cabin-head">
                    <span class="cabin-room">Room OPD-204</span>
                    <span class="cabin-badge badge-busy">CALLING</span>
                </div>
                <h4>Dr. Rajesh Kumar</h4>
                <div class="cabin-dept">Cardiology</div>
                <div class="cabin-token">Serving: <strong>CARD-42</strong></div>
            </div>
            <div class="cabin-card available">
                <div class="cabin-head">
                    <span class="cabin-room">Room OPD-112</span>
                    <span class="cabin-badge badge-available">AVAILABLE</span>
                </div>
                <h4>Dr. Elena Rostova</h4>
                <div class="cabin-dept">Orthopedics</div>
                <div class="cabin-token">Next Token: <strong>Ready</strong></div>
            </div>
            <div class="cabin-card available">
                <div class="cabin-head">
                    <span class="cabin-room">Room OPD-305</span>
                    <span class="cabin-badge badge-available">AVAILABLE</span>
                </div>
                <h4>Dr. Marcus Vance</h4>
                <div class="cabin-dept">Pediatrics</div>
                <div class="cabin-token">Next Token: <strong>Ready</strong></div>
            </div>
            <div class="cabin-card available">
                <div class="cabin-head">
                    <span class="cabin-room">Room OPD-101</span>
                    <span class="cabin-badge badge-available">AVAILABLE</span>
                </div>
                <h4>Dr. Aisha Patel</h4>
                <div class="cabin-dept">General Medicine</div>
                <div class="cabin-token">Next Token: <strong>Ready</strong></div>
            </div>
        </div>

        <!-- Public Announcements & Advisories Feed -->
        <div class="announcements-feed">
            <h3>Hospital Public Announcements &amp; Advisories</h3>
            <div class="announcement-list">
                <div class="announcement-item urgent">
                    <span class="tag-urgent">EMERGENCY NOTICE</span>
                    <h4>Emergency Triage Protocol In Effect</h4>
                    <p>Patients with severe chest pain or trauma are prioritized immediately as per National Health Protocols.</p>
                </div>
                <div class="announcement-item info">
                    <span class="tag-info">OPD UPDATE</span>
                    <h4>Wheelchair &amp; Senior Citizen Fast-Track</h4>
                    <p>Assistance and wheelchair escorts are available at Counter 2 (Ground Floor Central Wing).</p>
                </div>
                <div class="announcement-item clinical">
                    <span class="tag-clinical">PHARMACY ADVISORY</span>
                    <h4>Prescription Dispensing at Counter 4</h4>
                    <p>Once consultation is completed, proceed to Pharmacy Counter 4 with your token number.</p>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/static/js/queue-ajax.js"></script>
</body>
</html>
