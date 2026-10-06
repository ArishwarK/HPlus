package com.hospital.controller.ajax;

import com.hospital.model.Doctor;
import com.hospital.service.DoctorService;
import com.hospital.util.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * AJAX endpoint providing dynamic doctor listings by department for appointment booking dropdowns.
 */
public class DoctorAvailabilityAjaxServlet extends HttpServlet {

    private DoctorService doctorService;

    @Override
    public void init() {
        this.doctorService = new DoctorService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String deptIdStr = request.getParameter("departmentId");
        if (deptIdStr != null && !deptIdStr.isEmpty()) {
            int deptId = Integer.parseInt(deptIdStr);
            List<Doctor> doctors = doctorService.getDoctorsByDepartment(deptId);
            JsonUtil.sendJsonResponse(response, HttpServletResponse.SC_OK, doctors);
            return;
        }

        List<Doctor> allDoctors = doctorService.getAllDoctors();
        JsonUtil.sendJsonResponse(response, HttpServletResponse.SC_OK, allDoctors);
    }
}
