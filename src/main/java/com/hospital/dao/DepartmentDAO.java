package com.hospital.dao;

import com.hospital.exception.DAOException;
import com.hospital.model.Department;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for departments table.
 */
public class DepartmentDAO {

    public List<Department> findAllActive() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT department_id, name, code, description, location_building, location_floor, " +
                     "default_avg_consultation_time, is_active, created_at FROM departments " +
                     "WHERE is_active = TRUE ORDER BY name ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error fetching departments", e);
        }
        return list;
    }

    public Department findById(int departmentId) {
        String sql = "SELECT department_id, name, code, description, location_building, location_floor, " +
                     "default_avg_consultation_time, is_active, created_at FROM departments WHERE department_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding department by id: " + departmentId, e);
        }
        return null;
    }

    public int create(Department dept) {
        String sql = "INSERT INTO departments (name, code, description, location_building, location_floor, default_avg_consultation_time, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getCode());
            ps.setString(3, dept.getDescription());
            ps.setString(4, dept.getLocationBuilding());
            ps.setInt(5, dept.getLocationFloor());
            ps.setInt(6, dept.getDefaultAvgConsultationTime());
            ps.setBoolean(7, dept.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    dept.setDepartmentId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error creating department: " + dept.getName(), e);
        }
        return 0;
    }

    public boolean update(Department dept) {
        String sql = "UPDATE departments SET name = ?, code = ?, description = ?, location_building = ?, " +
                     "location_floor = ?, default_avg_consultation_time = ?, is_active = ? WHERE department_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getCode());
            ps.setString(3, dept.getDescription());
            ps.setString(4, dept.getLocationBuilding());
            ps.setInt(5, dept.getLocationFloor());
            ps.setInt(6, dept.getDefaultAvgConsultationTime());
            ps.setBoolean(7, dept.isActive());
            ps.setInt(8, dept.getDepartmentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error updating department: " + dept.getDepartmentId(), e);
        }
    }

    private Department mapRow(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setDepartmentId(rs.getInt("department_id"));
        d.setName(rs.getString("name"));
        d.setCode(rs.getString("code"));
        d.setDescription(rs.getString("description"));
        d.setLocationBuilding(rs.getString("location_building"));
        d.setLocationFloor(rs.getInt("location_floor"));
        d.setDefaultAvgConsultationTime(rs.getInt("default_avg_consultation_time"));
        d.setActive(rs.getBoolean("is_active"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        return d;
    }
}
