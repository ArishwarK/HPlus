package com.hospital.dao;

import com.hospital.exception.DAOException;
import com.hospital.model.Consultation;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for consultations table.
 */
public class ConsultationDAO {

    public int create(Consultation c) {
        String sql = "INSERT INTO consultations (queue_id, doctor_id, patient_id, chief_complaints, diagnosis, " +
                     "prescription, duration_minutes, consultation_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getQueueId());
            ps.setInt(2, c.getDoctorId());
            ps.setInt(3, c.getPatientId());
            ps.setString(4, c.getChiefComplaints());
            ps.setString(5, c.getDiagnosis());
            ps.setString(6, c.getPrescription());
            ps.setInt(7, c.getDurationMinutes());
            ps.setDate(8, c.getConsultationDate());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    c.setConsultationId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error recording consultation", e);
        }
        return 0;
    }

    public List<Consultation> findByPatientId(int patientId) {
        List<Consultation> list = new ArrayList<>();
        String sql = "SELECT c.consultation_id, c.queue_id, c.doctor_id, c.patient_id, c.chief_complaints, " +
                     "c.diagnosis, c.prescription, c.duration_minutes, c.consultation_date, c.created_at, " +
                     "u.full_name AS doctor_name, dept.name AS department_name, p.full_name AS patient_name, " +
                     "p.uhid AS patient_uhid, q.token_number " +
                     "FROM consultations c " +
                     "JOIN doctors d ON c.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON d.department_id = dept.department_id " +
                     "JOIN patients p ON c.patient_id = p.patient_id " +
                     "JOIN queue_entries q ON c.queue_id = q.queue_id " +
                     "WHERE c.patient_id = ? ORDER BY c.consultation_date DESC, c.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error fetching consultations for patient: " + patientId, e);
        }
        return list;
    }

    public List<Consultation> findByDoctorAndDate(int doctorId, Date date) {
        List<Consultation> list = new ArrayList<>();
        String sql = "SELECT c.consultation_id, c.queue_id, c.doctor_id, c.patient_id, c.chief_complaints, " +
                     "c.diagnosis, c.prescription, c.duration_minutes, c.consultation_date, c.created_at, " +
                     "u.full_name AS doctor_name, dept.name AS department_name, p.full_name AS patient_name, " +
                     "p.uhid AS patient_uhid, q.token_number " +
                     "FROM consultations c " +
                     "JOIN doctors d ON c.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON d.department_id = dept.department_id " +
                     "JOIN patients p ON c.patient_id = p.patient_id " +
                     "JOIN queue_entries q ON c.queue_id = q.queue_id " +
                     "WHERE c.doctor_id = ? AND c.consultation_date = ? " +
                     "ORDER BY c.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error fetching consultations for doctor: " + doctorId, e);
        }
        return list;
    }

    public double calculateDoctorAverageMinutes(int doctorId) {
        String sql = "SELECT AVG(duration_minutes) FROM consultations WHERE doctor_id = ? AND duration_minutes > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble(1);
                    return avg > 0 ? avg : 10.0;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error calculating doctor average duration", e);
        }
        return 10.0;
    }

    private Consultation mapRow(ResultSet rs) throws SQLException {
        Consultation c = new Consultation();
        c.setConsultationId(rs.getInt("consultation_id"));
        c.setQueueId(rs.getInt("queue_id"));
        c.setDoctorId(rs.getInt("doctor_id"));
        c.setPatientId(rs.getInt("patient_id"));
        c.setChiefComplaints(rs.getString("chief_complaints"));
        c.setDiagnosis(rs.getString("diagnosis"));
        c.setPrescription(rs.getString("prescription"));
        c.setDurationMinutes(rs.getInt("duration_minutes"));
        c.setConsultationDate(rs.getDate("consultation_date"));
        c.setCreatedAt(rs.getTimestamp("created_at"));

        c.setDoctorName(rs.getString("doctor_name"));
        c.setDepartmentName(rs.getString("department_name"));
        c.setPatientName(rs.getString("patient_name"));
        c.setPatientUhid(rs.getString("patient_uhid"));
        c.setTokenNumber(rs.getInt("token_number"));
        return c;
    }
}
