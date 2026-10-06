# Smart Hospital Queue Management and Waiting-Time Estimation System

**Academic Capstone / Enterprise College Web Project**  
**Technology Architecture**: Java (JDK 17) &bull; Jakarta Servlets 6.0 &bull; JSP 3.1 &bull; JDBC &bull; MySQL 8.x &bull; Apache Tomcat 10.1+ &bull; Maven &bull; Vanilla JavaScript (Fetch API / AJAX) &bull; Pure CSS3

---

## 1. Problem Statement & Objectives

Modern outpatient departments (OPDs) in hospitals suffer from unpredictable physical crowding, lack of transparency in token issuance, patient anxiety due to unknown waiting times, and unfair triage where high-priority or walk-in arrivals starve routine appointments.

This system delivers a **real-time, transparent, algorithmic hospital queue management platform** engineered using core Java enterprise web standards (Jakarta Servlets, JSP, JDBC, and asynchronous AJAX).

### Key Objectives:
1. **Dynamic Token Engine**: Decoupled, doctor-specific independent daily queues with deterministic progression.
2. **Transparent Algorithmic Waiting-Time Estimation**: Dedicated mathematical estimation incorporating patients ahead, doctor's historical average duration, elapsed consultation time, and priority weighting.
3. **Fair Priority Scheduling with Anti-Starvation Aging**: Supports `EMERGENCY`, `HIGH`, and `NORMAL` tiers, while dynamically boosting the priority score of long-waiting normal patients to eliminate starvation.
4. **Zero-Refresh Real-Time AJAX Updates**: Periodic 5-second polling via native `fetch()` ensuring seamless DOM updates without page reloads.
5. **Role-Based Access Control (RBAC)**: Segregated portals for `ADMIN`, `DOCTOR`, `RECEPTIONIST`, and `PATIENT`.

---

## 2. Technology Stack & Strict Architectural Boundaries

| Layer | Technology | Specification / Version |
| :--- | :--- | :--- |
| **Presentation (Frontend)** | HTML5, Pure CSS3 | Custom responsive CSS (no Bootstrap, no Tailwind) |
| **Client Scripting** | Vanilla JavaScript | Native Fetch API for asynchronous AJAX polling |
| **View Layer** | Jakarta Server Pages (JSP) | JSP 3.1, JSTL 3.0, Expression Language (EL) |
| **Controller Layer** | Jakarta Servlets | Servlet 6.0 (Tomcat 10.1+ standard) |
| **Service Layer** | Pure Java Services | Business logic, queue engine, fairness math |
| **Persistence (DAO)** | Pure JDBC | PreparedStatement, Connection Pooling, Transactions |
| **Database** | MySQL 8.0+ | InnoDB, foreign keys, unique constraints, indexes |
| **Build & Packaging** | Apache Maven 3.8+ | Generates standard `hospital-queue.war` |
| **Application Server** | Apache Tomcat 10.1+ | Servlet Container |

> **Strict Architectural Restriction Notice**:  
> No Spring, Spring Boot, Hibernate, JPA, React, Angular, Vue, Bootstrap, Tailwind, jQuery, or external ORMs are used. The implementation demonstrates pure Java web architecture using Servlets, JSP, JDBC, and AJAX.

---

## 3. System Architecture & MVC Layering

