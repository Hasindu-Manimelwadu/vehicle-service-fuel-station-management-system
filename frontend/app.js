// =====================================================================
// Staff Management Admin — talks to the Spring Boot REST API.
// No frameworks: plain fetch() calls against /api/staff, /api/shifts,
// /api/attendance, /api/salaries.
//
// Validation happens twice:
//   1. here in the browser (instant feedback, inline messages), and
//   2. on the server (the real authority). Server field errors are also
//      shown inline under the matching input.
// =====================================================================

// The API address depends on how the page was opened:
//   * docker compose (nginx, http://localhost:8081)  -> same origin /api
//   * IntelliJ / VS Code Live Server / file://        -> backend directly on :8080
// Instead of guessing, we probe each candidate and use the first one that
// actually answers like our API. A URL typed into the sidebar box wins.
const apiInput = document.getElementById('apiBase');
let API_BASE = apiInput.value.replace(/\/$/, '');

async function looksLikeApi(base){
  try {
    const ctrl = new AbortController();
    const t = setTimeout(() => ctrl.abort(), 2500);
    const res = await fetch(base + '/staff', { signal: ctrl.signal });
    clearTimeout(t);
    if (!res.ok) return false;
    return Array.isArray(await res.json());
  } catch (_) { return false; }
}

async function detectApiBase(){
  let saved = null;
  try { saved = localStorage.getItem('apiBase'); } catch (_) {}
  const candidates = [];
  if (saved) candidates.push(saved);
  if (location.protocol.startsWith('http')) candidates.push(location.origin + '/api');
  candidates.push('http://localhost:8080/api', 'http://127.0.0.1:8080/api');
  for (const c of [...new Set(candidates)]) {
    if (await looksLikeApi(c)) { API_BASE = c; apiInput.value = c; return true; }
  }
  API_BASE = 'http://localhost:8080/api';
  apiInput.value = API_BASE;
  return false;
}

// Clear message for errors that don't come from our backend
function httpErrorMessage(res, body){
  if (body && body.error) return body.error;
  if (res.status === 404) return `API not found at ${API_BASE} (404). Check the "API base URL" box at the bottom-left — it should point to the backend, e.g. http://localhost:8080/api.`;
  if (res.status === 502 || res.status === 503) return `The backend is not running or still starting (${res.status}).`;
  return `Request to ${API_BASE} failed (${res.status}). Check the "API base URL" box at the bottom-left points to the backend, e.g. http://localhost:8080/api.`;
}

