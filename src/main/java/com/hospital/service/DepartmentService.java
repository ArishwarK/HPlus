package com.hospital.service;

import com.hospital.dao.DepartmentDAO;
import com.hospital.model.Department;

import java.util.List;

/**
 * Service managing hospital medical departments.
 */
public class DepartmentService {

    private final DepartmentDAO departmentDAO;

    public DepartmentService() {
        this.departmentDAO = new DepartmentDAO();
    }

    public DepartmentService(DepartmentDAO departmentDAO) {
        this.departmentDAO = departmentDAO;
    }

    public List<Department> getAllActiveDepartments() {
        return departmentDAO.findAllActive();
    }

    public Department getDepartmentById(int id) {
        return departmentDAO.findById(id);
    }

    public int createDepartment(Department department) {
        return departmentDAO.create(department);
    }

    public boolean updateDepartment(Department department) {
        return departmentDAO.update(department);
    }
}
