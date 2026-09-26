import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import Icon from "../components/Icon";
import PageHeader from "../components/PageHeader";
import { useToast } from "../components/ToastContext";
import useUnsavedChanges from "../hooks/useUnsavedChanges";

const blankForm = { bookingId: "", serviceDescription: "", customerComplaint: "", mileage: "", remarks: "" };

function validate(form, editing) {
  const errors = {};
  if (!editing) {
    const booking = Number(form.bookingId);
    if (!form.bookingId) errors.bookingId = "Booking reference is required.";
    else if (!Number.isInteger(booking) || booking <= 0) errors.bookingId = "Booking reference must be a positive whole number.";
  }
  const description = form.serviceDescription.trim();
  if (!description) errors.serviceDescription = "Service description is required.";
  else if (description.length > 500) errors.serviceDescription = "Maximum 500 characters allowed.";

  if (form.customerComplaint.length > 500) errors.customerComplaint = "Maximum 500 characters allowed.";

  if (form.mileage === "") errors.mileage = "Mileage is required.";
  else {
    const mileage = Number(form.mileage);
    if (!Number.isInteger(mileage) || mileage < 0) errors.mileage = "Mileage must be a non-negative whole number.";
  }

  if (form.remarks.length > 1000) errors.remarks = "Maximum 1000 characters allowed.";
  return errors;
}

