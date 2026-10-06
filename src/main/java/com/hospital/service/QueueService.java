package com.hospital.service;

import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.dao.QueueDAO;
import com.hospital.exception.ServiceException;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.PriorityLevel;
import com.hospital.model.QueueEntry;
import com.hospital.model.QueueStatus;
import com.hospital.model.QueueStatusResponse;
import com.hospital.model.WaitingTimeEstimate;
import com.hospital.util.DateTimeUtil;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * Core Real-Time Hospital Queue Engine.
 * Manages daily doctor token progression, fair priority scheduling with anti-starvation aging,
 * real-time token state transitions, and AJAX payload formatting.
 */
public class QueueService {

    private final QueueDAO queueDAO;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;
    private final WaitingTimeService waitingTimeService;

    public QueueService() {
        this.queueDAO = new QueueDAO();
        this.doctorDAO = new DoctorDAO();
        this.patientDAO = new PatientDAO();
        this.waitingTimeService = new WaitingTimeService();
    }

    public QueueService(QueueDAO queueDAO, DoctorDAO doctorDAO, PatientDAO patientDAO, WaitingTimeService waitingTimeService) {
        this.queueDAO = queueDAO;
        this.doctorDAO = doctorDAO;
        this.patientDAO = patientDAO;
        this.waitingTimeService = waitingTimeService;
    }

    /**
     * Generates a new Token and places the patient in the doctor's queue.
     */
    public synchronized QueueEntry generateToken(int doctorId, int patientId, Integer appointmentId,
                                                PriorityLevel priority) throws ServiceException {

        Doctor doctor = doctorDAO.findById(doctorId);
        if (doctor == null || !doctor.isActive()) {
            throw new ServiceException("Doctor is not currently on duty.");
        }

        Patient patient = patientDAO.findById(patientId);
        if (patient == null) {
            throw new ServiceException("Patient record not found.");
        }

        Date today = DateTimeUtil.today();

        // Prevent duplicate active token on the same day for this doctor
        QueueEntry existing = queueDAO.findByPatientToday(patientId, today);
        if (existing != null && existing.getDoctorId() == doctorId && 
            (existing.getStatus() == QueueStatus.WAITING || existing.getStatus() == QueueStatus.CALLED || existing.getStatus() == QueueStatus.IN_CONSULTATION)) {
            throw new ServiceException("Patient already has active token " + existing.getTokenDisplay() + " with this doctor today.");
        }

        int tokenNum = queueDAO.getNextTokenNumber(doctorId, today);
        String deptCode = doctor.getDepartmentCode() != null ? doctor.getDepartmentCode() : "GEN";
        String tokenDisplay = deptCode + "-" + tokenNum;

        // Base priority score calculation
        double baseScore = (priority != null ? priority : PriorityLevel.NORMAL).getBaseScore();

        QueueEntry entry = new QueueEntry();
        entry.setTokenNumber(tokenNum);
        entry.setTokenDisplay(tokenDisplay);
        entry.setDoctorId(doctorId);
        entry.setPatientId(patientId);
        entry.setAppointmentId(appointmentId);
        entry.setQueueDate(today);
        entry.setPriorityLevel(priority != null ? priority : PriorityLevel.NORMAL);
        entry.setCalculatedPriorityScore(baseScore);
        entry.setStatus(QueueStatus.WAITING);
        entry.setArrivalTime(DateTimeUtil.currentTimestamp());

        int id = queueDAO.create(entry);
        entry.setQueueId(id);
        entry.setDoctorName(doctor.getDoctorName());
        entry.setDepartmentName(doctor.getDepartmentName());
        entry.setRoomNumber(doctor.getRoomNumber());
        entry.setPatientName(patient.getFullName());

        return entry;
    }

    /**
     * Advances the queue: calls the next eligible patient.
     * Workflow:
     * 1. Updates fairness aging scores so normal patients who have waited long are prioritized.
     * 2. Identifies the next eligible WAITING patient based on calculated_priority_score and arrival_time.
     * 3. Marks the selected patient as CALLED.
     * 4. Updates doctor status to BUSY.
     * 5. Returns the called QueueEntry.
     */
    public synchronized QueueEntry callNextPatient(int doctorId) throws ServiceException {
        Doctor doctor = doctorDAO.findById(doctorId);
        if (doctor == null) {
            throw new ServiceException("Doctor not found.");
        }

        Date today = DateTimeUtil.today();

        // Step 1: Run Fairness Aging Algorithm
        queueDAO.updateFairnessAgingScores(doctorId, today);

        // Step 2: Query highest ranked WAITING patient
        QueueEntry nextPatient = queueDAO.findNextEligiblePatient(doctorId, today);
        if (nextPatient == null) {
            throw new ServiceException("No waiting patients currently in the queue.");
        }

        // Step 3: Transition to CALLED
        Timestamp now = DateTimeUtil.currentTimestamp();
        queueDAO.markAsCalled(nextPatient.getQueueId(), now);
        nextPatient.setStatus(QueueStatus.CALLED);
        nextPatient.setCalledTime(now);

        // Step 4: Set doctor to BUSY
        doctorDAO.updateStatus(doctorId, Doctor.Status.BUSY);

        return nextPatient;
    }

