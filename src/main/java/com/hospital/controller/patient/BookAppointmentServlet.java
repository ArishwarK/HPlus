package com.hospital.controller.patient;

import com.hospital.exception.ServiceException;
import com.hospital.model.Appointment;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.PriorityLevel;
import com.hospital.model.User;
import com.hospital.service.AppointmentService;
import com.hospital.service.DepartmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.PatientService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * Controller handling online appointment booking.
 */
public class BookAppointmentServlet extends HttpServlet {

    private DepartmentService departmentService;
    private DoctorService doctorService;
    private AppointmentService appointmentService;
    private PatientService patientService;

    @Override
    public void init() {
        this.departmentService = new DepartmentService();
        this.doctorService = new DoctorService();
        this.appointmentService = new AppointmentService();
        this.patientService = new PatientService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Department> departments = departmentService.getAllActiveDepartments();
        List<Doctor> doctors = doctorService.getAllDoctors();

        request.setAttribute("departments", departments);
        request.setAttribute("doctors", doctors);

        request.getRequestDispatcher("/WEB-INF/views/patient/book-appointment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        Patient patient = patientService.getPatientByUserId(currentUser.getUserId());

        int departmentId = Integer.parseInt(request.getParameter("departmentId"));
        int doctorId = Integer.parseInt(request.getParameter("doctorId"));
        Date apptDate = Date.valueOf(request.getParameter("appointmentDate"));
        Time apptTime = Time.valueOf(request.getParameter("appointmentTime") + ":00");
        String symptoms = request.getParameter("symptoms");
        String priorityStr = request.getParameter("priorityLevel");
        PriorityLevel priority = PriorityLevel.fromString(priorityStr);

        try {
            Appointment appt = appointmentService.bookAppointment(
                    patient.getPatientId(), doctorId, departmentId, apptDate, apptTime,
                    Appointment.Type.ONLINE_SCHEDULED, priority, symptoms, currentUser.getUserId());

            response.sendRedirect(request.getContextPath() + 
                    "/patient/dashboard?booked=true&apptNo=" + appt.getAppointmentNumber());

        } catch (ServiceException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("departments", departmentService.getAllActiveDepartments());
            request.setAttribute("doctors", doctorService.getAllDoctors());
            request.getRequestDispatcher("/WEB-INF/views/patient/book-appointment.jsp").forward(request, response);
        }
    }
}