export default function ServiceRecordForm() {
  const { id } = useParams();
  const editing = Boolean(id);
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [form, setForm] = useState(blankForm);
  const [initialForm, setInitialForm] = useState(blankForm);
  const [touched, setTouched] = useState({});
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!editing) return;
    api.get(`/api/service-records/${id}`)
      .then(({ data }) => {
        const next = {
          bookingId: data.bookingId ?? "",
          serviceDescription: data.serviceDescription || "",
          customerComplaint: data.customerComplaint || "",
          mileage: data.mileage ?? "",
          remarks: data.remarks || "",
        };
        setForm(next);
        setInitialForm(next);
      })
      .catch((e) => setError(e.response?.data?.message || "Unable to load record"));
  }, [editing, id]);

  const fieldErrors = useMemo(() => validate(form, editing), [form, editing]);
  const isDirty = JSON.stringify(form) !== JSON.stringify(initialForm);
  const guardNavigation = useUnsavedChanges(isDirty && !loading);

  const change = (e) => {
    const { name, value } = e.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const blur = (e) => setTouched((current) => ({ ...current, [e.target.name]: true }));
  const showFieldError = (name) => touched[name] && fieldErrors[name];

  const leaveForm = () => guardNavigation(() => navigate("/service-records"));

  const submit = async (event) => {
    event.preventDefault();
    const allTouched = Object.keys(form).reduce((acc, key) => ({ ...acc, [key]: true }), {});
    setTouched(allTouched);
    setError("");

    const errors = validate(form, editing);
    if (Object.keys(errors).length) {
      const firstField = Object.keys(errors)[0];
      document.querySelector(`[name="${firstField}"]`)?.focus();
      showToast("Please fix the highlighted fields before saving.", "error");
      return;
    }

    setLoading(true);
    const payload = {
      serviceDescription: form.serviceDescription.trim(),
      customerComplaint: form.customerComplaint.trim(),
      mileage: Number(form.mileage),
      remarks: form.remarks.trim(),
    };

    try {
      if (editing) await api.put(`/api/service-records/${id}`, payload);
      else await api.post("/api/service-records", { ...payload, bookingId: Number(form.bookingId) });
      setInitialForm(form);
      showToast(editing ? "Service record updated." : "Service record created successfully.");
      navigate("/service-records");
    } catch (e) {
      const data = e.response?.data;
      const validationMessage = data?.errors ? Object.values(data.errors).join(" • ") : null;
      setError(validationMessage || data?.message || "Save failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-container narrow-page">
      <PageHeader
        eyebrow="Service records"
        title={editing ? `Edit record #${id}` : "Create Service Record"}
        subtitle={editing ? "Update the service details before work begins." : "Capture the service request clearly before assigning a technician."}
        actions={<button className="btn-soft" onClick={leaveForm}><Icon name="arrowLeft" size={17} /> Back</button>}
      />

      <section className="form-card fade-up delay-1">
        <div className="form-card-intro">
          <span className="feature-icon"><Icon name="clipboard" size={23} /></span>
          <div><h3>Service information</h3><p>Fields marked with * are required.</p></div>
          {isDirty && <span className="unsaved-chip"><span /> Unsaved changes</span>}
        </div>

        {error && <div className="notice notice-danger"><Icon name="alert" size={18} />{error}</div>}

        <form onSubmit={submit} className="form-stack" noValidate>
          {!editing && (
            <label className={`field-group ${showFieldError("bookingId") ? "field-invalid" : ""}`}>
              <span>Booking reference ID *</span>
              <input name="bookingId" type="number" min="1" step="1" value={form.bookingId} onChange={change} onBlur={blur} placeholder="e.g. 1001" aria-invalid={Boolean(showFieldError("bookingId"))} />
              {showFieldError("bookingId") ? <small className="field-error">{fieldErrors.bookingId}</small> : <small>Temporary reference until the booking module is fully integrated.</small>}
            </label>
          )}

          <label className={`field-group ${showFieldError("serviceDescription") ? "field-invalid" : ""}`}>
            <span>Service description *</span>
            <textarea name="serviceDescription" rows="4" maxLength="500" value={form.serviceDescription} onChange={change} onBlur={blur} placeholder="Describe the service requested..." aria-invalid={Boolean(showFieldError("serviceDescription"))} />
            <div className="field-meta-row">
              {showFieldError("serviceDescription") ? <small className="field-error">{fieldErrors.serviceDescription}</small> : <small>Clear, concise description of the requested work.</small>}
              <small className="char-count">{form.serviceDescription.length}/500</small>
            </div>
          </label>

          <div className="form-grid-2">
            <label className={`field-group ${showFieldError("mileage") ? "field-invalid" : ""}`}>
              <span>Current mileage *</span>
              <div className="input-with-icon"><Icon name="mileage" size={18} /><input name="mileage" type="number" min="0" step="1" value={form.mileage} onChange={change} onBlur={blur} placeholder="45200" aria-invalid={Boolean(showFieldError("mileage"))} /></div>
              {showFieldError("mileage") && <small className="field-error">{fieldErrors.mileage}</small>}
            </label>

            <label className={`field-group ${showFieldError("customerComplaint") ? "field-invalid" : ""}`}>
              <span>Customer complaint</span>
              <textarea name="customerComplaint" rows="3" maxLength="500" value={form.customerComplaint} onChange={change} onBlur={blur} placeholder="Any reported issue or concern" aria-invalid={Boolean(showFieldError("customerComplaint"))} />
              <div className="field-meta-row">
                {showFieldError("customerComplaint") ? <small className="field-error">{fieldErrors.customerComplaint}</small> : <span />}
                <small className="char-count">{form.customerComplaint.length}/500</small>
              </div>
            </label>
          </div>

          <label className={`field-group ${showFieldError("remarks") ? "field-invalid" : ""}`}>
            <span>Internal remarks</span>
            <textarea name="remarks" rows="3" maxLength="1000" value={form.remarks} onChange={change} onBlur={blur} placeholder="Optional notes for staff and technician" aria-invalid={Boolean(showFieldError("remarks"))} />
            <div className="field-meta-row">
              {showFieldError("remarks") ? <small className="field-error">{fieldErrors.remarks}</small> : <span />}
              <small className="char-count">{form.remarks.length}/1000</small>
            </div>
          </label>

          <div className="form-actions">
            <button type="button" className="btn-soft" onClick={leaveForm}>Cancel</button>
            <button className="btn-app" disabled={loading}>
              {loading ? <><span className="button-spinner" /> Saving...</> : editing ? "Update record" : "Create record"}
            </button>
          </div>
        </form>
      </section>
    </div>
  );
}