// ---------------------------------------------------------------------
// validation helpers
// ---------------------------------------------------------------------
const RX = {
  id:        /^[A-Za-z0-9_-]*$/,
  employeeNo:/^EMP-\d{4,6}$/,
  name:      /^[A-Za-z][A-Za-z .'-]*$/,
  phone:     /^(0|\+94)\d{9}$/,
  email:     /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/,
  month:     /^\d{4}-(0[1-9]|1[0-2])$/,
  money:     /^\d{1,10}(\.\d{1,2})?$/,
};
function todayStr(){
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
}
function thisMonthStr(){ return todayStr().substring(0,7); }
function toMinutes(t){ if (!t) return null; const [h,m] = t.split(':').map(Number); return h*60 + m; }
// minutes from start to end; an end earlier than the start runs past midnight
function spanMinutes(start, end){
  const a = toMinutes(start), b = toMinutes(end);
  if (a === null || b === null || a === b) return 0;
  return b > a ? b - a : b + 24*60 - a;
}
function staffById(id){ return (ENTITIES.staff._cache || {})[id]; }

const v = {
  optionalId: (label) => (val) => {
    if (!val) return null;
    if (val.length > 20) return `${label} must be at most 20 characters`;
    if (!RX.id.test(val)) return `${label} may contain only letters, digits, '-' and '_'`;
    return null;
  },
  required: (label) => (val) => (val === null || val === undefined || String(val).trim() === '') ? `${label} is required` : null,
  money: (label, {min = 0, allowZero = true, max = 10000000} = {}) => (val) => {
    if (val === '' || val === null) return null;
    if (!RX.money.test(String(val))) return `${label} must be a positive number with at most 2 decimals`;
    const n = Number(val);
    if (!allowZero && n <= 0) return `${label} must be greater than 0`;
    if (n < min) return `${label} cannot be negative`;
    if (n > max) return `${label} cannot exceed ${max.toLocaleString()}`;
    return null;
  },
};

// ---------------------------------------------------------------------
// Entity configuration: how to render each table and its add/edit form.
// field.validate(value, allValues, isEdit) -> error message or null
// form-level cfg.validate(allValues, isEdit) -> {field: message}
// ---------------------------------------------------------------------
const ENTITIES = {
  staff: {
    endpoint: '/staff',
    idField: 'staffId',
    tbody: 'staff-tbody',
    columns: 10,
    label: 'staff member',
    searchKeys: ['staffId','employeeNo','fullName','designation','phone','email','employmentStatus'],
    fields: [
      { key: 'staffId',     label: 'Staff ID',    type: 'text', readOnlyOnEdit: true, placeholder: 'auto-generated if left blank', maxlength: 20,
        validate: v.optionalId('Staff ID') },
      { key: 'userId',      label: 'User ID',     type: 'text', maxlength: 20, placeholder: 'optional, e.g. USR010',
        validate: v.optionalId('User ID') },
      { key: 'employeeNo',  label: 'Employee No', type: 'text', required: true, placeholder: 'EMP-1001', maxlength: 20,
        validate: val => RX.employeeNo.test(val) ? null : 'Employee number must look like EMP-1001' },
      { key: 'fullName',    label: 'Full name',   type: 'text', required: true, maxlength: 100,
        validate: val => {
          const t = val.trim();
          if (t.length < 3) return 'Full name must be at least 3 characters';
          if (!RX.name.test(t)) return 'Full name may contain only letters, spaces, dots, apostrophes and hyphens';
          return null;
        } },
      { key: 'phone',       label: 'Phone',       type: 'tel', required: true, placeholder: '0771234567', maxlength: 12,
        validate: val => RX.phone.test(val.trim()) ? null : 'Phone must be a valid Sri Lankan number, e.g. 0771234567 or +94771234567' },
      { key: 'email',       label: 'Email',       type: 'email', required: true, maxlength: 100,
        validate: val => RX.email.test(val.trim()) ? null : 'Enter a valid email address' },
      { key: 'designation', label: 'Designation', type: 'text', required: true, maxlength: 50,
        validate: val => val.trim().length < 2 ? 'Designation must be at least 2 characters' : null },
      { key: 'dateJoined',  label: 'Date joined', type: 'date', required: true, max: todayStr,
        validate: val => val > todayStr() ? 'Date joined cannot be in the future' : null },
      { key: 'employmentStatus', label: 'Employment status', type: 'select', required: true,
        options: ['ACTIVE', 'ON_LEAVE', 'SUSPENDED', 'TERMINATED'] },
      { key: 'basicSalary', label: 'Basic salary (Rs.)', type: 'number', required: true, step: '0.01', min: '0',
        validate: v.money('Basic salary', {allowZero: false}) },
    ],
    row(item) {
      return `
        <td class="id-cell">${esc(item.staffId)}</td>
        <td>${esc(item.employeeNo)}</td>
        <td>${esc(item.fullName)}</td>
        <td>${esc(item.designation)}</td>
        <td>${esc(item.phone)}</td>
        <td>${esc(item.email)}</td>
        <td>${esc(item.dateJoined)}</td>
        <td>${statusPill(item.employmentStatus)}</td>
        <td>${money(item.basicSalary)}</td>
        <td>${actionsCell('staff', item.staffId)}</td>
      `;
    }
  },

  shift: {
    endpoint: '/shifts',
    idField: 'shiftId',
    tbody: 'shift-tbody',
    columns: 7,
    label: 'work shift',
    searchKeys: ['shiftId','staffId','shiftDate','shiftStatus'],
    fields: [
      { key: 'shiftId',    label: 'Shift ID', type: 'text', readOnlyOnEdit: true, placeholder: 'auto-generated if left blank', maxlength: 20,
        validate: v.optionalId('Shift ID') },
      { key: 'staffId',    label: 'Staff member', type: 'staff', required: true },
      { key: 'shiftDate',  label: 'Shift date', type: 'date', required: true },
      { key: 'startTime',  label: 'Start time', type: 'time', required: true },
      { key: 'endTime',    label: 'End time', type: 'time', required: true, hint: 'An end time earlier than the start means an overnight shift.' },
      { key: 'shiftStatus', label: 'Status', type: 'select', required: true,
        options: ['SCHEDULED', 'ONGOING', 'COMPLETED', 'CANCELLED'] },
    ],
    validate(d, isEdit) {
      const e = {};
      const staff = staffById(d.staffId);
      if (staff && staff.employmentStatus === 'TERMINATED' && d.shiftStatus !== 'CANCELLED')
        e.staffId = 'Cannot assign shifts to a terminated staff member';
      if (staff && d.shiftDate && d.shiftDate < staff.dateJoined)
        e.shiftDate = `Shift date is before the staff member joined (${staff.dateJoined})`;
      if (!isEdit && d.shiftStatus === 'SCHEDULED' && d.shiftDate && d.shiftDate < todayStr())
        e.shiftDate = 'A new scheduled shift cannot be in the past';
      const s = toMinutes(d.startTime); let en = toMinutes(d.endTime);
      if (s !== null && en !== null) {
        if (s === en) e.endTime = 'End time must be different from start time';
        else {
          if (en < s) en += 1440;
          if (en - s > 16*60) e.endTime = 'A shift cannot be longer than 16 hours';
        }
      }
      return e;
    },
    row(item) {
      return `
        <td class="id-cell">${esc(item.shiftId)}</td>
        <td>${staffLabel(item.staffId)}</td>
        <td>${esc(item.shiftDate)}</td>
        <td>${trimTime(item.startTime)}</td>
        <td>${trimTime(item.endTime)}</td>
        <td>${statusPill(item.shiftStatus)}</td>
        <td>${actionsCell('shift', item.shiftId)}</td>
      `;
    }
  },

  attendance: {
    endpoint: '/attendance',
    idField: 'attendanceId',
    tbody: 'attendance-tbody',
    columns: 8,
    label: 'attendance record',
    searchKeys: ['attendanceId','staffId','date','attendanceStatus'],
    fields: [
      { key: 'attendanceId', label: 'Attendance ID', type: 'text', readOnlyOnEdit: true, placeholder: 'auto-generated if left blank', maxlength: 20,
        validate: v.optionalId('Attendance ID') },
      { key: 'staffId',      label: 'Staff member', type: 'staff', required: true },
      { key: 'date',         label: 'Date', type: 'date', required: true, max: todayStr,
        validate: val => val > todayStr() ? 'Attendance cannot be recorded for a future date' : null },
      { key: 'attendanceStatus', label: 'Status', type: 'select', required: true,
        options: ['PRESENT', 'ABSENT', 'LATE', 'HALF_DAY', 'ON_LEAVE'] },
      { key: 'checkInTime',  label: 'Check-in time', type: 'time' },
      { key: 'checkOutTime', label: 'Check-out time', type: 'time' },
      { key: 'workingHours', label: 'Working hours', type: 'number', computed: true, hint: 'Calculated automatically from check-in / check-out (a check-out earlier than check-in means past midnight). Hours beyond the scheduled shift (or the standard day if no shift) count as overtime on that month\'s salary.' },
    ],
    validate(d) {
      const e = {};
      const staff = staffById(d.staffId);
      if (staff && d.date && d.date < staff.dateJoined)
        e.date = `Date is before the staff member joined (${staff.dateJoined})`;
      const off = d.attendanceStatus === 'ABSENT' || d.attendanceStatus === 'ON_LEAVE';
      if (off) {
        if (d.checkInTime || d.checkOutTime) e.checkInTime = 'Leave check-in/out empty for this status';
        return e;
      }
      if (!d.checkInTime) e.checkInTime = 'Check-in time is required for this status';
      if (d.checkOutTime && !d.checkInTime) e.checkOutTime = 'Enter a check-in time first';
      if (d.checkInTime && d.checkOutTime) {
        const span = spanMinutes(d.checkInTime, d.checkOutTime);
        if (span === 0) e.checkOutTime = 'Check-out must be different from check-in';
        else if (span > 20*60) e.checkOutTime = 'More than 20 hours of work - check the times';
      }
      return e;
    },
    // live behaviour: disable times for ABSENT/ON_LEAVE, compute hours
    onChange(form) {
      const status = form.elements.attendanceStatus.value;
      const off = status === 'ABSENT' || status === 'ON_LEAVE';
      ['checkInTime','checkOutTime'].forEach(k => {
        const el = form.elements[k];
        el.disabled = off;
        if (off) el.value = '';
      });
      const a = toMinutes(form.elements.checkInTime.value), b = toMinutes(form.elements.checkOutTime.value);
      const span = (a !== null && b !== null) ? spanMinutes(form.elements.checkInTime.value, form.elements.checkOutTime.value) : 0;
      form.elements.workingHours.value = off ? '0.00' : span > 0 ? (span / 60).toFixed(2) : '';
    },
    row(item) {
      return `
        <td class="id-cell">${esc(item.attendanceId)}</td>
        <td>${staffLabel(item.staffId)}</td>
        <td>${esc(item.date)}</td>
        <td>${trimTime(item.checkInTime)}</td>
        <td>${trimTime(item.checkOutTime)}</td>
        <td>${hoursCell(item.workingHours, item.overtimeHours)}</td>
        <td>${statusPill(item.attendanceStatus)}</td>
        <td>${actionsCell('attendance', item.attendanceId)}</td>
      `;
    }
  },

  salary: {
    endpoint: '/salaries',
    idField: 'salaryId',
    tbody: 'salary-tbody',
    columns: 9,
    label: 'salary record',
    searchKeys: ['salaryId','staffId','month','paymentStatus'],
    fields: [
      { key: 'salaryId',    label: 'Salary ID', type: 'text', readOnlyOnEdit: true, placeholder: 'auto-generated if left blank', maxlength: 20,
        validate: v.optionalId('Salary ID') },
      { key: 'staffId',     label: 'Staff member', type: 'staff', required: true },
      { key: 'month',       label: 'Month', type: 'month', required: true, max: thisMonthStr,
        validate: val => {
          if (!RX.month.test(val)) return 'Month must be in YYYY-MM format';
          if (val > thisMonthStr()) return 'Salary month cannot be in the future';
          return null;
        } },
      { key: 'basicSalary', label: 'Basic salary (Rs.)', type: 'number', required: true, step: '0.01', min: '0',
        validate: v.money('Basic salary', {allowZero: false}) },
      { key: 'overtimeHours', label: 'Overtime hours', type: 'number', computed: true,
        hint: 'Calculated from attendance: hours worked beyond the standard day, for the selected month.' },
      { key: 'overtimePay', label: 'Overtime pay (Rs.)', type: 'number', computed: true,
        hint: 'Calculated automatically.' },
      { key: 'deductions',  label: 'Deductions (Rs.)', type: 'number', step: '0.01', min: '0',
        validate: v.money('Deductions') },
      { key: 'netSalary',   label: 'Net salary (Rs.)', type: 'number', computed: true, hint: 'Basic + overtime − deductions (calculated automatically).' },
      { key: 'paymentStatus', label: 'Payment status', type: 'select', required: true,
        options: ['PENDING', 'PAID', 'FAILED'] },
      { key: 'paymentDate', label: 'Payment date', type: 'date', max: todayStr },
    ],
    validate(d) {
      const e = {};
      const staff = staffById(d.staffId);
      if (staff && d.month && d.month < staff.dateJoined.substring(0,7))
        e.month = `Month is before the staff member joined (${staff.dateJoined})`;
      const net = Number(d.basicSalary || 0) + Number(d.overtimePay || 0) - Number(d.deductions || 0);
      if (net < 0) e.deductions = 'Deductions cannot be greater than basic salary + overtime';
      if (d.paymentStatus === 'PAID' && !d.paymentDate) e.paymentDate = 'Payment date is required when status is PAID';
      if (d.paymentDate) {
        if (d.paymentDate > todayStr()) e.paymentDate = 'Payment date cannot be in the future';
        else if (d.month && d.paymentDate < d.month + '-01') e.paymentDate = 'Payment date cannot be before the salary month';
      }
      return e;
    },
    onChange(form, changedName, isEdit) {
      // pre-fill basic salary from the selected staff member on new records
      if (changedName === 'staffId' && !isEdit) {
        const s = staffById(form.elements.staffId.value);
        if (s) form.elements.basicSalary.value = s.basicSalary;
      }
      // overtime depends on staff + month + basic -> ask the server
      if (changedName === null || ['staffId','month','basicSalary'].includes(changedName)) {
        refreshOvertimePreview(form);
      }
      updateNetPreview(form);
    },
    row(item) {
      return `
        <td class="id-cell">${esc(item.salaryId)}</td>
        <td>${staffLabel(item.staffId)}</td>
        <td>${esc(item.month)}</td>
        <td>${money(item.basicSalary)}</td>
        <td>${money(item.overtimePay)}${Number(item.overtimeHours) > 0 ? ` <span class="muted">(${Number(item.overtimeHours).toFixed(2)} h)</span>` : ''}</td>
        <td>${money(item.deductions)}</td>
        <td><strong>${money(item.netSalary)}</strong></td>
        <td>${statusPill(item.paymentStatus)}</td>
        <td>${actionsCell('salary', item.salaryId)}</td>
      `;
    }
  }
};

// ---------------------------------------------------------------------
// overtime (calculated by the server from attendance)
// ---------------------------------------------------------------------
let OT_RULES = { standardDailyHours: 8, monthlyHoursDivisor: 240, overtimeMultiplier: 1.5 };
async function loadOvertimeRules(){
  try {
    const res = await fetch(API_BASE + '/salaries/overtime-rules');
    if (res.ok) OT_RULES = await res.json();
  } catch (_) { /* keep defaults */ }
}

function updateNetPreview(form){
  const n = Number(form.elements.basicSalary.value || 0) + Number(form.elements.overtimePay.value || 0) - Number(form.elements.deductions.value || 0);
  form.elements.netSalary.value = form.elements.basicSalary.value ? n.toFixed(2) : '';
}

function setOtHint(text){
  const el = modalForm.querySelector('[data-hint="overtimePay"]');
  if (el) el.textContent = text;
}

let _otTimer = null, _otSeq = 0;
function refreshOvertimePreview(form){
  clearTimeout(_otTimer);
  _otTimer = setTimeout(async () => {
    const staffId = form.elements.staffId.value;
    const month = form.elements.month.value;
    const basic = form.elements.basicSalary.value;
    if (!staffId || !RX.month.test(month)) {
      form.elements.overtimeHours.value = '';
      form.elements.overtimePay.value = '';
      setOtHint('Select a staff member and month to calculate overtime.');
      updateNetPreview(form);
      return;
    }
    const seq = ++_otSeq;
    const qs = new URLSearchParams({ staffId, month });
    if (basic && RX.money.test(basic)) qs.set('basicSalary', basic);
    try {
      const res = await fetch(`${API_BASE}/salaries/overtime-preview?${qs}`);
      const body = await res.json().catch(() => null);
      if (seq !== _otSeq || !overlay.classList.contains('is-open')) return;   // stale response
      if (!res.ok) throw new Error(body && body.error || 'HTTP ' + res.status);
      form.elements.overtimeHours.value = Number(body.overtimeHours).toFixed(2);
      form.elements.overtimePay.value = Number(body.overtimePay).toFixed(2);
      let hint;
      if (Number(body.overtimeHours) > 0) {
        hint = `${Number(body.overtimeHours).toFixed(2)} h over ${body.daysWithOvertime} day(s) × Rs. ${Number(body.overtimeRate).toFixed(2)}/h `
          + `(${OT_RULES.overtimeMultiplier}× the hourly rate of basic ÷ ${OT_RULES.monthlyHoursDivisor}) - added to net salary.`;
      } else if (!body.attendanceDays) {
        hint = `No attendance recorded for this staff member in ${month}, so there is no overtime yet. It will be added automatically when attendance is entered.`;
      } else {
        hint = `No hours beyond the scheduled shift (or ${OT_RULES.standardDailyHours} h if no shift) in ${month} attendance.`;
      }
      if (body.daysMissingCheckout > 0)
        hint += ` ${body.daysMissingCheckout} day(s) have no check-out yet and are not counted.`;
      setOtHint(hint);
    } catch (err) {
      if (seq !== _otSeq) return;
      form.elements.overtimeHours.value = '';
      form.elements.overtimePay.value = '';
      setOtHint('Could not calculate overtime: ' + err.message);
    }
    updateNetPreview(form);
  }, 250);
}

function hoursCell(h, otFromServer){
  if (h === null || h === undefined || h === '') return '—';
  const ot = (otFromServer !== null && otFromServer !== undefined)
    ? Number(otFromServer)
    : Number(h) - Number(OT_RULES.standardDailyHours);
  return esc(Number(h).toFixed(2)) + (ot > 0 ? ` <span class="ot-badge">+${ot.toFixed(2)} OT</span>` : '');
}

// ---------------------------------------------------------------------
// render helpers
// ---------------------------------------------------------------------
function esc(s){
  if (s === null || s === undefined) return '';
  return String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
}
function money(val){
  if (val === null || val === undefined || val === '') return '—';
  return 'Rs. ' + Number(val).toLocaleString('en-LK', {minimumFractionDigits:2, maximumFractionDigits:2});
}
function trimTime(t){
  if (!t) return '—';
  return esc(t.length >= 5 ? t.substring(0,5) : t);
}
function staffLabel(id){
  const s = staffById(id);
  return s ? `<span class="id-cell">${esc(id)}</span> <span class="muted">${esc(s.fullName)}</span>`
           : `<span class="id-cell">${esc(id)}</span>`;
}
function statusPill(status){
  if (!status) return '—';
  const good = ['ACTIVE','COMPLETED','PAID','PRESENT','SCHEDULED'];
  const warn = ['ON_LEAVE','PENDING','ONGOING','LATE','HALF_DAY'];
  const bad  = ['SUSPENDED','TERMINATED','FAILED','ABSENT','CANCELLED'];
  let cls = '';
  if (good.includes(status)) cls = 'good';
  else if (warn.includes(status)) cls = 'warn';
  else if (bad.includes(status)) cls = 'bad';
  return `<span class="status-pill ${cls}">${esc(status.replaceAll('_',' '))}</span>`;
}
function actionsCell(entityKey, id){
  return `
    <div class="row-actions">
      <button class="icon-btn" data-edit="${esc(entityKey)}" data-id="${esc(id)}">Edit</button>
      <button class="icon-btn danger" data-delete="${esc(entityKey)}" data-id="${esc(id)}">Delete</button>
    </div>
  `;
}
function showToast(message, isError){
  const toast = document.getElementById('toast');
  toast.textContent = message;
  toast.className = 'toast show' + (isError ? ' error' : '');
  clearTimeout(showToast._t);
  showToast._t = setTimeout(() => { toast.className = 'toast'; }, 3000);
}

// Edit/Delete buttons (event delegation instead of inline onclick)
document.querySelector('.workspace').addEventListener('click', e => {
  const btn = e.target.closest('button[data-id]');
  if (!btn) return;
  if (btn.dataset.edit) openForm(btn.dataset.edit, btn.dataset.id);
  if (btn.dataset.delete) deleteRecord(btn.dataset.delete, btn.dataset.id);
});

// ---------------------------------------------------------------------
// navigation between panels
// ---------------------------------------------------------------------
document.querySelectorAll('.rail-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.rail-btn').forEach(b => b.classList.remove('is-active'));
    document.querySelectorAll('.panel').forEach(p => p.classList.remove('is-active'));
    btn.classList.add('is-active');
    document.getElementById('panel-' + btn.dataset.panel).classList.add('is-active');
  });
});

