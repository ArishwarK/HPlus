/**
 * VALIDATION JAVASCRIPT - Client-Side Validation Utility
 */

function validateRegisterForm(form) {
  const password = form.password.value;
  if (password.length < 6) {
    alert("Password must contain at least 6 characters.");
    return false;
  }
  return true;
}

function validateBookingForm(form) {
  if (!form.departmentId.value) {
    alert("Please select a medical department.");
    return false;
  }
  if (!form.doctorId.value) {
    alert("Please select an available doctor.");
    return false;
  }
  if (!form.appointmentDate.value) {
    alert("Please select a valid appointment date.");
    return false;
  }
  return true;
}
