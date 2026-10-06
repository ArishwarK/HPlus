package com.hospital.dao;

import com.hospital.exception.DAOException;
import com.hospital.model.PriorityLevel;
import com.hospital.model.QueueEntry;
import com.hospital.model.QueueStatus;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for queue_entries table.
 * Executes fair priority ranking, token advancement, and queue state transactions.
 */
public class QueueDAO {

    public synchronized int getNextTokenNumber(int doctorId, Date date) {
        String sql = "SELECT COALESCE(MAX(token_number), 0) + 1 FROM queue_entries WHERE doctor_id = ? AND queue_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error generating next token number", e);
        }
        return 1;
    }

    public int create(QueueEntry entry) {
        String sql = "INSERT INTO queue_entries (token_number, token_display, doctor_id, patient_id, appointment_id, " +
                     "queue_date, priority_level, calculated_priority_score, status, arrival_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entry.getTokenNumber());
            ps.setString(2, entry.getTokenDisplay());
            ps.setInt(3, entry.getDoctorId());
            ps.setInt(4, entry.getPatientId());
            if (entry.getAppointmentId() != null) {
                ps.setInt(5, entry.getAppointmentId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setDate(6, entry.getQueueDate());
            ps.setString(7, entry.getPriorityLevel().name());
            ps.setDouble(8, entry.getCalculatedPriorityScore());
            ps.setString(9, entry.getStatus().name());
            ps.setTimestamp(10, entry.getArrivalTime());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    entry.setQueueId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating queue entry: " + entry.getTokenDisplay(), e);
        }
        return 0;
    }

    public QueueEntry findById(int queueId) {
        String sql = "SELECT q.queue_id, q.token_number, q.token_display, q.doctor_id, q.patient_id, q.appointment_id, " +
                     "q.queue_date, q.priority_level, q.calculated_priority_score, q.status, q.arrival_time, q.called_time, " +
                     "q.consultation_start_time, q.consultation_end_time, q.skipped_count, q.created_at, q.updated_at, " +
                     "p.full_name AS patient_name, p.uhid AS patient_uhid, p.phone_number AS patient_phone, " +
                     "u.full_name AS doctor_name, dept.name AS department_name, d.room_number, a.symptoms " +
                     "FROM queue_entries q " +
                     "JOIN patients p ON q.patient_id = p.patient_id " +
                     "JOIN doctors d ON q.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON d.department_id = dept.department_id " +
                     "LEFT JOIN appointments a ON q.appointment_id = a.appointment_id " +
                     "WHERE q.queue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, queueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding queue entry by id: " + queueId, e);
        }
        return null;
    }

    public QueueEntry findCurrentServing(int doctorId, Date date) {
        String sql = "SELECT q.queue_id, q.token_number, q.token_display, q.doctor_id, q.patient_id, q.appointment_id, " +
                     "q.queue_date, q.priority_level, q.calculated_priority_score, q.status, q.arrival_time, q.called_time, " +
                     "q.consultation_start_time, q.consultation_end_time, q.skipped_count, q.created_at, q.updated_at, " +
                     "p.full_name AS patient_name, p.uhid AS patient_uhid, p.phone_number AS patient_phone, " +
                     "u.full_name AS doctor_name, dept.name AS department_name, d.room_number, a.symptoms " +
                     "FROM queue_entries q " +
                     "JOIN patients p ON q.patient_id = p.patient_id " +
                     "JOIN doctors d ON q.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON d.department_id = dept.department_id " +
                     "LEFT JOIN appointments a ON q.appointment_id = a.appointment_id " +
                     "WHERE q.doctor_id = ? AND q.queue_date = ? AND q.status IN ('CALLED', 'IN_CONSULTATION') " +
                     "ORDER BY q.called_time DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding currently served patient for doctor: " + doctorId, e);
        }
        return null;
    }

    public QueueEntry findNextEligiblePatient(int doctorId, Date date) {
        // Priority + Aging score ordering: highest priority score first, ties broken by arrival time
        String sql = "SELECT q.queue_id, q.token_number, q.token_display, q.doctor_id, q.patient_id, q.appointment_id, " +
                     "q.queue_date, q.priority_level, q.calculated_priority_score, q.status, q.arrival_time, q.called_time, " +
                     "q.consultation_start_time, q.consultation_end_time, q.skipped_count, q.created_at, q.updated_at, " +
                     "p.full_name AS patient_name, p.uhid AS patient_uhid, p.phone_number AS patient_phone, " +
                     "u.full_name AS doctor_name, dept.name AS department_name, d.room_number, a.symptoms " +
                     "FROM queue_entries q " +
                     "JOIN patients p ON q.patient_id = p.patient_id " +
                     "JOIN doctors d ON q.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON d.department_id = dept.department_id " +
                     "LEFT JOIN appointments a ON q.appointment_id = a.appointment_id " +
                     "WHERE q.doctor_id = ? AND q.queue_date = ? AND q.status = 'WAITING' " +
                     "ORDER BY q.calculated_priority_score DESC, q.arrival_time ASC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error querying next eligible patient in queue", e);
        }
        return null;
    }

    public List<QueueEntry> findByDoctorAndDate(int doctorId, Date date) {
        List<QueueEntry> list = new ArrayList<>();
        String sql = "SELECT q.queue_id, q.token_number, q.token_display, q.doctor_id, q.patient_id, q.appointment_id, " +
                     "q.queue_date, q.priority_level, q.calculated_priority_score, q.status, q.arrival_time, q.called_time, " +
                     "q.consultation_start_time, q.consultation_end_time, q.skipped_count, q.created_at, q.updated_at, " +
                     "p.full_name AS patient_name, p.uhid AS patient_uhid, p.phone_number AS patient_phone, " +
                     "u.full_name AS doctor_name, dept.name AS department_name, d.room_number, a.symptoms " +
                     "FROM queue_entries q " +
                     "JOIN patients p ON q.patient_id = p.patient_id " +
                     "JOIN doctors d ON q.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON d.department_id = dept.department_id " +
                     "LEFT JOIN appointments a ON q.appointment_id = a.appointment_id " +
                     "WHERE q.doctor_id = ? AND q.queue_date = ? " +
                     "ORDER BY CASE q.status " +
                     "  WHEN 'IN_CONSULTATION' THEN 1 " +
                     "  WHEN 'CALLED' THEN 2 " +
                     "  WHEN 'WAITING' THEN 3 " +
                     "  WHEN 'SKIPPED' THEN 4 " +
                     "  WHEN 'COMPLETED' THEN 5 " +
                     "  ELSE 6 END, " +
                     "q.calculated_priority_score DESC, q.arrival_time ASC";
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
            throw new DAOException("Error fetching doctor's queue entries", e);
        }
        return list;
    }

    public QueueEntry findByPatientToday(int patientId, Date date) {
        String sql = "SELECT q.queue_id, q.token_number, q.token_display, q.doctor_id, q.patient_id, q.appointment_id, " +
                     "q.queue_date, q.priority_level, q.calculated_priority_score, q.status, q.arrival_time, q.called_time, " +
                     "q.consultation_start_time, q.consultation_end_time, q.skipped_count, q.created_at, q.updated_at, " +
                     "p.full_name AS patient_name, p.uhid AS patient_uhid, p.phone_number AS patient_phone, " +
                     "u.full_name AS doctor_name, dept.name AS department_name, d.room_number, a.symptoms " +
                     "FROM queue_entries q " +
                     "JOIN patients p ON q.patient_id = p.patient_id " +
                     "JOIN doctors d ON q.doctor_id = d.doctor_id " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON d.department_id = dept.department_id " +
                     "LEFT JOIN appointments a ON q.appointment_id = a.appointment_id " +
                     "WHERE q.patient_id = ? AND q.queue_date = ? AND q.status NOT IN ('CANCELLED') " +
                     "ORDER BY q.queue_id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setDate(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding patient's queue entry today: " + patientId, e);
        }
        return null;
    }

    public int countPatientsAhead(int doctorId, Date date, double myScore, Timestamp myArrivalTime) {
        String sql = "SELECT COUNT(*) FROM queue_entries " +
                     "WHERE doctor_id = ? AND queue_date = ? AND status = 'WAITING' " +
                     "AND (calculated_priority_score > ? OR (calculated_priority_score = ? AND arrival_time < ?))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, date);
            ps.setDouble(3, myScore);
            ps.setDouble(4, myScore);
            ps.setTimestamp(5, myArrivalTime);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error counting patients ahead", e);
        }
        return 0;
    }

    public boolean updateStatus(int queueId, QueueStatus status) {
        String sql = "UPDATE queue_entries SET status = ?, updated_at = NOW() WHERE queue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, queueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating queue status: " + queueId, e);
        }
    }

    public boolean markAsCalled(int queueId, Timestamp calledTime) {
        String sql = "UPDATE queue_entries SET status = 'CALLED', called_time = ?, updated_at = NOW() WHERE queue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, calledTime);
            ps.setInt(2, queueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error marking token as called: " + queueId, e);
        }
    }

    public boolean markAsConsultationStarted(int queueId, Timestamp startTime) {
        String sql = "UPDATE queue_entries SET status = 'IN_CONSULTATION', consultation_start_time = ?, updated_at = NOW() WHERE queue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, startTime);
            ps.setInt(2, queueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error starting consultation: " + queueId, e);
        }
    }

    public boolean markAsCompleted(int queueId, Timestamp endTime) {
        String sql = "UPDATE queue_entries SET status = 'COMPLETED', consultation_end_time = ?, updated_at = NOW() WHERE queue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, endTime);
            ps.setInt(2, queueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error completing consultation: " + queueId, e);
        }
    }

    public boolean incrementSkippedCount(int queueId) {
        String sql = "UPDATE queue_entries SET status = 'SKIPPED', skipped_count = skipped_count + 1, updated_at = NOW() WHERE queue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, queueId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error skipping token: " + queueId, e);
        }
    }

    /**
     * Aging and Fairness Update:
     * Increases calculated_priority_score of waiting normal patients based on elapsed minutes in queue.
     * Formula: BaseScore + (MIN(ElapsedMinutes, 60) * 1.5)
     */
    public void updateFairnessAgingScores(int doctorId, Date date) {
        String sql = "UPDATE queue_entries " +
                     "SET calculated_priority_score = CASE priority_level " +
                     "    WHEN 'EMERGENCY' THEN 300.00 " +
                     "    WHEN 'HIGH' THEN 180.00 + LEAST(TIMESTAMPDIFF(MINUTE, arrival_time, NOW()) * 1.0, 50.0) " +
                     "    ELSE 100.00 + LEAST(TIMESTAMPDIFF(MINUTE, arrival_time, NOW()) * 1.5, 90.0) " +
                     "END " +
                     "WHERE doctor_id = ? AND queue_date = ? AND status = 'WAITING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, date);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error updating fairness scores for doctor: " + doctorId, e);
        }
    }

    public int countWaiting(int doctorId, Date date) {
        String sql = "SELECT COUNT(*) FROM queue_entries WHERE doctor_id = ? AND queue_date = ? AND status = 'WAITING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error counting waiting patients", e);
        }
        return 0;
    }

    public int countCompleted(int doctorId, Date date) {
        String sql = "SELECT COUNT(*) FROM queue_entries WHERE doctor_id = ? AND queue_date = ? AND status = 'COMPLETED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error counting completed patients", e);
        }
        return 0;
    }

    private QueueEntry mapRow(ResultSet rs) throws SQLException {
        QueueEntry q = new QueueEntry();
        q.setQueueId(rs.getInt("queue_id"));
        q.setTokenNumber(rs.getInt("token_number"));
        q.setTokenDisplay(rs.getString("token_display"));
        q.setDoctorId(rs.getInt("doctor_id"));
        q.setPatientId(rs.getInt("patient_id"));
        int apptId = rs.getInt("appointment_id");
        if (!rs.wasNull()) {
            q.setAppointmentId(apptId);
        }
        q.setQueueDate(rs.getDate("queue_date"));
        q.setPriorityLevel(PriorityLevel.fromString(rs.getString("priority_level")));
        q.setCalculatedPriorityScore(rs.getDouble("calculated_priority_score"));
        q.setStatus(QueueStatus.fromString(rs.getString("status")));
        q.setArrivalTime(rs.getTimestamp("arrival_time"));
        q.setCalledTime(rs.getTimestamp("called_time"));
        q.setConsultationStartTime(rs.getTimestamp("consultation_start_time"));
        q.setConsultationEndTime(rs.getTimestamp("consultation_end_time"));
        q.setSkippedCount(rs.getInt("skipped_count"));
        q.setCreatedAt(rs.getTimestamp("created_at"));
        q.setUpdatedAt(rs.getTimestamp("updated_at"));

        q.setPatientName(rs.getString("patient_name"));
        q.setPatientUhid(rs.getString("patient_uhid"));
        q.setPatientPhone(rs.getString("patient_phone"));
        q.setDoctorName(rs.getString("doctor_name"));
        q.setDepartmentName(rs.getString("department_name"));
        q.setRoomNumber(rs.getString("room_number"));
        q.setSymptoms(rs.getString("symptoms"));
        return q;
    }
}
