package com.hospital.dao;

import com.hospital.exception.DAOException;
import com.hospital.model.AuditLog;
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
 * Data Access Object for audit_logs table.
 */
public class AuditLogDAO {

    public void log(Integer userId, String action, String details, String ipAddress) {
        String sql = "INSERT INTO audit_logs (user_id, action, details, ip_address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null) {
                ps.setInt(1, userId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, action);
            ps.setString(3, details);
            ps.setString(4, ipAddress);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Non-critical logging failure shouldn't crash main transaction
            System.err.println("Audit log failure: " + e.getMessage());
        }
    }

    public List<AuditLog> findRecent(int limit) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT l.log_id, l.user_id, l.action, l.details, l.ip_address, l.timestamp, u.username " +
                     "FROM audit_logs l " +
                     "LEFT JOIN users u ON l.user_id = u.user_id " +
                     "ORDER BY l.timestamp DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog();
                    log.setLogId(rs.getInt("log_id"));
                    int uid = rs.getInt("user_id");
                    if (!rs.wasNull()) log.setUserId(uid);
                    log.setAction(rs.getString("action"));
                    log.setDetails(rs.getString("details"));
                    log.setIpAddress(rs.getString("ip_address"));
                    log.setTimestamp(rs.getTimestamp("timestamp"));
                    log.setUsername(rs.getString("username"));
                    list.add(log);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error fetching recent audit logs", e);
        }
        return list;
    }
}
