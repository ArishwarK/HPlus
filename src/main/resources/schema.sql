-- ==============================================================================
-- DATABASE SCHEMA: hospital_queue_db
-- Smart Hospital Queue Management and Waiting-Time Estimation System
-- Engine: InnoDB | Charset: utf8mb4 | Collation: utf8mb4_unicode_ci
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS hospital_queue_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE hospital_queue_db;

-- 1. USERS TABLE (Authentication & Core Credentials)
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    role ENUM('ADMIN', 'DOCTOR', 'RECEPTIONIST', 'PATIENT') NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_username (username),
    INDEX idx_user_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. DEPARTMENTS TABLE
CREATE TABLE IF NOT EXISTS departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(10) NOT NULL UNIQUE,
    description TEXT,
    location_building VARCHAR(50) DEFAULT 'Main Building',
    location_floor INT DEFAULT 1,
    default_avg_consultation_time INT NOT NULL DEFAULT 10 COMMENT 'In minutes',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dept_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. DOCTORS TABLE
CREATE TABLE IF NOT EXISTS doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    department_id INT NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    room_number VARCHAR(20) NOT NULL,
    qualification VARCHAR(100) NOT NULL,
    experience_years INT NOT NULL DEFAULT 1,
    consultation_fee DECIMAL(10, 2) NOT NULL DEFAULT 50.00,
    avg_consultation_minutes INT NOT NULL DEFAULT 10,
    status ENUM('AVAILABLE', 'BUSY', 'ON_BREAK', 'OFF_DUTY') NOT NULL DEFAULT 'AVAILABLE',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(department_id),
    INDEX idx_doc_dept (department_id),
    INDEX idx_doc_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. PATIENTS TABLE
CREATE TABLE IF NOT EXISTS patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE NULL COMMENT 'Null for walk-in patients registered by receptionist',
    uhid VARCHAR(30) NOT NULL UNIQUE COMMENT 'Unique Hospital Identification Number',
    full_name VARCHAR(100) NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    date_of_birth DATE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    blood_group VARCHAR(5),
    address TEXT,
    emergency_contact VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_patient_uhid (uhid),
    INDEX idx_patient_phone (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. DOCTOR SCHEDULES TABLE (Shift & Availability)
CREATE TABLE IF NOT EXISTS doctor_schedules (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    schedule_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    max_tokens INT NOT NULL DEFAULT 40,
    status ENUM('AVAILABLE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    UNIQUE KEY uk_doc_date_slot (doctor_id, schedule_date, start_time),
    INDEX idx_schedule_date (schedule_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. APPOINTMENTS TABLE
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(30) NOT NULL UNIQUE,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    department_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    type ENUM('ONLINE_SCHEDULED', 'WALK_IN', 'FOLLOW_UP') NOT NULL DEFAULT 'ONLINE_SCHEDULED',
    priority_level ENUM('NORMAL', 'HIGH', 'EMERGENCY') NOT NULL DEFAULT 'NORMAL',
    status ENUM('BOOKED', 'CHECKED_IN', 'IN_QUEUE', 'COMPLETED', 'CANCELLED', 'NO_SHOW') NOT NULL DEFAULT 'BOOKED',
    symptoms TEXT,
    created_by_user_id INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
    FOREIGN KEY (department_id) REFERENCES departments(department_id),
    FOREIGN KEY (created_by_user_id) REFERENCES users(user_id),
    UNIQUE KEY uk_patient_doctor_slot (patient_id, doctor_id, appointment_date, appointment_time),
    INDEX idx_appt_date (appointment_date),
    INDEX idx_appt_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. QUEUE ENTRIES TABLE (Core Real-Time Queue Engine)
CREATE TABLE IF NOT EXISTS queue_entries (
    queue_id INT AUTO_INCREMENT PRIMARY KEY,
    token_number INT NOT NULL,
    token_display VARCHAR(20) NOT NULL COMMENT 'e.g. CARD-42 or GEN-15',
    doctor_id INT NOT NULL,
    patient_id INT NOT NULL,
    appointment_id INT NULL,
    queue_date DATE NOT NULL,
    priority_level ENUM('NORMAL', 'HIGH', 'EMERGENCY') NOT NULL DEFAULT 'NORMAL',
    calculated_priority_score DECIMAL(8, 2) NOT NULL DEFAULT 100.00 COMMENT 'Fairness-boosted priority score',
    status ENUM('WAITING', 'CALLED', 'IN_CONSULTATION', 'COMPLETED', 'SKIPPED', 'CANCELLED') NOT NULL DEFAULT 'WAITING',
    arrival_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    called_time TIMESTAMP NULL,
    consultation_start_time TIMESTAMP NULL,
    consultation_end_time TIMESTAMP NULL,
    skipped_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE SET NULL,
    UNIQUE KEY uk_doc_date_token (doctor_id, queue_date, token_number),
    INDEX idx_queue_lookup (doctor_id, queue_date, status),
    INDEX idx_queue_patient (patient_id, queue_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. CONSULTATIONS TABLE
CREATE TABLE IF NOT EXISTS consultations (
    consultation_id INT AUTO_INCREMENT PRIMARY KEY,
    queue_id INT NOT NULL UNIQUE,
    doctor_id INT NOT NULL,
    patient_id INT NOT NULL,
    chief_complaints TEXT,
    diagnosis TEXT,
    prescription TEXT,
    duration_minutes INT NOT NULL DEFAULT 0,
    consultation_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (queue_id) REFERENCES queue_entries(queue_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. NOTIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_notif_user (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. AUDIT LOGS TABLE
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_audit_time (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