    /**
     * Marks patient as SKIPPED (e.g. called but did not appear).
     */
    public boolean skipPatient(int queueId) throws ServiceException {
        QueueEntry entry = queueDAO.findById(queueId);
        if (entry == null) {
            throw new ServiceException("Queue entry not found.");
        }
        boolean ok = queueDAO.incrementSkippedCount(queueId);
        if (ok) {
            doctorDAO.updateStatus(entry.getDoctorId(), Doctor.Status.AVAILABLE);
        }
        return ok;
    }

    /**
     * Recalls a previously skipped patient back to active WAITING status.
     */
    public boolean recallPatient(int queueId) throws ServiceException {
        QueueEntry entry = queueDAO.findById(queueId);
        if (entry == null) {
            throw new ServiceException("Queue entry not found.");
        }
        return queueDAO.updateStatus(queueId, QueueStatus.WAITING);
    }

    /**
     * Constructs the real-time JSON response for AJAX polling by patient.
     */
    public QueueStatusResponse getQueueStatusForPatient(int patientId) {
        Date today = DateTimeUtil.today();
        QueueStatusResponse res = new QueueStatusResponse();

        QueueEntry patientEntry = queueDAO.findByPatientToday(patientId, today);
        if (patientEntry == null) {
            res.setSuccess(false);
            res.setMessage("No active queue token found for today.");
            return res;
        }

        int doctorId = patientEntry.getDoctorId();
        Doctor doctor = doctorDAO.findById(doctorId);
        QueueEntry currentlyServing = queueDAO.findCurrentServing(doctorId, today);

        int patientsAhead = 0;
        if (patientEntry.getStatus() == QueueStatus.WAITING) {
            patientsAhead = queueDAO.countPatientsAhead(doctorId, today,
                    patientEntry.getCalculatedPriorityScore(), patientEntry.getArrivalTime());
        }

        WaitingTimeEstimate estimate = waitingTimeService.calculateWaitTime(
                patientEntry, currentlyServing, doctor, patientsAhead);

        res.setSuccess(true);
        res.setDoctorName(doctor != null ? doctor.getDoctorName() : patientEntry.getDoctorName());
        res.setDepartmentName(doctor != null ? doctor.getDepartmentName() : patientEntry.getDepartmentName());
        res.setRoomNumber(doctor != null ? doctor.getRoomNumber() : patientEntry.getRoomNumber());
        res.setCurrentToken(currentlyServing != null ? currentlyServing.getTokenNumber() : 0);
        res.setYourToken(patientEntry.getTokenNumber());
        res.setPatientsAhead(estimate.getPatientsAhead());
        res.setEstimatedWaitMinutes(estimate.getTotalEstimatedMinutes());
        res.setQueueStatus(patientEntry.getStatus().name());
        res.setDoctorStatus(doctor != null ? doctor.getStatus().name() : "AVAILABLE");
        res.setTotalWaiting(queueDAO.countWaiting(doctorId, today));
        res.setTotalCompleted(queueDAO.countCompleted(doctorId, today));

        return res;
    }

    /**
     * Returns full queue information for doctor's live view.
     */
    public QueueStatusResponse getDoctorQueueStatus(int doctorId) {
        Date today = DateTimeUtil.today();
        Doctor doctor = doctorDAO.findById(doctorId);
        QueueEntry currentlyServing = queueDAO.findCurrentServing(doctorId, today);
        List<QueueEntry> queueList = queueDAO.findByDoctorAndDate(doctorId, today);

        // Calculate dynamic wait times for all waiting entries in the list
        int runningAhead = 0;
        for (QueueEntry q : queueList) {
            if (q.getStatus() == QueueStatus.WAITING) {
                WaitingTimeEstimate est = waitingTimeService.calculateWaitTime(q, currentlyServing, doctor, runningAhead);
                q.setPatientsAhead(runningAhead);
                q.setEstimatedWaitMinutes(est.getTotalEstimatedMinutes());
                runningAhead++;
            }
        }

        QueueStatusResponse res = new QueueStatusResponse();
        res.setSuccess(true);
        res.setDoctorName(doctor != null ? doctor.getDoctorName() : "Doctor");
        res.setDepartmentName(doctor != null ? doctor.getDepartmentName() : "");
        res.setRoomNumber(doctor != null ? doctor.getRoomNumber() : "");
        res.setCurrentToken(currentlyServing != null ? currentlyServing.getTokenNumber() : 0);
        res.setDoctorStatus(doctor != null ? doctor.getStatus().name() : "AVAILABLE");
        res.setTotalWaiting(queueDAO.countWaiting(doctorId, today));
        res.setTotalCompleted(queueDAO.countCompleted(doctorId, today));
        res.setQueueEntries(queueList);

        return res;
    }

    public List<QueueEntry> getDoctorQueueList(int doctorId) {
        return queueDAO.findByDoctorAndDate(doctorId, DateTimeUtil.today());
    }

    public QueueEntry getQueueEntryById(int queueId) {
        return queueDAO.findById(queueId);
    }
}
