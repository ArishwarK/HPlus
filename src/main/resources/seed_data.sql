-- ==============================================================================
-- SEED DATA: hospital_queue_db
-- Populates departments, staff users, doctors, patients, schedules, and active queues
-- ==============================================================================

USE hospital_queue_db;

-- 1. SEED USERS (Default password for all demo accounts is: password123)
-- BCrypt Hash: $2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2
INSERT INTO users (user_id, username, password_hash, email, full_name, phone_number, role, is_active) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'admin@hospital.org', 'Dr. Sarah Jenkins (Chief Admin)', '+1-555-0100', 'ADMIN', TRUE),
(2, 'dr.kumar', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'kumar@hospital.org', 'Dr. Rajesh Kumar', '+1-555-0101', 'DOCTOR', TRUE),
(3, 'dr.elena', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'elena@hospital.org', 'Dr. Elena Rostova', '+1-555-0102', 'DOCTOR', TRUE),
(4, 'dr.marcus', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'marcus@hospital.org', 'Dr. Marcus Vance', '+1-555-0103', 'DOCTOR', TRUE),
(5, 'dr.aisha', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'aisha@hospital.org', 'Dr. Aisha Patel', '+1-555-0104', 'DOCTOR', TRUE),
(6, 'receptionist1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'reception@hospital.org', 'Nancy Cooper (Desk Manager)', '+1-555-0150', 'RECEPTIONIST', TRUE),
(7, 'patient.john', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'john.doe@gmail.com', 'Johnathan Doe', '+1-555-0201', 'PATIENT', TRUE),
(8, 'patient.maria', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'maria.s@gmail.com', 'Maria Santos', '+1-555-0202', 'PATIENT', TRUE),
(9, 'patient.david', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'david.k@gmail.com', 'David Kim', '+1-555-0203', 'PATIENT', TRUE)
ON DUPLICATE KEY UPDATE username=VALUES(username);

-- 2. SEED DEPARTMENTS
INSERT INTO departments (department_id, name, code, description, location_building, location_floor, default_avg_consultation_time) VALUES
(1, 'Cardiology', 'CARD', 'Heart health, cardiovascular treatments, ECG, and catheterization.', 'Wing A - Cardio Tower', 2, 10),
(2, 'Orthopedics', 'ORTH', 'Bone and joint surgery, sports injuries, rehabilitation and trauma.', 'Wing B - Surgery Block', 1, 12),
(3, 'Pediatrics', 'PED', 'Comprehensive infant, child, and adolescent healthcare & wellness.', 'Wing C - Children Pavilion', 3, 8),
(4, 'General Medicine', 'GEN', 'Primary care, diagnostic screenings, chronic ailment management.', 'Main OPD Center', 1, 8),
(5, 'Neurology', 'NEUR', 'Brain, spinal cord, neurological disorders, and nerve conduction.', 'Wing A - Cardio Tower', 4, 15),
(6, 'Dermatology', 'DERM', 'Clinical skin therapy, allergy treatments, cosmetic & laser care.', 'Main OPD Center', 2, 7)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 3. SEED DOCTORS
INSERT INTO doctors (doctor_id, user_id, department_id, specialization, room_number, qualification, experience_years, consultation_fee, avg_consultation_minutes, status, is_active) VALUES
(1, 2, 1, 'Interventional Cardiologist', 'OPD-204', 'MD, DM (Cardiology), FACC', 14, 80.00, 10, 'AVAILABLE', TRUE),
(2, 3, 2, 'Senior Orthopedic Surgeon', 'OPD-112', 'MS (Ortho), MCh, FRCS', 12, 75.00, 12, 'AVAILABLE', TRUE),
(3, 4, 3, 'Consultant Pediatrician', 'OPD-305', 'MD (Pediatrics), DCH', 9, 60.00, 8, 'AVAILABLE', TRUE),
(4, 5, 4, 'Chief Physician (Internal Med)', 'OPD-101', 'MBBS, MD (General Medicine)', 16, 50.00, 8, 'AVAILABLE', TRUE)
ON DUPLICATE KEY UPDATE specialization=VALUES(specialization);

-- 4. SEED PATIENTS
INSERT INTO patients (patient_id, user_id, uhid, full_name, gender, date_of_birth, phone_number, email, blood_group, address, emergency_contact) VALUES
(1, 7, 'UHID-2026-0041', 'Johnathan Doe', 'MALE', '1988-04-12', '+1-555-0201', 'john.doe@gmail.com', 'O+', '42 Pine Crest Ave, Springfield', '+1-555-9988'),
(2, 8, 'UHID-2026-0042', 'Maria Santos', 'FEMALE', '1994-09-23', '+1-555-0202', 'maria.s@gmail.com', 'A+', '88 Maple Boulevard, Springfield', '+1-555-7766'),
(3, 9, 'UHID-2026-0043', 'David Kim', 'MALE', '1976-11-05', '+1-555-0203', 'david.k@gmail.com', 'B+', '12 Lakeview Heights, Springfield', '+1-555-4433'),
(4, NULL, 'UHID-2026-0044', 'Eleanor Vance (Walk-In)', 'FEMALE', '1952-02-18', '+1-555-0204', 'eleanor.v@yahoo.com', 'AB+', '301 Cedar Ln, Springfield', '+1-555-3322'),
(5, NULL, 'UHID-2026-0045', 'Robert Chang (Walk-In)', 'MALE', '1982-08-30', '+1-555-0205', 'robert.c@gmail.com', 'O-', '55 Harbor Way, Springfield', '+1-555-2211')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- 5. SEED DOCTOR SCHEDULES FOR TODAY (CURRENT DATE)
INSERT INTO doctor_schedules (schedule_id, doctor_id, schedule_date, start_time, end_time, max_tokens, status) VALUES
(1, 1, CURRENT_DATE(), '09:00:00', '14:00:00', 30, 'AVAILABLE'),
(2, 2, CURRENT_DATE(), '09:30:00', '14:30:00', 25, 'AVAILABLE'),
(3, 3, CURRENT_DATE(), '08:30:00', '13:30:00', 35, 'AVAILABLE'),
(4, 4, CURRENT_DATE(), '09:00:00', '15:00:00', 40, 'AVAILABLE')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 6. SEED APPOINTMENTS
INSERT INTO appointments (appointment_id, appointment_number, patient_id, doctor_id, department_id, appointment_date, appointment_time, type, priority_level, status, symptoms) VALUES
(1, 'APT-20260926-01', 1, 1, 1, CURRENT_DATE(), '09:30:00', 'ONLINE_SCHEDULED', 'NORMAL', 'COMPLETED', 'Occasional palpitations and chest tightness after brisk walking.'),
(2, 'APT-20260926-02', 2, 1, 1, CURRENT_DATE(), '09:45:00', 'ONLINE_SCHEDULED', 'HIGH', 'IN_QUEUE', 'Sharp chest ache upon deep breathing, hypertension history.'),
(3, 'APT-20260926-03', 3, 1, 1, CURRENT_DATE(), '10:00:00', 'ONLINE_SCHEDULED', 'NORMAL', 'IN_QUEUE', 'Routine 6-month lipid and cardio review.'),
(4, 'APT-20260926-04', 4, 1, 1, CURRENT_DATE(), '10:15:00', 'WALK_IN', 'NORMAL', 'IN_QUEUE', 'Shortness of breath climbing stairs.'),
(5, 'APT-20260926-05', 5, 1, 1, CURRENT_DATE(), '10:30:00', 'WALK_IN', 'NORMAL', 'IN_QUEUE', 'Blood pressure checkup and medication refill.')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 7. SEED ACTIVE QUEUE ENTRIES (EXACT college prompt scenario demonstration)
-- Doctor: Dr. Kumar (Cardiology)
-- Token 41 -> COMPLETED
-- Token 42 -> CALLED
-- Token 43 -> WAITING
-- Token 44 -> WAITING
-- Token 45 -> WAITING
INSERT INTO queue_entries (queue_id, token_number, token_display, doctor_id, patient_id, appointment_id, queue_date, priority_level, calculated_priority_score, status, arrival_time, called_time, consultation_start_time, consultation_end_time) VALUES
(1, 41, 'CARD-41', 1, 1, 1, CURRENT_DATE(), 'NORMAL', 100.0, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 50 MINUTE), DATE_SUB(NOW(), INTERVAL 45 MINUTE), DATE_SUB(NOW(), INTERVAL 44 MINUTE), DATE_SUB(NOW(), INTERVAL 35 MINUTE)),
(2, 42, 'CARD-42', 1, 2, 2, CURRENT_DATE(), 'HIGH', 180.0, 'CALLED', DATE_SUB(NOW(), INTERVAL 35 MINUTE), DATE_SUB(NOW(), INTERVAL 5 MINUTE), DATE_SUB(NOW(), INTERVAL 4 MINUTE), NULL),
(3, 43, 'CARD-43', 1, 3, 3, CURRENT_DATE(), 'NORMAL', 115.0, 'WAITING', DATE_SUB(NOW(), INTERVAL 30 MINUTE), NULL, NULL, NULL),
(4, 44, 'CARD-44', 1, 4, 4, CURRENT_DATE(), 'NORMAL', 110.0, 'WAITING', DATE_SUB(NOW(), INTERVAL 20 MINUTE), NULL, NULL, NULL),
(5, 45, 'CARD-45', 1, 5, 5, CURRENT_DATE(), 'NORMAL', 105.0, 'WAITING', DATE_SUB(NOW(), INTERVAL 10 MINUTE), NULL, NULL, NULL)
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 8. SEED CONSULTATION FOR COMPLETED TOKEN 41
INSERT INTO consultations (consultation_id, queue_id, doctor_id, patient_id, chief_complaints, diagnosis, prescription, duration_minutes, consultation_date) VALUES
(1, 1, 1, 1, 'Palpitations and exertion fatigue', 'Mild sinus tachycardia; stress-induced', 'Tab. Metoprolol 25mg OD (14 days), ECG clear, avoid excess caffeine', 9, CURRENT_DATE())
ON DUPLICATE KEY UPDATE diagnosis=VALUES(diagnosis);
