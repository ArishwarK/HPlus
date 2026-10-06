package com.hospital.service;

import com.hospital.dao.ConsultationDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.QueueDAO;
import com.hospital.exception.ServiceException;
import com.hospital.model.Consultation;
import com.hospital.model.Doctor;
import com.hospital.model.QueueEntry;
import com.hospital.model.QueueStatus;
import com.hospital.util.DateTimeUtil;

import java.sql.Timestamp;
import java.util.List;

/**
 * Service managing doctor-patient clinical consultations and duration metrics.
 */
public class ConsultationService {

    private final ConsultationDAO consultationDAO;
    private final QueueDAO queueDAO;
    private final DoctorDAO doctorDAO;

    public ConsultationService() {
        this.consultationDAO = new ConsultationDAO();
        this.queueDAO = new QueueDAO();
        this.doctorDAO = new DoctorDAO();
    }

    public ConsultationService(ConsultationDAO consultationDAO, QueueDAO queueDAO, DoctorDAO doctorDAO) {
        this.consultationDAO = consultationDAO;
        this.queueDAO = queueDAO;
        this.doctorDAO = doctorDAO;
    }

    public boolean startConsultation(int queueId) throws ServiceException {
        QueueEntry entry = queueDAO.findById(queueId);
        if (entry == null) {
            throw new ServiceException("Queue token entry not found.");
        }
        if (entry.getStatus() != QueueStatus.CALLED && entry.getStatus() != QueueStatus.WAITING) {
            throw new ServiceException("Patient must be in CALLED or WAITING state to begin consultation.");
        }

        Timestamp now = DateTimeUtil.currentTimestamp();
        boolean ok = queueDAO.markAsConsultationStarted(queueId, now);
        if (ok) {
            doctorDAO.updateStatus(entry.getDoctorId(), Doctor.Status.BUSY);
        }
        return ok;
    }

    public Consultation completeConsultation(int queueId, String chiefComplaints,
                                             String diagnosis, String prescription) throws ServiceException {
        QueueEntry entry = queueDAO.findById(queueId);
        if (entry == null) {
            throw new ServiceException("Queue token entry not found.");
        }

        Timestamp endTime = DateTimeUtil.currentTimestamp();
        Timestamp startTime = entry.getConsultationStartTime() != null 
                ? entry.getConsultationStartTime() 
                : (entry.getCalledTime() != null ? entry.getCalledTime() : endTime);

        int durationMinutes = Math.max(1, DateTimeUtil.minutesBetween(startTime, endTime));

        // 1. Mark QueueEntry as COMPLETED
        queueDAO.markAsCompleted(queueId, endTime);

        // 2. Insert clinical record
        Consultation consultation = new Consultation();
        consultation.setQueueId(queueId);
        consultation.setDoctorId(entry.getDoctorId());
        consultation.setPatientId(entry.getPatientId());
        consultation.setChiefComplaints(chiefComplaints != null ? chiefComplaints.trim() : "General review");
        consultation.setDiagnosis(diagnosis != null ? diagnosis.trim() : "Routine consultation completed");
        consultation.setPrescription(prescription != null ? prescription.trim() : "As advised");
        consultation.setDurationMinutes(durationMinutes);
        consultation.setConsultationDate(DateTimeUtil.today());

        int consultationId = consultationDAO.create(consultation);
        consultation.setConsultationId(consultationId);

        // 3. Recalculate historical average duration for this doctor
        double newAvg = consultationDAO.calculateDoctorAverageMinutes(entry.getDoctorId());
        if (newAvg > 0) {
            doctorDAO.updateAvgDuration(entry.getDoctorId(), (int) Math.round(newAvg));
        }

        // 4. Update doctor status back to AVAILABLE
        doctorDAO.updateStatus(entry.getDoctorId(), Doctor.Status.AVAILABLE);

        return consultation;
    }

    public List<Consultation> getPatientHistory(int patientId) {
        return consultationDAO.findByPatientId(patientId);
    }

    public List<Consultation> getDoctorTodayConsultations(int doctorId) {
        return consultationDAO.findByDoctorAndDate(doctorId, DateTimeUtil.today());
    }
}
