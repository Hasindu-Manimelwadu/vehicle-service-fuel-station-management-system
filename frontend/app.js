// =====================================================================
// Staff Management Admin — talks to the Spring Boot REST API.
// No frameworks: plain fetch() calls against /api/staff, /api/shifts,
// /api/attendance, /api/salaries.
// =====================================================================

let API_BASE = document.getElementById('apiBase').value.replace(/\/$/, '');

// ---------------------------------------------------------------------
// Entity configuration: each entry describes how to render its table
// and its add/edit form. This is the only place you'd touch to add a
// new field.
// ---------------------------------------------------------------------
const ENTITIES = {
  staff: {
    endpoint: '/staff',
    idField: 'staffId',
    tbody: 'staff-tbody',
    columns: 10,
    label: 'staff member',
    fields: [
      { key: 'staffId',      label: 'Staff ID',      type: 'text',   editOnly: false, readOnlyOnEdit: true, placeholder: 'auto-generated if left blank' },
      { key: 'userId',       label: 'User ID',       type: 'text' },
      { key: 'employeeNo',   label: 'Employee No',   type: 'text',   required: true },
      { key: 'fullName',     label: 'Full name',     type: 'text',   required: true },
      { key: 'phone',        label: 'Phone',         type: 'text',   required: true },
      { key: 'email',        label: 'Email',         type: 'email',  required: true },
      { key: 'designation',  label: 'Designation',   type: 'text',   required: true },
      { key: 'dateJoined',   label: 'Date joined',   type: 'date',   required: true },
      { key: 'employmentStatus', label: 'Employment status', type: 'select', required: true,
        options: ['ACTIVE', 'ON_LEAVE', 'SUSPENDED', 'TERMINATED'] },
      { key: 'basicSalary',  label: 'Basic salary',  type: 'number', required: true, step: '0.01' },
    ],
    row(item) {
      return `
        <td class="id-cell">${item.staffId}</td>
        <td>${item.employeeNo}</td>
        <td>${item.fullName}</td>
        <td>${item.designation}</td>
        <td>${item.phone}</td>
        <td>${item.email}</td>
        <td>${item.dateJoined ?? ''}</td>
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
    fields: [
      { key: 'shiftId',    label: 'Shift ID', type: 'text', readOnlyOnEdit: true, placeholder: 'auto-generated if left blank' },
      { key: 'staffId',    label: 'Staff ID', type: 'text', required: true },
      { key: 'shiftDate',  label: 'Shift date', type: 'date', required: true },
      { key: 'startTime',  label: 'Start time', type: 'time', required: true },
      { key: 'endTime',    label: 'End time', type: 'time', required: true },
      { key: 'shiftStatus', label: 'Status', type: 'select', required: true,
        options: ['SCHEDULED', 'ONGOING', 'COMPLETED', 'CANCELLED'] },
    ],
    row(item) {
      return `
        <td class="id-cell">${item.shiftId}</td>
        <td class="id-cell">${item.staffId}</td>
        <td>${item.shiftDate ?? ''}</td>
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
    fields: [
      { key: 'attendanceId', label: 'Attendance ID', type: 'text', readOnlyOnEdit: true, placeholder: 'auto-generated if left blank' },
      { key: 'staffId',      label: 'Staff ID', type: 'text', required: true },
      { key: 'date',         label: 'Date', type: 'date', required: true },
      { key: 'checkInTime',  label: 'Check-in time', type: 'time' },
      { key: 'checkOutTime', label: 'Check-out time', type: 'time' },
      { key: 'workingHours', label: 'Working hours', type: 'number', step: '0.01' },
      { key: 'attendanceStatus', label: 'Status', type: 'select', required: true,
        options: ['PRESENT', 'ABSENT', 'LATE', 'HALF_DAY', 'ON_LEAVE'] },
    ],
    row(item) {
      return `
        <td class="id-cell">${item.attendanceId}</td>
        <td class="id-cell">${item.staffId}</td>
        <td>${item.date ?? ''}</td>
        <td>${trimTime(item.checkInTime)}</td>
        <td>${trimTime(item.checkOutTime)}</td>
        <td>${item.workingHours ?? ''}</td>
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
    fields: [
      { key: 'salaryId',    label: 'Salary ID', type: 'text', readOnlyOnEdit: true, placeholder: 'auto-generated if left blank' },
      { key: 'staffId',     label: 'Staff ID', type: 'text', required: true },
      { key: 'month',       label: 'Month', type: 'text', required: true, placeholder: 'YYYY-MM' },
      { key: 'basicSalary', label: 'Basic salary', type: 'number', required: true, step: '0.01' },
      { key: 'overtimePay', label: 'Overtime pay', type: 'number', step: '0.01' },
      { key: 'deductions',  label: 'Deductions', type: 'number', step: '0.01' },
      { key: 'paymentDate', label: 'Payment date', type: 'date' },
      { key: 'paymentStatus', label: 'Payment status', type: 'select', required: true,
        options: ['PENDING', 'PAID', 'FAILED'] },
    ],
    row(item) {
      return `
        <td class="id-cell">${item.salaryId}</td>
        <td class="id-cell">${item.staffId}</td>
        <td>${item.month ?? ''}</td>
        <td>${money(item.basicSalary)}</td>
        <td>${money(item.overtimePay)}</td>
        <td>${money(item.deductions)}</td>
        <td><strong>${money(item.netSalary)}</strong></td>
        <td>${statusPill(item.paymentStatus)}</td>
        <td>${actionsCell('salary', item.salaryId)}</td>
      `;
    }
  }
};

// ---------------------------------------------------------------------
// helpers
// ---------------------------------------------------------------------
function money(v){
  if (v === null || v === undefined || v === '') return '—';
  return 'Rs. ' + Number(v).toLocaleString('en-LK', {minimumFractionDigits:2, maximumFractionDigits:2});
}
function trimTime(t){
  if (!t) return '—';
  return t.length >= 5 ? t.substring(0,5) : t;
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
  return `<span class="status-pill ${cls}">${status.replaceAll('_',' ')}</span>`;
}
function actionsCell(entityKey, id){
  return `
    <div class="row-actions">
      <button class="icon-btn" onclick="openForm('${entityKey}', '${id}')">Edit</button>
      <button class="icon-btn danger" onclick="deleteRecord('${entityKey}', '${id}')">Delete</button>
    </div>
  `;
}
function showToast(message, isError){
  const toast = document.getElementById('toast');
  toast.textContent = message;
  toast.className = 'toast show' + (isError ? ' error' : '');
  clearTimeout(showToast._t);
  showToast._t = setTimeout(() => { toast.className = 'toast'; }, 2600);
}

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
// data loading
// ---------------------------------------------------------------------
async function loadAll(){
  await Promise.all(Object.keys(ENTITIES).map(loadEntity));
}

async function loadEntity(key){
  const cfg = ENTITIES[key];
  const tbody = document.getElementById(cfg.tbody);
  try {
    const res = await fetch(API_BASE + cfg.endpoint);
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const data = await res.json();
    setConnState(true);
    if (!data.length) {
      tbody.innerHTML = `<tr class="empty-row"><td colspan="${cfg.columns}">No ${cfg.label} records yet.</td></tr>`;
      return;
    }
    tbody.innerHTML = data.map(item => `<tr>${cfg.row(item)}</tr>`).join('');
    // stash records for edit-form pre-fill
    cfg._cache = Object.fromEntries(data.map(i => [String(i[cfg.idField]), i]));
  } catch (err) {
    setConnState(false);
    tbody.innerHTML = `<tr class="empty-row"><td colspan="${cfg.columns}">Could not reach the API (${err.message}). Check the API base URL and that the backend is running.</td></tr>`;
  }
}

function setConnState(ok){
  const el = document.getElementById('connState');
  el.textContent = ok ? 'connected' : 'not connected';
  el.className = 'conn-state ' + (ok ? 'ok' : 'bad');
}

document.getElementById('apiBase').addEventListener('change', e => {
  API_BASE = e.target.value.replace(/\/$/, '');
  loadAll();
});

// ---------------------------------------------------------------------
// modal / form
// ---------------------------------------------------------------------
const overlay = document.getElementById('overlay');
const modalForm = document.getElementById('modal-form');
const modalTitle = document.getElementById('modal-title');
const modalError = document.getElementById('modal-error');
let activeEntityKey = null;
let activeRecordId = null;

document.querySelectorAll('[data-open-form]').forEach(btn => {
  btn.addEventListener('click', () => openForm(btn.dataset.openForm, null));
});
document.getElementById('modal-close').addEventListener('click', closeForm);
overlay.addEventListener('click', e => { if (e.target === overlay) closeForm(); });

function openForm(entityKey, recordId){
  activeEntityKey = entityKey;
  activeRecordId = recordId;
  const cfg = ENTITIES[entityKey];
  const existing = recordId ? (cfg._cache || {})[String(recordId)] : null;

  modalTitle.textContent = (existing ? 'Edit ' : 'Add ') + cfg.label;
  modalError.textContent = '';

  modalForm.innerHTML = cfg.fields.map(f => {
    const val = existing ? (existing[f.key] ?? '') : '';
    const disabled = (existing && f.readOnlyOnEdit) ? 'disabled' : '';
    if (f.type === 'select') {
      return `
        <div class="field">
          <label>${f.label}${f.required ? ' *' : ''}</label>
          <select name="${f.key}" ${f.required ? 'required' : ''}>
            ${f.options.map(o => `<option value="${o}" ${o === val ? 'selected' : ''}>${o.replaceAll('_',' ')}</option>`).join('')}
          </select>
        </div>`;
    }
    return `
      <div class="field">
        <label>${f.label}${f.required ? ' *' : ''}</label>
        <input
          name="${f.key}"
          type="${f.type}"
          value="${val ?? ''}"
          ${f.step ? `step="${f.step}"` : ''}
          ${f.placeholder ? `placeholder="${f.placeholder}"` : ''}
          ${f.required ? 'required' : ''}
          ${disabled}
        >
      </div>`;
  }).join('') + `
    <div class="form-actions">
      <button type="button" class="btn-secondary" id="cancelBtn">Cancel</button>
      <button type="submit" class="btn-primary">${existing ? 'Save changes' : 'Create'}</button>
    </div>
  `;

  modalForm.querySelector('#cancelBtn').addEventListener('click', closeForm);
  overlay.classList.add('is-open');
}

function closeForm(){
  overlay.classList.remove('is-open');
  activeEntityKey = null;
  activeRecordId = null;
}

modalForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const cfg = ENTITIES[activeEntityKey];
  const formData = new FormData(modalForm);
  const payload = {};
  cfg.fields.forEach(f => {
    let v = formData.get(f.key);
    if (v === '' || v === null) { payload[f.key] = null; return; }
    if (f.type === 'number') v = Number(v);
    payload[f.key] = v;
  });

  const isEdit = !!activeRecordId;
  const url = API_BASE + cfg.endpoint + (isEdit ? '/' + activeRecordId : '');
  const method = isEdit ? 'PUT' : 'POST';

  try {
    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    const body = await res.json().catch(() => null);
    if (!res.ok) {
      throw new Error((body && body.error) ? body.error : 'Request failed (' + res.status + ')');
    }
    closeForm();
    showToast(isEdit ? 'Changes saved.' : 'Record created.');
    loadEntity(activeEntityKey);
  } catch (err) {
    modalError.textContent = err.message;
  }
});

// ---------------------------------------------------------------------
// delete
// ---------------------------------------------------------------------
async function deleteRecord(entityKey, id){
  const cfg = ENTITIES[entityKey];
  if (!confirm(`Delete this ${cfg.label}? This cannot be undone.`)) return;
  try {
    const res = await fetch(API_BASE + cfg.endpoint + '/' + id, { method: 'DELETE' });
    if (!res.ok && res.status !== 204) {
      const body = await res.json().catch(() => null);
      throw new Error((body && body.error) ? body.error : 'Delete failed (' + res.status + ')');
    }
    showToast('Record deleted.');
    loadEntity(entityKey);
  } catch (err) {
    showToast(err.message, true);
  }
}

// expose for inline onclick handlers
window.openForm = openForm;
window.deleteRecord = deleteRecord;

// ---------------------------------------------------------------------
// init
// ---------------------------------------------------------------------
loadAll();
