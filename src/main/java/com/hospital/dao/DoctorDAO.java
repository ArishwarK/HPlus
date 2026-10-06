package com.hospital.dao;

import com.hospital.exception.DAOException;
import com.hospital.model.Doctor;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for doctors table with department and queue metric joins.
 */
public class DoctorDAO {

    public List<Doctor> findAll() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT d.doctor_id, d.user_id, d.department_id, d.specialization, d.room_number, " +
                     "d.qualification, d.experience_years, d.consultation_fee, d.avg_consultation_minutes, " +
                     "d.status, d.is_active, d.created_at, " +
                     "u.full_name AS doctor_name, u.email, u.phone_number, " +
                     "dept.name AS department_name, dept.code AS department_code " +
                     "FROM doctors d " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON d.department_id = dept.department_id " +
                     "ORDER BY d.is_active DESC, u.full_name ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error fetching all doctors", e);
        }
        return list;
    }

    public List<Doctor> findByDepartmentId(int departmentId) {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT d.doctor_id, d.user_id, d.department_id, d.specialization, d.room_number, " +
                     "d.qualification, d.experience_years, d.consultation_fee, d.avg_consultation_minutes, " +
                     "d.status, d.is_active, d.created_at, " +
                     "u.full_name AS doctor_name, u.email, u.phone_number, " +
                     "dept.name AS department_name, dept.code AS department_code " +
                     "FROM doctors d " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON d.department_id = dept.department_id " +
                     "WHERE d.department_id = ? AND d.is_active = TRUE " +
                     "ORDER BY u.full_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error fetching doctors for department: " + departmentId, e);
        }
        return list;
    }

    public Doctor findById(int doctorId) {
        String sql = "SELECT d.doctor_id, d.user_id, d.department_id, d.specialization, d.room_number, " +
                     "d.qualification, d.experience_years, d.consultation_fee, d.avg_consultation_minutes, " +
                     "d.status, d.is_active, d.created_at, " +
                     "u.full_name AS doctor_name, u.email, u.phone_number, " +
                     "dept.name AS department_name, dept.code AS department_code " +
                     "FROM doctors d " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON d.department_id = dept.department_id " +
                     "WHERE d.doctor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding doctor by id: " + doctorId, e);
        }
        return null;
    }

    public Doctor findByUserId(int userId) {
        String sql = "SELECT d.doctor_id, d.user_id, d.department_id, d.specialization, d.room_number, " +
                     "d.qualification, d.experience_years, d.consultation_fee, d.avg_consultation_minutes, " +
                     "d.status, d.is_active, d.created_at, " +
                     "u.full_name AS doctor_name, u.email, u.phone_number, " +
                     "dept.name AS department_name, dept.code AS department_code " +
                     "FROM doctors d " +
                     "JOIN users u ON d.user_id = u.user_id " +
                     "JOIN departments dept ON d.department_id = dept.department_id " +
                     "WHERE d.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding doctor by user id: " + userId, e);
        }
        return null;
    }

    public boolean updateStatus(int doctorId, Doctor.Status status) {
        String sql = "UPDATE doctors SET status = ? WHERE doctor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, doctorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating doctor status: " + doctorId, e);
        }
    }

    public boolean updateAvgDuration(int doctorId, int newAvgMinutes) {
        String sql = "UPDATE doctors SET avg_consultation_minutes = ? WHERE doctor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newAvgMinutes);
            ps.setInt(2, doctorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating doctor avg consultation duration", e);
        }
    }

    public int create(Doctor doctor) {
        String sql = "INSERT INTO doctors (user_id, department_id, specialization, room_number, qualification, " +
                     "experience_years, consultation_fee, avg_consultation_minutes, status, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, doctor.getUserId());
            ps.setInt(2, doctor.getDepartmentId());
            ps.setString(3, doctor.getSpecialization());
            ps.setString(4, doctor.getRoomNumber());
            ps.setString(5, doctor.getQualification());
            ps.setInt(6, doctor.getExperienceYears());
            ps.setBigDecimal(7, doctor.getConsultationFee());
            ps.setInt(8, doctor.getAvgConsultationMinutes());
            ps.setString(9, doctor.getStatus().name());
            ps.setBoolean(10, doctor.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    doctor.setDoctorId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating doctor record", e);
        }
        return 0;
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setDoctorId(rs.getInt("doctor_id"));
        d.setUserId(rs.getInt("user_id"));
        d.setDepartmentId(rs.getInt("department_id"));
        d.setSpecialization(rs.getString("specialization"));
        d.setRoomNumber(rs.getString("room_number"));
        d.setQualification(rs.getString("qualification"));
        d.setExperienceYears(rs.getInt("experience_years"));
        d.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        d.setAvgConsultationMinutes(rs.getInt("avg_consultation_minutes"));
        d.setStatus(Doctor.Status.fromString(rs.getString("status")));
        d.setActive(rs.getBoolean("is_active"));
        d.setCreatedAt(rs.getTimestamp("created_at"));

        d.setDoctorName(rs.getString("doctor_name"));
        d.setEmail(rs.getString("email"));
        d.setPhoneNumber(rs.getString("phone_number"));
        d.setDepartmentName(rs.getString("department_name"));
        d.setDepartmentCode(rs.getString("department_code"));
        return d;
    }
}