```
+-------------------------------------------------------------------------+
|                              WEB BROWSER                                |
|  HTML5 + Pure CSS3 + Vanilla JavaScript (Fetch API 5s Polling Engine)   |
+-------------------------------------------------------------------------+
                                    |
                  HTTP Requests / AJAX JSON Calls
                                    v
+-------------------------------------------------------------------------+
|                  FILTER LAYER (com.hospital.filter)                     |
|  - CharacterEncodingFilter (UTF-8)                                      |
|  - AuthenticationFilter (Session validation & redirect)                |
|  - RoleAuthorizationFilter (RBAC URL authorization)                     |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
|                CONTROLLER LAYER (com.hospital.controller)               |
|  - auth/ (LoginServlet, LogoutServlet, RegisterServlet)                 |
|  - patient/ (PatientDashboard, PatientQueue, BookAppointment, History)  |
|  - doctor/ (DoctorDashboard, CallNext, StartExam, Complete, Skip)       |
|  - receptionist/ (ReceptionDashboard, RegisterWalkIn, GenerateToken)   |
|  - admin/ (AdminDashboard, ManageDoctors, ManageDepts, Analytics)       |
|  - ajax/ (QueueStatusServlet, DoctorQueueAjax, NextPatientAjax)         |
+-------------------------------------------------------------------------+
                     |                                    |
              Forward Data                         JSON Payload
                     v                                    v
+-----------------------------+          +--------------------------------+
|    VIEW LAYER (JSP / JSTL)  |          |       AJAX JSON RESPONSE       |
|  /WEB-INF/views/...         |          |  { currentToken, yourToken,    |
|  Pure UI rendering          |          |    patientsAhead, waitMinutes }|
+-----------------------------+          +--------------------------------+
                     |                                    |
                     +-----------------+------------------+
                                       |
                                       v
+-------------------------------------------------------------------------+
|                 BUSINESS SERVICE LAYER (com.hospital.service)           |
|  - QueueService (Token progression, call next, skip, recall)            |
|  - WaitingTimeService (Mathematical wait-time estimation)               |
|  - AppointmentService (Conflict validation, booking, check-in)          |
|  - AuthService (BCrypt cryptographic verification, session)             |
|  - ConsultationService (Duration tracking, historical doctor average)   |
+-------------------------------------------------------------------------+
                                       |
                                       v
+-------------------------------------------------------------------------+
|                  DATA ACCESS OBJECTS (com.hospital.dao)                 |
|  UserDAO, PatientDAO, DoctorDAO, DepartmentDAO, AppointmentDAO,         |
|  QueueDAO, ConsultationDAO, AuditLogDAO                                 |
+-------------------------------------------------------------------------+
                                       |
                   Pure JDBC PreparedStatements
                                       v
+-------------------------------------------------------------------------+
|                        MYSQL DATABASE (InnoDB)                          |
|  users, patients, doctors, departments, appointments, queue_entries,    |
|  consultations, doctor_schedules, notifications, audit_logs             |
+-------------------------------------------------------------------------+
```

---

## 4. Database Schema Design (`hospital_queue_db`)

The normalized relational schema includes 10 tables with foreign key cascades, unique constraints, and optimized index trees:

1. **`users`**: Central credentials table (`username`, `password_hash`, `email`, `role`, `is_active`). Passwords hashed with BCrypt.
2. **`departments`**: Clinical departments (`code`, `name`, `default_avg_consultation_time`, `location_building`).
3. **`doctors`**: Medical staff profile, cabin room, consultation fee, dynamic `avg_consultation_minutes`, duty status.
4. **`patients`**: Patient demographics with unique identification number (`uhid`, e.g., `UHID-2026-0041`).
5. **`doctor_schedules`**: Daily shifts, start/end times, and max token capacity.
6. **`appointments`**: Booked slots with double-booking prevention (`UNIQUE KEY uk_patient_doctor_slot`).
7. **`queue_entries`**: Heart of real-time queue. Stores `token_number`, `token_display` (`CARD-42`), `priority_level`, `calculated_priority_score`, `status` (`WAITING`, `CALLED`, `IN_CONSULTATION`, `COMPLETED`, `SKIPPED`, `CANCELLED`), timestamps.
8. **`consultations`**: Medical outcomes, complaints, diagnosis, prescription, and recorded duration in minutes.
9. **`notifications`**: Patient alerts.
10. **`audit_logs`**: System audit trail tracking sensitive transactions.

---

## 5. Core Queue & Priority Algorithm

### Priority Tiers:
- **`EMERGENCY`**: Baseline Score = `300.0`
- **`HIGH`**: Baseline Score = `180.0`
- **`NORMAL`**: Baseline Score = `100.0`

### Anti-Starvation Fairness Aging Equation:
If normal patients are sorted solely below high-priority walk-ins, they suffer starvation during busy OPD shifts. The queue engine runs an automated aging formula:

$$\text{CalculatedScore} = \text{BaseScore} + \min(\text{ElapsedWaitMinutes} \times 1.5,\; 90.0)$$

**Starvation Prevention Rule**: A normal patient (`Base = 100`) who waits 54 minutes attains a calculated score of $100 + (54 \times 1.5) = 181.0$, surpassing newly arrived High Priority tokens (`Base = 180.0`). Ties are resolved deterministically by `arrival_time ASC`.

---

## 6. Algorithmic Waiting-Time Estimation

Implemented in `com.hospital.service.WaitingTimeService.java`:

$$\text{EstimatedWait} = (\text{PatientsAhead} \times T_{\text{avg}}) + \max(1,\; T_{\text{avg}} - T_{\text{elapsed}})$$

Where:
- $\text{PatientsAhead}$ = Number of active `WAITING` patients ranked ahead in the queue.
- $T_{\text{avg}}$ = Doctor's historical average consultation duration (e.g., 8 minutes).
- $T_{\text{elapsed}}$ = Elapsed minutes for the patient currently inside the consulting cabin.