// ---------------------------------------------------------------------
// data loading + search
// ---------------------------------------------------------------------
async function loadAll(){
  await loadOvertimeRules();
  await loadEntity('staff');   // others use staff names for display
  await Promise.all(['shift','attendance','salary'].map(loadEntity));
}

async function loadEntity(key){
  const cfg = ENTITIES[key];
  const tbody = document.getElementById(cfg.tbody);
  try {
    const res = await fetch(API_BASE + cfg.endpoint);
    if (!res.ok) throw new Error('HTTP ' + res.status + ' from ' + API_BASE);
    const data = await res.json();
    setConnState(true);
    cfg._data = data;
    cfg._cache = Object.fromEntries(data.map(i => [String(i[cfg.idField]), i]));
    renderEntity(key);
  } catch (err) {
    setConnState(false);
    cfg._data = []; cfg._cache = {};
    tbody.innerHTML = `<tr class="empty-row"><td colspan="${cfg.columns}">Could not reach the API (${esc(err.message)}). Check the API base URL and that the backend is running.</td></tr>`;
  }
}

function renderEntity(key){
  const cfg = ENTITIES[key];
  const tbody = document.getElementById(cfg.tbody);
  const q = (document.querySelector(`[data-search="${key}"]`)?.value || '').trim().toLowerCase();
  const rows = (cfg._data || []).filter(item => {
    if (!q) return true;
    const extra = item.staffId && key !== 'staff' ? (staffById(item.staffId)?.fullName || '') : '';
    return cfg.searchKeys.some(k => String(item[k] ?? '').toLowerCase().includes(q)) || extra.toLowerCase().includes(q);
  });
  const countEl = document.querySelector(`[data-count="${key}"]`);
  if (countEl) countEl.textContent = `${rows.length} of ${(cfg._data || []).length}`;
  if (!rows.length) {
    tbody.innerHTML = `<tr class="empty-row"><td colspan="${cfg.columns}">${q ? 'No matching records.' : `No ${cfg.label} records yet.`}</td></tr>`;
    return;
  }
  tbody.innerHTML = rows.map(item => `<tr>${cfg.row(item)}</tr>`).join('');
}

