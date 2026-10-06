/**
 * COMMON JAVASCRIPT - Smart Hospital Queue Management System
 * Vanilla JS - No external libraries
 */

document.addEventListener("DOMContentLoaded", function () {
  // Auto-dismiss alert banners after 5 seconds
  const alerts = document.querySelectorAll(".alert");
  if (alerts.length > 0) {
    setTimeout(() => {
      alerts.forEach((alert) => {
        alert.style.transition = "opacity 0.4s ease";
        alert.style.opacity = "0";
        setTimeout(() => alert.remove(), 400);
      });
    }, 5000);
  }
});

function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.add("active");
  }
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.remove("active");
  }
}
