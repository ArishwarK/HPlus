package com.hospital.dao;

import com.hospital.exception.DAOException;
import com.hospital.model.Role;
import com.hospital.model.User;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for users table.
 */
public class UserDAO {

    public User findByUsername(String username) {
        String sql = "SELECT user_id, username, password_hash, email, full_name, phone_number, role, is_active, created_at, updated_at " +
                     "FROM users WHERE username = ? AND is_active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding user by username: " + username, e);
        }
        return null;
    }

    public User findById(int userId) {
        String sql = "SELECT user_id, username, password_hash, email, full_name, phone_number, role, is_active, created_at, updated_at " +
                     "FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding user by id: " + userId, e);
        }
        return null;
    }

    public int create(User user) {
        String sql = "INSERT INTO users (username, password_hash, email, full_name, phone_number, role, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getRole().name());
            ps.setBoolean(7, user.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    user.setUserId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating user: " + user.getUsername(), e);
        }
        return 0;
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash, email, full_name, phone_number, role, is_active, created_at, updated_at " +
                     "FROM users ORDER BY user_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error fetching all users", e);
        }
        return list;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setRole(Role.fromString(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}