document.querySelectorAll('[data-search]').forEach(inp => {
  inp.addEventListener('input', () => renderEntity(inp.dataset.search));
});

function setConnState(ok){
  const el = document.getElementById('connState');
  el.textContent = ok ? 'connected' : 'not connected';
  el.className = 'conn-state ' + (ok ? 'ok' : 'bad');
}

apiInput.addEventListener('change', e => {
  API_BASE = e.target.value.trim().replace(/\/$/, '');
  try { localStorage.setItem('apiBase', API_BASE); } catch (_) {}
  loadAll();
});

// ---------------------------------------------------------------------
// modal / form
// ---------------------------------------------------------------------
const overlay = document.getElementById('overlay');
const modalForm = document.getElementById('modal-form');
const modalTitle = document.getElementById('modal-title');
const modalError = document.getElementById('modal-error');
modalForm.setAttribute('novalidate', '');   // we show our own messages
let activeEntityKey = null;
let activeRecordId = null;

document.querySelectorAll('[data-open-form]').forEach(btn => {
  btn.addEventListener('click', () => openForm(btn.dataset.openForm, null));
});
document.getElementById('modal-close').addEventListener('click', closeForm);
overlay.addEventListener('click', e => { if (e.target === overlay) closeForm(); });
document.addEventListener('keydown', e => { if (e.key === 'Escape' && overlay.classList.contains('is-open')) closeForm(); });

