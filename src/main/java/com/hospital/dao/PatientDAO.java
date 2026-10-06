package com.hospital.dao;

import com.hospital.exception.DAOException;
import com.hospital.model.Patient;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for patients table.
 */
public class PatientDAO {

    public Patient findById(int patientId) {
        String sql = "SELECT patient_id, user_id, uhid, full_name, gender, date_of_birth, " +
                     "phone_number, email, blood_group, address, emergency_contact, is_active, created_at " +
                     "FROM patients WHERE patient_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding patient by id: " + patientId, e);
        }
        return null;
    }

    public Patient findByUserId(int userId) {
        String sql = "SELECT patient_id, user_id, uhid, full_name, gender, date_of_birth, " +
                     "phone_number, email, blood_group, address, emergency_contact, is_active, created_at " +
                     "FROM patients WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding patient by user id: " + userId, e);
        }
        return null;
    }

    public Patient findByUhid(String uhid) {
        String sql = "SELECT patient_id, user_id, uhid, full_name, gender, date_of_birth, " +
                     "phone_number, email, blood_group, address, emergency_contact, is_active, created_at " +
                     "FROM patients WHERE uhid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uhid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding patient by UHID: " + uhid, e);
        }
        return null;
    }

    public List<Patient> search(String query) {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT patient_id, user_id, uhid, full_name, gender, date_of_birth, " +
                     "phone_number, email, blood_group, address, emergency_contact, is_active, created_at " +
                     "FROM patients WHERE is_active = TRUE AND " +
                     "(full_name LIKE ? OR uhid LIKE ? OR phone_number LIKE ?) " +
                     "ORDER BY full_name ASC LIMIT 20";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String wildcard = "%" + query.trim() + "%";
            ps.setString(1, wildcard);
            ps.setString(2, wildcard);
            ps.setString(3, wildcard);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error searching patients with query: " + query, e);
        }
        return list;
    }

    public int create(Patient patient) {
        String sql = "INSERT INTO patients (user_id, uhid, full_name, gender, date_of_birth, " +
                     "phone_number, email, blood_group, address, emergency_contact, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (patient.getUserId() != null) {
                ps.setInt(1, patient.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, patient.getUhid());
            ps.setString(3, patient.getFullName());
            ps.setString(4, patient.getGender().name());
            ps.setDate(5, patient.getDateOfBirth());
            ps.setString(6, patient.getPhoneNumber());
            ps.setString(7, patient.getEmail());
            ps.setString(8, patient.getBloodGroup());
            ps.setString(9, patient.getAddress());
            ps.setString(10, patient.getEmergencyContact());
            ps.setBoolean(11, patient.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    patient.setPatientId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating patient record: " + patient.getUhid(), e);
        }
        return 0;
    }

    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM patients WHERE is_active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error counting patients", e);
        }
        return 0;
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getInt("patient_id"));
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            p.setUserId(userId);
        }
        p.setUhid(rs.getString("uhid"));
        p.setFullName(rs.getString("full_name"));
        p.setGender(Patient.Gender.fromString(rs.getString("gender")));
        p.setDateOfBirth(rs.getDate("date_of_birth"));
        p.setPhoneNumber(rs.getString("phone_number"));
        p.setEmail(rs.getString("email"));
        p.setBloodGroup(rs.getString("blood_group"));
        p.setAddress(rs.getString("address"));
        p.setEmergencyContact(rs.getString("emergency_contact"));
        p.setActive(rs.getBoolean("is_active"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