### Concrete Example:
- Doctor: Dr. Kumar (Cardiology), $T_{\text{avg}} = 8\text{ mins}$
- Currently Serving: Token 42 (elapsed time inside cabin = 4 minutes; remaining = $8 - 4 = 4\text{ mins}$)
- Your Token: Token 45 (Patients ahead = 2: Token 43 and Token 44)
- **Calculation**: $(2 \times 8) + 4 = 16 + 4 = 20\text{ minutes}$

---

## 7. Real-Time Asynchronous AJAX Specification

The client runs a non-blocking asynchronous polling loop:

```javascript
// Native JavaScript Fetch API polling every 5000ms
setInterval(async () => {
    const res = await fetch(`${contextPath}/ajax/queue-status?patientId=${patientId}`);
    const data = await res.json();
    updateDOM(data);
}, 5000);
```

### JSON Response Contract:
```json
{
  "success": true,
  "doctorName": "Dr. Rajesh Kumar",
  "departmentName": "Cardiology",
  "roomNumber": "OPD-204",
  "currentToken": 42,
  "yourToken": 45,
  "patientsAhead": 2,
  "estimatedWaitMinutes": 16,
  "queueStatus": "WAITING",
  "doctorStatus": "BUSY",
  "totalWaiting": 3,
  "totalCompleted": 1,
  "serverTimestamp": 1758882000000
}
```

---

## 8. Role Portals & Credentials for Evaluation

All demo user accounts use the default evaluation password: `password123`

| Role | Username | Password | Access Path | Description |
| :--- | :--- | :--- | :--- | :--- |
| **ADMIN** | `admin` | `password123` | `/admin/dashboard` | Hospital stats, doctor management, department benchmarks, priority rules |
| **DOCTOR** | `dr.kumar` | `password123` | `/doctor/dashboard` | Live queue console, call next patient, start exam, write prescription |
| **RECEPTIONIST** | `receptionist1` | `password123` | `/receptionist/dashboard` | Patient registration, walk-in token issuance, doctor room status |
| **PATIENT** | `patient.john` | `password123` | `/patient/dashboard` | Live token board, appointment booking, medical history, wait time |

---

## 9. Build, Packaging & Tomcat Deployment Instructions

### Prerequisites:
- **Java Development Kit (JDK)**: OpenJDK 17 or higher
- **Build Tool**: Apache Maven 3.8+
- **Application Server**: Apache Tomcat 10.1.x+ (Jakarta EE 10 compatible)
- **Database**: MySQL Server 8.0+

### Step 1: Database Setup
```bash
# Log into MySQL CLI
mysql -u root -p

# Execute schema and initial seed data
mysql -u root -p < src/main/resources/schema.sql
mysql -u root -p < src/main/resources/seed_data.sql
```

### Step 2: Configure Database Credentials
Edit `src/main/resources/db.properties` if your MySQL username or password differ:
```properties
db.url=jdbc:mysql://localhost:3306/hospital_queue_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.username=root
db.password=root
```

### Step 3: Build WAR with Maven
```bash
# Clean and package into WAR archive
mvn clean package
```
This builds `target/hospital-queue.war`.

### Step 4: Deploy to Apache Tomcat
1. Copy `target/hospital-queue.war` into the `webapps/` folder of your Apache Tomcat 10.1 installation:
   ```bash
   cp target/hospital-queue.war /opt/tomcat/webapps/
   ```
2. Start Tomcat:
   ```bash
   /opt/tomcat/bin/startup.sh   # (or startup.bat on Windows)
   ```
3. Open your browser and navigate to:
   ```
   http://localhost:8080/hospital-queue/
   ```

---

## 10. Verification & Test Scenarios

### A. Real-Time Token Advancement:
1. Open Patient portal in Browser Tab 1 (`patient.john`) &rarr; Navigate to **Live Token Tracker**. Note Token 45, Current Token 41, Ahead = 3.
2. Open Doctor portal in Browser Tab 2 (`dr.kumar`) &rarr; Click **Call Next Patient**.
3. Observe Tab 1 update automatically within 5 seconds without manual refresh: Current Token becomes 42, Ahead becomes 2, Estimated Wait recalculates.

### B. Anti-Starvation Aging Test:
1. Add a High Priority walk-in token.
2. Inspect `queue_entries.calculated_priority_score` in the database.
3. Observe normal tokens that have arrived early receiving aging boosts, preventing them from being indefinitely postponed.

### C. Security & RBAC:
1. Log in as `patient.john`.
2. Attempt to navigate directly to `http://localhost:8080/hospital-queue/admin/dashboard`.
3. The `RoleAuthorizationFilter` intercepts the request and responds with `HTTP 403 Forbidden`.