function fieldHtml(f, val, existing){
  const disabled = (existing && f.readOnlyOnEdit) || f.computed ? 'disabled' : '';
  const label = `<label for="f-${f.key}">${esc(f.label)}${f.required ? ' <span class="req">*</span>' : ''}</label>`;
  const hint = f.hint ? `<small class="field-hint" data-hint="${f.key}">${esc(f.hint)}</small>` : '';
  const err = `<small class="field-error" data-err="${f.key}"></small>`;

  if (f.type === 'select') {
    return `<div class="field">${label}
      <select id="f-${f.key}" name="${f.key}">
        ${f.options.map(o => `<option value="${o}" ${o === val ? 'selected' : ''}>${o.replaceAll('_',' ')}</option>`).join('')}
      </select>${hint}${err}</div>`;
  }
  if (f.type === 'staff') {
    const staff = ENTITIES.staff._data || [];
    const opts = staff.map(s => {
      const note = s.employmentStatus !== 'ACTIVE' ? ` (${s.employmentStatus.replaceAll('_',' ').toLowerCase()})` : '';
      return `<option value="${esc(s.staffId)}" ${s.staffId === val ? 'selected' : ''}>${esc(s.staffId)} — ${esc(s.fullName)}${note}</option>`;
    }).join('');
    const missing = val && !staffById(val) ? `<option value="${esc(val)}" selected>${esc(val)} (not found)</option>` : '';
    return `<div class="field">${label}
      <select id="f-${f.key}" name="${f.key}">
        <option value="">— select staff member —</option>${missing}${opts}
      </select>${staff.length ? '' : '<small class="field-hint">No staff yet — add a staff member first.</small>'}${hint}${err}</div>`;
  }
  const max = typeof f.max === 'function' ? f.max() : f.max;
  return `<div class="field">${label}
    <input id="f-${f.key}" name="${f.key}" type="${f.type}" value="${esc(val ?? '')}"
      ${f.step ? `step="${f.step}"` : ''} ${f.min ? `min="${f.min}"` : ''} ${max ? `max="${max}"` : ''}
      ${f.maxlength ? `maxlength="${f.maxlength}"` : ''}
      ${f.placeholder ? `placeholder="${esc(f.placeholder)}"` : ''} ${disabled}>
    ${hint}${err}</div>`;
}

