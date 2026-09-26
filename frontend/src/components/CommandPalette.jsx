import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import Icon from "./Icon";

export default function CommandPalette() {
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState("");
  const [activeIndex, setActiveIndex] = useState(0);
  const inputRef = useRef(null);

  const token = localStorage.getItem("accessToken");
  const role = localStorage.getItem("role");

  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const commands = useMemo(() => {
    const items = [];
    if (token) {
      items.push({ icon: "dashboard", label: "Go to Dashboard", hint: "Overview and workshop pulse", keywords: "home overview stats", action: () => navigate("/dashboard") });
      if (role === "STAFF" || role === "ADMIN") {
        items.push({ icon: "clipboard", label: "Open Service Records", hint: "Search and manage records", keywords: "records table board", action: () => navigate("/service-records") });
        items.push({ icon: "plus", label: "Create Service Record", hint: "Start a new workshop record", keywords: "new create service", action: () => navigate("/service-records/new") });
      }
      if (role === "TECHNICIAN") {
        items.push({ icon: "wrench", label: "Open My Jobs", hint: "Assigned technician work", keywords: "jobs technician assigned", action: () => navigate("/technician/jobs") });
      }
      items.push({ icon: "pulse", label: "Operations View", hint: "Dark workshop interface is always active", keywords: "dark operations interface mode", action: () => navigate("/dashboard") });
      items.push({ icon: "logout", label: "Sign Out", hint: "End this session securely", keywords: "logout exit sign out", action: logout, danger: true });
    } else {
      items.push({ icon: "user", label: "Sign In", hint: "Open the login screen", keywords: "login account", action: () => navigate("/login") });
      items.push({ icon: "plus", label: "Create Account", hint: "Register a customer account", keywords: "register signup", action: () => navigate("/register") });
    }
    return items;
  }, [token, role, navigate]);

  const filtered = useMemo(() => {
    const value = query.trim().toLowerCase();
    if (!value) return commands;
    return commands.filter((item) => `${item.label} ${item.hint} ${item.keywords}`.toLowerCase().includes(value));
  }, [commands, query]);

  useEffect(() => {
    const onKeyDown = (event) => {
      if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === "k") {
        event.preventDefault();
        setOpen((current) => !current);
      }
      if (!open) return;
      if (event.key === "Escape") setOpen(false);
      if (event.key === "ArrowDown") {
        event.preventDefault();
        setActiveIndex((current) => Math.min(current + 1, Math.max(filtered.length - 1, 0)));
      }
      if (event.key === "ArrowUp") {
        event.preventDefault();
        setActiveIndex((current) => Math.max(current - 1, 0));
      }
      if (event.key === "Enter" && filtered[activeIndex]) {
        event.preventDefault();
        filtered[activeIndex].action();
        setOpen(false);
      }
    };
    const onOpen = () => setOpen(true);
    window.addEventListener("keydown", onKeyDown);
    window.addEventListener("autocare:command-palette", onOpen);
    return () => {
      window.removeEventListener("keydown", onKeyDown);
      window.removeEventListener("autocare:command-palette", onOpen);
    };
  }, [open, filtered, activeIndex]);

  useEffect(() => {
    if (open) {
      setQuery("");
      setActiveIndex(0);
      window.setTimeout(() => inputRef.current?.focus(), 20);
    }
  }, [open]);

  useEffect(() => setActiveIndex(0), [query]);

  if (!open) return null;

  return (
    <div className="command-backdrop" onMouseDown={() => setOpen(false)}>
      <section className="command-palette" onMouseDown={(event) => event.stopPropagation()} role="dialog" aria-modal="true" aria-label="Quick command menu">
        <div className="command-search">
          <Icon name="search" size={19} />
          <input ref={inputRef} value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search actions and pages..." />
          <kbd>ESC</kbd>
        </div>
        <div className="command-section-label">Quick actions</div>
        <div className="command-results">
          {filtered.map((item, index) => (
            <button
              key={item.label}
              className={`command-item ${index === activeIndex ? "active" : ""} ${item.danger ? "danger" : ""}`}
              onMouseEnter={() => setActiveIndex(index)}
              onClick={() => { item.action(); setOpen(false); }}
            >
              <span className="command-icon"><Icon name={item.icon} size={18} /></span>
              <span className="command-copy"><strong>{item.label}</strong><small>{item.hint}</small></span>
              <Icon name="chevronRight" size={16} />
            </button>
          ))}
          {filtered.length === 0 && (
            <div className="command-empty"><Icon name="search" size={24} /><strong>No matching action</strong><span>Try a different command or page name.</span></div>
          )}
        </div>
        <footer className="command-footer"><span><kbd>↑</kbd><kbd>↓</kbd> Navigate</span><span><kbd>↵</kbd> Open</span><span><kbd>ESC</kbd> Close</span></footer>
      </section>
    </div>
  );
}
