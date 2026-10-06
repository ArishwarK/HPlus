package com.hospital.util;

import com.hospital.exception.DAOException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-safe Database Connection Manager.
 * Automatically connects to MySQL (and initializes schema/seed data if needed),
 * or seamlessly falls back to an embedded H2 database in MySQL compatibility mode
 * so the application works out-of-the-box with zero manual database setup.
 */
public final class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static final Properties PROPERTIES = new Properties();

    private static String url;
    private static String username;
    private static String password;
    private static String driver;
    private static volatile boolean initialized = false;

    static {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                PROPERTIES.load(in);
                driver = PROPERTIES.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                url = PROPERTIES.getProperty("db.url", "jdbc:mysql://localhost:3306/hospital_queue_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");
                username = PROPERTIES.getProperty("db.username", "root");
                password = PROPERTIES.getProperty("db.password", "arish2007");
            } else {
                driver = "com.mysql.cj.jdbc.Driver";
                url = "jdbc:mysql://localhost:3306/hospital_queue_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
                username = "root";
                password = "arish2007";
            }
            // Allow cloud environment variables (Render / Railway / Docker) to override db.properties
            if (System.getenv("DB_URL") != null && !System.getenv("DB_URL").isEmpty()) {
                url = System.getenv("DB_URL");
            }
            if (System.getenv("DB_USERNAME") != null && !System.getenv("DB_USERNAME").isEmpty()) {
                username = System.getenv("DB_USERNAME");
            }
            if (System.getenv("DB_PASSWORD") != null) {
                password = System.getenv("DB_PASSWORD");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not read db.properties, using defaults", e);
        }
    }

    private DBConnection() {
        // Prevent instantiation
    }

    /**
     * Obtains a database connection. Automatically initializes schema and seed data on first use,
     * and falls back to H2 in-memory database (MySQL mode) if MySQL is unavailable.
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            synchronized (DBConnection.class) {
                if (!initialized) {
                    initializeDatabase();
                    initialized = true;
                }
            }
        }
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not establish database connection to: " + url, e);
            throw new DAOException("Database connection failure: " + e.getMessage(), e);
        }
    }

    private static void initializeDatabase() {
        // 1. Try configured MySQL connection
        if (tryMySqlConnection(url, username, password)) {
            LOGGER.info("Connected to MySQL successfully using configured credentials.");
            ensureSchemaAndSeed();
            return;
        }

        // 2. Try MySQL with empty root password (common on XAMPP/WAMP/local setups)
        if (tryMySqlConnection(url, username, "")) {
            password = "";
            LOGGER.info("Connected to MySQL successfully using empty password.");
            ensureSchemaAndSeed();
            return;
        }

        // 3. Fallback to Embedded H2 Database in MySQL Compatibility Mode
        try {
            Class.forName("org.h2.Driver");
            driver = "org.h2.Driver";
            url = "jdbc:h2:mem:hospital_queue_db;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE";
            username = "sa";
            password = "";
            LOGGER.warning("MySQL not reachable. Activated embedded H2 database in MySQL mode for zero-config execution.");
            initH2SchemaAndSeed();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to initialize embedded H2 database fallback", ex);
        }
    }

    private static boolean tryMySqlConnection(String targetUrl, String user, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // First try connecting directly to hospital_queue_db
            try (Connection conn = DriverManager.getConnection(targetUrl, user, pass)) {
                return true;
            } catch (SQLException dbMissingEx) {
                // If database hospital_queue_db does not exist yet, connect to server root and create it
                String rootUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
                try (Connection rootConn = DriverManager.getConnection(rootUrl, user, pass);
                     Statement stmt = rootConn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS hospital_queue_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static void ensureSchemaAndSeed() {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            boolean needsSeed = false;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    needsSeed = true;
                }
            } catch (SQLException tableMissing) {
                needsSeed = true;
            }

            if (needsSeed) {
                executeSqlResource(conn, "schema.sql");
                executeSqlResource(conn, "seed_data.sql");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error verifying/seeding MySQL schema", e);
        }
    }

    private static void initH2SchemaAndSeed() {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password_hash VARCHAR(255) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "full_name VARCHAR(100) NOT NULL, " +
                    "phone_number VARCHAR(20) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL, " +
                    "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS departments (" +
                    "department_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL UNIQUE, " +
                    "code VARCHAR(10) NOT NULL UNIQUE, " +
                    "description TEXT, " +
                    "location_building VARCHAR(50) DEFAULT 'Main Building', " +
                    "location_floor INT DEFAULT 1, " +
                    "default_avg_consultation_time INT NOT NULL DEFAULT 10, " +
                    "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS doctors (" +
                    "doctor_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL UNIQUE, " +
                    "department_id INT NOT NULL, " +
                    "specialization VARCHAR(100) NOT NULL, " +
                    "room_number VARCHAR(20) NOT NULL, " +
                    "qualification VARCHAR(100) NOT NULL, " +
                    "experience_years INT NOT NULL DEFAULT 1, " +
                    "consultation_fee DECIMAL(10, 2) NOT NULL DEFAULT 50.00, " +
                    "avg_consultation_minutes INT NOT NULL DEFAULT 10, " +
                    "status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE', " +
                    "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS patients (" +
                    "patient_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT UNIQUE NULL, " +
                    "uhid VARCHAR(30) NOT NULL UNIQUE, " +
                    "full_name VARCHAR(100) NOT NULL, " +
                    "gender VARCHAR(15) NOT NULL, " +
                    "date_of_birth DATE NOT NULL, " +
                    "phone_number VARCHAR(20) NOT NULL, " +
                    "email VARCHAR(100), " +
                    "blood_group VARCHAR(5), " +
                    "address TEXT, " +
                    "emergency_contact VARCHAR(20), " +
                    "is_active BOOLEAN NOT NULL DEFAULT TRUE, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS doctor_schedules (" +
                    "schedule_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "doctor_id INT NOT NULL, " +
                    "schedule_date DATE NOT NULL, " +
                    "start_time TIME NOT NULL, " +
                    "end_time TIME NOT NULL, " +
                    "max_tokens INT NOT NULL DEFAULT 40, " +
                    "status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE', " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS appointments (" +
                    "appointment_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "appointment_number VARCHAR(30) NOT NULL UNIQUE, " +
                    "patient_id INT NOT NULL, " +
                    "doctor_id INT NOT NULL, " +
                    "department_id INT NOT NULL, " +
                    "appointment_date DATE NOT NULL, " +
                    "appointment_time TIME NOT NULL, " +
                    "type VARCHAR(30) NOT NULL DEFAULT 'ONLINE_SCHEDULED', " +
                    "priority_level VARCHAR(20) NOT NULL DEFAULT 'NORMAL', " +
                    "status VARCHAR(20) NOT NULL DEFAULT 'BOOKED', " +
                    "symptoms TEXT, " +
                    "created_by_user_id INT, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS queue_entries (" +
                    "queue_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "token_number INT NOT NULL, " +
                    "token_display VARCHAR(20) NOT NULL, " +
                    "doctor_id INT NOT NULL, " +
                    "patient_id INT NOT NULL, " +
                    "appointment_id INT NULL, " +
                    "queue_date DATE NOT NULL, " +
                    "priority_level VARCHAR(20) NOT NULL DEFAULT 'NORMAL', " +
                    "calculated_priority_score DECIMAL(8, 2) NOT NULL DEFAULT 100.00, " +
                    "status VARCHAR(25) NOT NULL DEFAULT 'WAITING', " +
                    "arrival_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "called_time TIMESTAMP NULL, " +
                    "consultation_start_time TIMESTAMP NULL, " +
                    "consultation_end_time TIMESTAMP NULL, " +
                    "skipped_count INT NOT NULL DEFAULT 0, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS consultations (" +
                    "consultation_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "queue_id INT NOT NULL UNIQUE, " +
                    "doctor_id INT NOT NULL, " +
                    "patient_id INT NOT NULL, " +
                    "chief_complaints TEXT, " +
                    "diagnosis TEXT, " +
                    "prescription TEXT, " +
                    "duration_minutes INT NOT NULL DEFAULT 0, " +
                    "consultation_date DATE NOT NULL, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS notifications (" +
                    "notification_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "title VARCHAR(150) NOT NULL, " +
                    "message TEXT NOT NULL, " +
                    "is_read BOOLEAN NOT NULL DEFAULT FALSE, " +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS audit_logs (" +
                    "log_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NULL, " +
                    "action VARCHAR(100) NOT NULL, " +
                    "details TEXT, " +
                    "ip_address VARCHAR(45), " +
                    "timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");

            String hash = PasswordUtil.hashPassword("password123");

            stmt.execute("INSERT INTO users (user_id, username, password_hash, email, full_name, phone_number, role, is_active) VALUES " +
                    "(1, 'admin', '" + hash + "', 'admin@hospital.org', 'Dr. Sarah Jenkins (Chief Admin)', '+1-555-0100', 'ADMIN', TRUE), " +
                    "(2, 'dr.kumar', '" + hash + "', 'kumar@hospital.org', 'Dr. Rajesh Kumar', '+1-555-0101', 'DOCTOR', TRUE), " +
                    "(3, 'dr.elena', '" + hash + "', 'elena@hospital.org', 'Dr. Elena Rostova', '+1-555-0102', 'DOCTOR', TRUE), " +
                    "(4, 'dr.marcus', '" + hash + "', 'marcus@hospital.org', 'Dr. Marcus Vance', '+1-555-0103', 'DOCTOR', TRUE), " +
                    "(5, 'dr.aisha', '" + hash + "', 'aisha@hospital.org', 'Dr. Aisha Patel', '+1-555-0104', 'DOCTOR', TRUE), " +
                    "(6, 'receptionist1', '" + hash + "', 'reception@hospital.org', 'Nancy Cooper (Desk Manager)', '+1-555-0150', 'RECEPTIONIST', TRUE), " +
                    "(7, 'patient.john', '" + hash + "', 'john.doe@gmail.com', 'Johnathan Doe', '+1-555-0201', 'PATIENT', TRUE), " +
                    "(8, 'patient.maria', '" + hash + "', 'maria.s@gmail.com', 'Maria Santos', '+1-555-0202', 'PATIENT', TRUE), " +
                    "(9, 'patient.david', '" + hash + "', 'david.k@gmail.com', 'David Kim', '+1-555-0203', 'PATIENT', TRUE)");

            stmt.execute("INSERT INTO departments (department_id, name, code, description, location_building, location_floor, default_avg_consultation_time) VALUES " +
                    "(1, 'Cardiology', 'CARD', 'Heart health, cardiovascular treatments, ECG, and catheterization.', 'Wing A - Cardio Tower', 2, 10), " +
                    "(2, 'Orthopedics', 'ORTH', 'Bone and joint surgery, sports injuries, rehabilitation and trauma.', 'Wing B - Surgery Block', 1, 12), " +
                    "(3, 'Pediatrics', 'PED', 'Comprehensive infant, child, and adolescent healthcare & wellness.', 'Wing C - Children Pavilion', 3, 8), " +
                    "(4, 'General Medicine', 'GEN', 'Primary care, diagnostic screenings, chronic ailment management.', 'Main OPD Center', 1, 8), " +
                    "(5, 'Neurology', 'NEUR', 'Brain, spinal cord, neurological disorders, and nerve conduction.', 'Wing A - Cardio Tower', 4, 15), " +
                    "(6, 'Dermatology', 'DERM', 'Clinical skin therapy, allergy treatments, cosmetic & laser care.', 'Main OPD Center', 2, 7)");

            stmt.execute("INSERT INTO doctors (doctor_id, user_id, department_id, specialization, room_number, qualification, experience_years, consultation_fee, avg_consultation_minutes, status, is_active) VALUES " +
                    "(1, 2, 1, 'Interventional Cardiologist', 'OPD-204', 'MD, DM (Cardiology), FACC', 14, 80.00, 10, 'AVAILABLE', TRUE), " +
                    "(2, 3, 2, 'Senior Orthopedic Surgeon', 'OPD-112', 'MS (Ortho), MCh, FRCS', 12, 75.00, 12, 'AVAILABLE', TRUE), " +
                    "(3, 4, 3, 'Consultant Pediatrician', 'OPD-305', 'MD (Pediatrics), DCH', 9, 60.00, 8, 'AVAILABLE', TRUE), " +
                    "(4, 5, 4, 'Chief Physician (Internal Med)', 'OPD-101', 'MBBS, MD (General Medicine)', 16, 50.00, 8, 'AVAILABLE', TRUE)");

            stmt.execute("INSERT INTO patients (patient_id, user_id, uhid, full_name, gender, date_of_birth, phone_number, email, blood_group, address, emergency_contact) VALUES " +
                    "(1, 7, 'UHID-2026-0041', 'Johnathan Doe', 'MALE', '1988-04-12', '+1-555-0201', 'john.doe@gmail.com', 'O+', '42 Pine Crest Ave, Springfield', '+1-555-9988'), " +
                    "(2, 8, 'UHID-2026-0042', 'Maria Santos', 'FEMALE', '1994-09-23', '+1-555-0202', 'maria.s@gmail.com', 'A+', '88 Maple Boulevard, Springfield', '+1-555-7766'), " +
                    "(3, 9, 'UHID-2026-0043', 'David Kim', 'MALE', '1976-11-05', '+1-555-0203', 'david.k@gmail.com', 'B+', '12 Lakeview Heights, Springfield', '+1-555-4433'), " +
                    "(4, NULL, 'UHID-2026-0044', 'Eleanor Vance (Walk-In)', 'FEMALE', '1952-02-18', '+1-555-0204', 'eleanor.v@yahoo.com', 'AB+', '301 Cedar Ln, Springfield', '+1-555-3322'), " +
                    "(5, NULL, 'UHID-2026-0045', 'Robert Chang (Walk-In)', 'MALE', '1982-08-30', '+1-555-0205', 'robert.c@gmail.com', 'O-', '55 Harbor Way, Springfield', '+1-555-2211')");

            stmt.execute("INSERT INTO appointments (appointment_id, appointment_number, patient_id, doctor_id, department_id, appointment_date, appointment_time, type, priority_level, status, symptoms) VALUES " +
                    "(1, 'APT-20260926-01', 1, 1, 1, CURRENT_DATE(), '09:30:00', 'ONLINE_SCHEDULED', 'NORMAL', 'IN_QUEUE', 'Occasional palpitations and chest tightness after brisk walking.'), " +
                    "(2, 'APT-20260926-02', 2, 1, 1, CURRENT_DATE(), '09:45:00', 'ONLINE_SCHEDULED', 'HIGH', 'IN_QUEUE', 'Sharp chest ache upon deep breathing, hypertension history.'), " +
                    "(3, 'APT-20260926-03', 3, 1, 1, CURRENT_DATE(), '10:00:00', 'ONLINE_SCHEDULED', 'NORMAL', 'IN_QUEUE', 'Routine 6-month lipid and cardio review.'), " +
                    "(4, 'APT-20260926-04', 4, 1, 1, CURRENT_DATE(), '10:15:00', 'WALK_IN', 'NORMAL', 'IN_QUEUE', 'Shortness of breath climbing stairs.'), " +
                    "(5, 'APT-20260926-05', 5, 1, 1, CURRENT_DATE(), '10:30:00', 'WALK_IN', 'NORMAL', 'IN_QUEUE', 'Blood pressure checkup and medication refill.')");

            stmt.execute("INSERT INTO queue_entries (queue_id, token_number, token_display, doctor_id, patient_id, appointment_id, queue_date, priority_level, calculated_priority_score, status, arrival_time, called_time, consultation_start_time, consultation_end_time) VALUES " +
                    "(1, 41, 'CARD-41', 1, 5, 5, CURRENT_DATE(), 'NORMAL', 100.0, 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), " +
                    "(2, 42, 'CARD-42', 1, 2, 2, CURRENT_DATE(), 'HIGH', 180.0, 'CALLED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL), " +
                    "(3, 43, 'CARD-43', 1, 3, 3, CURRENT_DATE(), 'NORMAL', 115.0, 'WAITING', CURRENT_TIMESTAMP, NULL, NULL, NULL), " +
                    "(4, 44, 'CARD-44', 1, 4, 4, CURRENT_DATE(), 'NORMAL', 110.0, 'WAITING', CURRENT_TIMESTAMP, NULL, NULL, NULL), " +
                    "(5, 45, 'CARD-45', 1, 1, 1, CURRENT_DATE(), 'NORMAL', 105.0, 'WAITING', CURRENT_TIMESTAMP, NULL, NULL, NULL)");

            stmt.execute("INSERT INTO consultations (consultation_id, queue_id, doctor_id, patient_id, chief_complaints, diagnosis, prescription, duration_minutes, consultation_date) VALUES " +
                    "(1, 1, 1, 5, 'Palpitations and exertion fatigue', 'Mild sinus tachycardia; stress-induced', 'Tab. Metoprolol 25mg OD (14 days), ECG clear, avoid excess caffeine', 9, CURRENT_DATE())");

            // Reset auto-increment sequences past seeded IDs
            stmt.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 10");
            stmt.execute("ALTER TABLE departments ALTER COLUMN department_id RESTART WITH 7");
            stmt.execute("ALTER TABLE doctors ALTER COLUMN doctor_id RESTART WITH 5");
            stmt.execute("ALTER TABLE patients ALTER COLUMN patient_id RESTART WITH 6");
            stmt.execute("ALTER TABLE appointments ALTER COLUMN appointment_id RESTART WITH 6");
            stmt.execute("ALTER TABLE queue_entries ALTER COLUMN queue_id RESTART WITH 6");
            stmt.execute("ALTER TABLE consultations ALTER COLUMN consultation_id RESTART WITH 2");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error seeding H2 database", e);
        }
    }

    private static void executeSqlResource(Connection conn, String resourceName) {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) return;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            try (Statement stmt = conn.createStatement()) {
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                        continue;
                    }
                    sb.append(line).append(" ");
                    if (trimmed.endsWith(";")) {
                        String sql = sb.toString().trim();
                        sql = sql.substring(0, sql.length() - 1);
                        try {
                            stmt.execute(sql);
                        } catch (SQLException ignored) {
                        }
                        sb.setLength(0);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error executing SQL script: " + resourceName, e);
        }
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection", e);
            }
        }
    }

    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing statement", e);
            }
        }
    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing resultSet", e);
            }
        }
    }

    public static void close(Statement stmt, ResultSet rs) {
        close(rs);
        close(stmt);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        close(rs);
        close(stmt);
        close(conn);
    }

    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error during transaction rollback", e);
            }
        }
    }
}