function openForm(entityKey, recordId){
  activeEntityKey = entityKey;
  activeRecordId = recordId;
  const cfg = ENTITIES[entityKey];
  const existing = recordId ? (cfg._cache || {})[String(recordId)] : null;
  if (recordId && !existing) { showToast('Record not found — refreshing list.', true); loadEntity(entityKey); return; }

  modalTitle.textContent = (existing ? 'Edit ' : 'Add ') + cfg.label;
  modalError.textContent = '';

  modalForm.innerHTML = cfg.fields.map(f => {
    let val = existing ? (existing[f.key] ?? '') : '';
    if (f.type === 'time' && val) val = val.substring(0,5);
    return fieldHtml(f, val, existing);
  }).join('') + `
    <div class="form-actions">
      <button type="button" class="btn-secondary" id="cancelBtn">Cancel</button>
      <button type="submit" class="btn-primary" id="submitBtn">${existing ? 'Save changes' : 'Create'}</button>
    </div>
  `;

  modalForm.querySelector('#cancelBtn').addEventListener('click', closeForm);
  if (cfg.onChange) {
    cfg.onChange(modalForm, null, !!existing);
    modalForm.addEventListener('input', onFormInput);
    modalForm.addEventListener('change', onFormInput);
  }
  overlay.classList.add('is-open');
  modalForm.querySelector('input:not([disabled]), select')?.focus();
}

