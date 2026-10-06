/**
 * REAL-TIME QUEUE ENGINE CLIENT - AJAX Polling & DOM Synchronization
 * Smart Hospital Queue Management System
 * Uses pure Vanilla JavaScript and native Fetch API.
 */

class QueueMonitor {
  constructor(options) {
    this.contextPath = options.contextPath || "";
    this.patientId = options.patientId || null;
    this.doctorId = options.doctorId || null;
    this.pollInterval = options.pollInterval || 5000;
    this.timerId = null;
    this.lastCurrentToken = null;
    this.isPolling = false;

    this.init();
  }

  init() {
    if (this.patientId) {
      this.pollPatientQueue();
      this.timerId = setInterval(() => this.pollPatientQueue(), this.pollInterval);
    } else if (this.doctorId) {
      this.pollDoctorQueue();
      this.timerId = setInterval(() => this.pollDoctorQueue(), this.pollInterval);
    }
  }

  /**
   * Polls /ajax/queue-status for real-time patient queue view.
   */
  async pollPatientQueue() {
    if (this.isPolling) return;
    this.isPolling = true;

    try {
      const url = `${this.contextPath}/ajax/queue-status?patientId=${encodeURIComponent(this.patientId)}&_t=${Date.now()}`;
      const response = await fetch(url, {
        method: "GET",
        headers: { "Accept": "application/json" }
      });

      if (!response.ok) {
        throw new Error(`HTTP error status ${response.status}`);
      }

      const data = await response.json();
      if (data && data.success) {
        this.updatePatientDOM(data);
      }
    } catch (err) {
      console.warn("Queue polling notice:", err.message);
    } finally {
      this.isPolling = false;
    }
  }

  /**
   * Updates patient queue DOM elements without page reload.
   */
  updatePatientDOM(data) {
    const currentTokenEl = document.getElementById("currentTokenDisplay");
    const yourTokenEl = document.getElementById("yourTokenDisplay");
    const patientsAheadEl = document.getElementById("patientsAheadDisplay");
    const waitTimeEl = document.getElementById("estimatedWaitDisplay");
    const statusBadgeEl = document.getElementById("queueStatusBadge");
    const callBannerEl = document.getElementById("patientCalledBanner");
    const roomEl = document.getElementById("roomNumberDisplay");

    if (currentTokenEl) {
      currentTokenEl.textContent = (data.currentToken && data.currentToken > 0) ? data.currentToken : "--";
    }

    if (yourTokenEl) {
      yourTokenEl.textContent = data.yourToken || "--";
    }

    if (patientsAheadEl) {
      patientsAheadEl.textContent = data.patientsAhead !== undefined ? data.patientsAhead : "0";
    }

    if (waitTimeEl) {
      if (data.queueStatus === "CALLED" || data.queueStatus === "IN_CONSULTATION") {
        waitTimeEl.textContent = "0 mins (Proceed to Room)";
      } else if (data.queueStatus === "COMPLETED") {
        waitTimeEl.textContent = "Finished";
      } else {
        waitTimeEl.textContent = (data.estimatedWaitMinutes || 0) + " mins";
      }
    }

    if (roomEl && data.roomNumber) {
      roomEl.textContent = data.roomNumber;
    }

    if (statusBadgeEl && data.queueStatus) {
      statusBadgeEl.textContent = data.queueStatus;
      statusBadgeEl.className = "badge status-" + data.queueStatus.toLowerCase().replace("_", "-");
    }

    // Check if token was just called
    if (data.queueStatus === "CALLED" || (data.currentToken && data.currentToken === data.yourToken)) {
      if (callBannerEl) {
        callBannerEl.style.display = "block";
      }
      this.playChime();
    } else {
      if (callBannerEl) {
        callBannerEl.style.display = "none";
      }
    }

    this.lastCurrentToken = data.currentToken;
  }

  /**
   * Polls /ajax/doctor-queue for doctor's live queue list.
   */
  async pollDoctorQueue() {
    if (this.isPolling) return;
    this.isPolling = true;

    try {
      const url = `${this.contextPath}/ajax/doctor-queue?doctorId=${encodeURIComponent(this.doctorId)}&_t=${Date.now()}`;
      const response = await fetch(url, {
        method: "GET",
        headers: { "Accept": "application/json" }
      });

      if (!response.ok) return;

      const data = await response.json();
      if (data && data.success) {
        this.updateDoctorDOM(data);
      }
    } catch (e) {
      console.warn("Doctor queue polling error:", e);
    } finally {
      this.isPolling = false;
    }
  }

  updateDoctorDOM(data) {
    const currentTokenEl = document.getElementById("docCurrentTokenDisplay");
    const totalWaitingEl = document.getElementById("docTotalWaitingDisplay");
    const totalCompletedEl = document.getElementById("docTotalCompletedDisplay");

    if (currentTokenEl) {
      currentTokenEl.textContent = (data.currentToken && data.currentToken > 0) ? data.currentToken : "--";
    }
    if (totalWaitingEl) {
      totalWaitingEl.textContent = data.totalWaiting || "0";
    }
    if (totalCompletedEl) {
      totalCompletedEl.textContent = data.totalCompleted || "0";
    }
  }

  playChime() {
    try {
      const ctx = new (window.AudioContext || window.webkitAudioContext)();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = "sine";
      osc.frequency.setValueAtTime(587.33, ctx.currentTime); // D5
      osc.frequency.setValueAtTime(880, ctx.currentTime + 0.15); // A5
      gain.gain.setValueAtTime(0.2, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.5);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + 0.5);
    } catch (e) {
      // Audio playback restriction handling
    }
  }

  stop() {
    if (this.timerId) {
      clearInterval(this.timerId);
    }
  }
}
