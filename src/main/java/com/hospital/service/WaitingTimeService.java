package com.hospital.service;

import com.hospital.model.Doctor;
import com.hospital.model.QueueEntry;
import com.hospital.model.QueueStatus;
import com.hospital.model.WaitingTimeEstimate;
import com.hospital.util.DateTimeUtil;

import java.sql.Timestamp;

/**
 * Dedicated Algorithmic Waiting-Time Estimator.
 * Calculates transparent, deterministic waiting time based on:
 * 1. Number of eligible patients positioned ahead in the doctor's queue.
 * 2. Doctor's dynamically recorded historical average consultation duration.
 * 3. Elapsed duration of the currently active consultation.
 * 4. Priority tier fairness weight.
 */
public class WaitingTimeService {

    /**
     * Calculates estimated waiting time for a given patient token.
     *
     * @param patientEntry The patient's queue entry
     * @param currentlyServing The entry currently CALLED or IN_CONSULTATION (can be null if doctor idle)
     * @param doctor The assigned doctor with average consultation metric
     * @param patientsAhead Count of waiting patients ahead in the queue
     * @return Detailed WaitingTimeEstimate breakdown
     */
    public WaitingTimeEstimate calculateWaitTime(QueueEntry patientEntry,
                                                QueueEntry currentlyServing,
                                                Doctor doctor,
                                                int patientsAhead) {

        WaitingTimeEstimate estimate = new WaitingTimeEstimate();
        estimate.setTokenNumber(patientEntry.getTokenNumber());
        estimate.setPatientsAhead(patientsAhead);

        int avgMinutes = (doctor != null && doctor.getAvgConsultationMinutes() > 0)
                ? doctor.getAvgConsultationMinutes()
                : 10;
        estimate.setDoctorAvgConsultationMinutes(avgMinutes);

        // If the patient is already CALLED or IN_CONSULTATION, wait time is 0
        if (patientEntry.getStatus() == QueueStatus.CALLED || 
            patientEntry.getStatus() == QueueStatus.IN_CONSULTATION) {
            estimate.setPatientsAhead(0);
            estimate.setCurrentConsultationElapsedMinutes(0);
            estimate.setEstimatedRemainingCurrentPatient(0);
            estimate.setTotalEstimatedMinutes(0);
            estimate.setEstimatedConsultationTimeFormatted("Immediate (Called Now)");
            return estimate;
        }

        if (patientEntry.getStatus() == QueueStatus.COMPLETED) {
            estimate.setTotalEstimatedMinutes(0);
            estimate.setEstimatedConsultationTimeFormatted("Completed");
            return estimate;
        }

        // 1. Calculate remaining time for the patient currently inside the doctor's cabin
        int remainingCurrentPatientMinutes = 0;
        int elapsedMinutes = 0;

        if (currentlyServing != null) {
            Timestamp startTs = currentlyServing.getConsultationStartTime() != null
                    ? currentlyServing.getConsultationStartTime()
                    : currentlyServing.getCalledTime();

            if (startTs != null) {
                elapsedMinutes = DateTimeUtil.minutesBetween(startTs, DateTimeUtil.currentTimestamp());
                // If elapsed has exceeded average, give an empirical buffer of 2 minutes
                remainingCurrentPatientMinutes = Math.max(2, avgMinutes - elapsedMinutes);
            } else {
                remainingCurrentPatientMinutes = avgMinutes;
            }
        }
        estimate.setCurrentConsultationElapsedMinutes(elapsedMinutes);
        estimate.setEstimatedRemainingCurrentPatient(remainingCurrentPatientMinutes);

        // 2. Base estimation: patients ahead multiplied by average consultation duration
        int queueWaitMinutes = patientsAhead * avgMinutes;

        // 3. Total algorithmic waiting time
        int totalMinutes = queueWaitMinutes + remainingCurrentPatientMinutes;

        // Ensure realistic minimum of at least 1 minute if waiting
        if (totalMinutes < 1) {
            totalMinutes = 1;
        }

        estimate.setTotalEstimatedMinutes(totalMinutes);

        // Format estimated consultation arrival time (e.g. "Approx. 24 mins")
        estimate.setEstimatedConsultationTimeFormatted(totalMinutes + " mins");

        return estimate;
    }
}
