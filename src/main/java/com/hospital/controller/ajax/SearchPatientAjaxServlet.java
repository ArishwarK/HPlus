package com.hospital.controller.ajax;

import com.hospital.model.Patient;
import com.hospital.service.PatientService;
import com.hospital.util.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * AJAX endpoint for receptionist quick patient search (by name, UHID, or phone).
 */
public class SearchPatientAjaxServlet extends HttpServlet {

    private PatientService patientService;

    @Override
    public void init() {
        this.patientService = new PatientService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String query = request.getParameter("query");
        if (query == null || query.trim().isEmpty()) {
            JsonUtil.sendJsonResponse(response, HttpServletResponse.SC_OK, List.of());
            return;
        }

        List<Patient> patients = patientService.searchPatients(query.trim());
        JsonUtil.sendJsonResponse(response, HttpServletResponse.SC_OK, patients);
    }
}
