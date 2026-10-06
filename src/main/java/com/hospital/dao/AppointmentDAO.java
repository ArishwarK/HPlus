package com.hospital.dao;

import com.hospital.exception.DAOException;
import com.hospital.model.Appointment;
import com.hospital.model.PriorityLevel;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for appointments table.
 */
public class AppointmentDAO {

    public int create(Appointment appt) {
        String sql = "INSERT INTO appointments (appointment_number, patient_id, doctor_id, department_id, " +
                     "appointment_date, appointment_time, type, priority_level, status, symptoms, created_by_user_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, appt.getAppointmentNumber());
            ps.setInt(2, appt.getPatientId());
            ps.setInt(3, appt.getDoctorId());
            ps.setInt(4, appt.getDepartmentId());
            ps.setDate(5, appt.getAppointmentDate());
            ps.setTime(6, appt.getAppointmentTime());
            ps.setString(7, appt.getType().name());
            ps.setString(8, appt.getPriorityLevel().name());
            ps.setString(9, appt.getStatus().name());
            ps.setString(10, appt.getSymptoms());
            if (appt.getCreatedByUserId() != null) {
                ps.setInt(11, appt.getCreatedByUserId());
            } else {
                ps.setNull(11, Types.INTEGER);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    appt.setAppointmentId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating appointment: " + appt.getAppointmentNumber(), e);
        }
        return 0;
    }

    public Appointment findById(int appointmentId) {
        String sql = "SELECT a.appointment_id, a.appointment_number, a.patient_id, a.doctor_id, a.department_id, " +
                     "a.appointment_date, a.appointment_time, a.type, a.priority_level, a.status, a.symptoms, " +
                     "a.created_by_user_id, a.created_at, a.updated_at, " +
                     "p.full_name AS patient_name, p.uhid AS patient_uhid, p.phone_number AS patient_phone, " +
                     "u.full_name AS doctor_name, dept.name AS department_name, d.room_number, " +
                     "q.token_number " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON a.department_id = dept.department_id " +
                     "LEFT JOIN queue_entries q ON a.appointment_id = q.appointment_id " +
                     "WHERE a.appointment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding appointment by id: " + appointmentId, e);
        }
        return null;
    }

    public List<Appointment> findByPatientId(int patientId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.appointment_id, a.appointment_number, a.patient_id, a.doctor_id, a.department_id, " +
                     "a.appointment_date, a.appointment_time, a.type, a.priority_level, a.status, a.symptoms, " +
                     "a.created_by_user_id, a.created_at, a.updated_at, " +
                     "p.full_name AS patient_name, p.uhid AS patient_uhid, p.phone_number AS patient_phone, " +
                     "u.full_name AS doctor_name, dept.name AS department_name, d.room_number, " +
                     "q.token_number " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON a.department_id = dept.department_id " +
                     "LEFT JOIN queue_entries q ON a.appointment_id = q.appointment_id " +
                     "WHERE a.patient_id = ? ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error fetching appointments for patient: " + patientId, e);
        }
        return list;
    }

    public Appointment findActiveTodayAppointment(int patientId, Date today) {
        String sql = "SELECT a.appointment_id, a.appointment_number, a.patient_id, a.doctor_id, a.department_id, " +
                     "a.appointment_date, a.appointment_time, a.type, a.priority_level, a.status, a.symptoms, " +
                     "a.created_by_user_id, a.created_at, a.updated_at, " +
                     "p.full_name AS patient_name, p.uhid AS patient_uhid, p.phone_number AS patient_phone, " +
                     "u.full_name AS doctor_name, dept.name AS department_name, d.room_number, " +
                     "q.token_number " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON a.department_id = dept.department_id " +
                     "LEFT JOIN queue_entries q ON a.appointment_id = q.appointment_id " +
                     "WHERE a.patient_id = ? AND a.appointment_date = ? AND a.status IN ('BOOKED', 'CHECKED_IN', 'IN_QUEUE') " +
                     "LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setDate(2, today);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding today's active appointment for patient: " + patientId, e);
        }
        return null;
    }

    public boolean hasConflict(int doctorId, Date date, Time time) {
        String sql = "SELECT COUNT(*) FROM appointments " +
                     "WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? " +
                     "AND status NOT IN ('CANCELLED', 'NO_SHOW')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, date);
            ps.setTime(3, time);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking doctor appointment conflict", e);
        }
        return false;
    }

    public boolean updateStatus(int appointmentId, Appointment.Status status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating appointment status: " + appointmentId, e);
        }
    }

    public int countTodayTotal(Date today) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, today);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error counting today's appointments", e);
        }
        return 0;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setAppointmentNumber(rs.getString("appointment_number"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));
        a.setDepartmentId(rs.getInt("department_id"));
        a.setAppointmentDate(rs.getDate("appointment_date"));
        a.setAppointmentTime(rs.getTime("appointment_time"));
        a.setType(Appointment.Type.valueOf(rs.getString("type")));
        a.setPriorityLevel(PriorityLevel.fromString(rs.getString("priority_level")));
        a.setStatus(Appointment.Status.fromString(rs.getString("status")));
        a.setSymptoms(rs.getString("symptoms"));
        int createdBy = rs.getInt("created_by_user_id");
        if (!rs.wasNull()) {
            a.setCreatedByUserId(createdBy);
        }
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setUpdatedAt(rs.getTimestamp("updated_at"));

        a.setPatientName(rs.getString("patient_name"));
        a.setPatientUhid(rs.getString("patient_uhid"));
        a.setPatientPhone(rs.getString("patient_phone"));
        a.setDoctorName(rs.getString("doctor_name"));
        a.setDepartmentName(rs.getString("department_name"));
        a.setRoomNumber(rs.getString("room_number"));
        int token = rs.getInt("token_number");
        if (!rs.wasNull()) {
            a.setTokenNumber(token);
        }
        return a;
    }
}
