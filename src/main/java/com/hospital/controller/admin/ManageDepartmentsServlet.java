package com.hospital.controller.admin;

import com.hospital.model.Department;
import com.hospital.service.DepartmentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller managing clinical departments and default consultation timings.
 */
public class ManageDepartmentsServlet extends HttpServlet {

    private DepartmentService departmentService;

    @Override
    public void init() {
        this.departmentService = new DepartmentService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Department> departments = departmentService.getAllActiveDepartments();
        request.setAttribute("departments", departments);
        request.getRequestDispatcher("/WEB-INF/views/admin/departments.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String description = request.getParameter("description");
        String building = request.getParameter("locationBuilding");
        int floor = Integer.parseInt(request.getParameter("locationFloor"));
        int avgTime = Integer.parseInt(request.getParameter("defaultAvgConsultationTime"));

        Department dept = new Department(0, name, code, description, building, floor, avgTime);
        departmentService.createDepartment(dept);

        response.sendRedirect(request.getContextPath() + "/admin/departments?created=true");
    }
}