function onFormInput(e){
  const cfg = ENTITIES[activeEntityKey];
  if (cfg && cfg.onChange) cfg.onChange(modalForm, e.target.name, !!activeRecordId);
  // clear the error on the field being edited
  if (e.target.name) setFieldError(e.target.name, null);
}

function closeForm(){
  overlay.classList.remove('is-open');
  modalForm.removeEventListener('input', onFormInput);
  modalForm.removeEventListener('change', onFormInput);
  activeEntityKey = null;
  activeRecordId = null;
}

function setFieldError(key, msg){
  const errEl = modalForm.querySelector(`[data-err="${key}"]`);
  const input = modalForm.elements[key];
  if (errEl) errEl.textContent = msg || '';
  if (input) input.classList.toggle('invalid', !!msg);
}

function readForm(cfg){
  const data = {};
  cfg.fields.forEach(f => {
    const el = modalForm.elements[f.key];
    data[f.key] = el ? el.value.trim() : '';
  });
  return data;
}

// run every validator; returns {field: message}
function validateForm(cfg, data, isEdit){
  const errors = {};
  cfg.fields.forEach(f => {
    if (f.computed || (isEdit && f.readOnlyOnEdit)) return;
    const val = data[f.key];
    if (f.required && !val) { errors[f.key] = `${f.label.replace(/ \(Rs\.\)$/, '')} is required`; return; }
    if (val && f.validate) {
      const msg = f.validate(val, data, isEdit);
      if (msg) errors[f.key] = msg;
    }
  });
  if (cfg.validate) {
    const extra = cfg.validate(data, isEdit) || {};
    for (const k in extra) if (!errors[k]) errors[k] = extra[k];
  }
  return errors;
}

modalForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const entityKey = activeEntityKey;          // keep a copy: closeForm() resets it
  const recordId = activeRecordId;
  const cfg = ENTITIES[entityKey];
  const isEdit = !!recordId;
  const data = readForm(cfg);

  cfg.fields.forEach(f => setFieldError(f.key, null));
  modalError.textContent = '';

  const errors = validateForm(cfg, data, isEdit);
  if (Object.keys(errors).length) {
    for (const k in errors) setFieldError(k, errors[k]);
    modalError.textContent = 'Please fix the highlighted fields.';
    modalForm.querySelector('.invalid')?.focus();
    return;
  }

  const payload = {};
  cfg.fields.forEach(f => {
    if (f.computed) return;                   // server calculates these
    let val = data[f.key];
    if (val === '') { payload[f.key] = null; return; }
    if (f.type === 'number') val = Number(val);
    payload[f.key] = val;
  });
  if (isEdit) payload[cfg.idField] = recordId;

  const url = API_BASE + cfg.endpoint + (isEdit ? '/' + encodeURIComponent(recordId) : '');
  const submitBtn = modalForm.querySelector('#submitBtn');
  submitBtn.disabled = true;
  try {
    const res = await fetch(url, {
      method: isEdit ? 'PUT' : 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    const body = await res.json().catch(() => null);
    if (!res.ok) {
      if (body && body.fields) for (const k in body.fields) setFieldError(k, body.fields[k]);
      throw new Error(httpErrorMessage(res, body));
    }
    closeForm();
    showToast(isEdit ? 'Changes saved.' : 'Record created.');
    if (entityKey === 'staff') await loadAll();   // names shown in other tables
    else await loadEntity(entityKey);
    if (entityKey === 'attendance') await loadEntity('salary');  // overtime recalculated
    if (entityKey === 'shift') await Promise.all([loadEntity('attendance'), loadEntity('salary')]);  // shift length sets the normal day
  } catch (err) {
    modalError.textContent = err.message === 'Failed to fetch' ? 'Could not reach the API.' : err.message;
  } finally {
    submitBtn.disabled = false;
  }
});

// ---------------------------------------------------------------------
// delete
// ---------------------------------------------------------------------
async function deleteRecord(entityKey, id){
  const cfg = ENTITIES[entityKey];
  let msg = `Delete this ${cfg.label} (${id})? This cannot be undone.`;
  if (entityKey === 'staff') {
    const s = staffById(id);
    msg = `Delete ${s ? s.fullName : id}?\n\nThis also deletes all of their work shifts, attendance and salary records. This cannot be undone.`;
  }
  if (!confirm(msg)) return;
  try {
    const res = await fetch(API_BASE + cfg.endpoint + '/' + encodeURIComponent(id), { method: 'DELETE' });
    if (!res.ok && res.status !== 204) {
      const body = await res.json().catch(() => null);
      throw new Error(httpErrorMessage(res, body));
    }
    showToast('Record deleted.');
    if (entityKey === 'staff') await loadAll();   // cascade removed child rows
    else await loadEntity(entityKey);
    if (entityKey === 'attendance') await loadEntity('salary');  // overtime recalculated
    if (entityKey === 'shift') await Promise.all([loadEntity('attendance'), loadEntity('salary')]);  // shift length sets the normal day
  } catch (err) {
    showToast(err.message, true);
  }
}

// ---------------------------------------------------------------------
// init
// ---------------------------------------------------------------------
detectApiBase().then(loadAll);
