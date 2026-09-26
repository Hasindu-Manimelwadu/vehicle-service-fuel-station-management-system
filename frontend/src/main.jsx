import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "bootstrap/dist/css/bootstrap.min.css";
import "./index.css";
import App from "./App.jsx";

// AutoCare uses one fixed professional dark operations theme.
document.documentElement.setAttribute("data-theme", "dark");
document.documentElement.style.colorScheme = "dark";
localStorage.removeItem("autocare-theme");

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <App />
  </StrictMode>
);
