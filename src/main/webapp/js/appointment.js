/**
 * APPOINTMENT JAVASCRIPT - Dynamic Department Filtering & Slot Selection
 */

function onDepartmentChange(deptSelect, doctorSelectId, contextPath) {
  const deptId = deptSelect.value;
  const doctorSelect = document.getElementById(doctorSelectId);
  if (!doctorSelect) return;

  if (!deptId) {
    doctorSelect.innerHTML = '<option value="">-- First Select a Department --</option>';
    return;
  }

  doctorSelect.innerHTML = '<option value="">Loading available doctors...</option>';

  fetch(`${contextPath}/ajax/doctor-availability?departmentId=${encodeURIComponent(deptId)}`)
    .then((res) => res.json())
    .then((doctors) => {
      if (!doctors || doctors.length === 0) {
        doctorSelect.innerHTML = '<option value="">No doctors currently available in this department</option>';
        return;
      }

      let options = '<option value="">-- Choose a Doctor --</option>';
      doctors.forEach((doc) => {
        options += `<option value="${doc.doctorId}">Dr. ${doc.doctorName} (${doc.specialization} - Room ${doc.roomNumber})</option>`;
      });
      doctorSelect.innerHTML = options;
    })
    .catch((err) => {
      console.error("Failed to load doctors:", err);
      doctorSelect.innerHTML = '<option value="">Error loading doctors</option>';
    });
}
