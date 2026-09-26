import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import CommandPalette from "./components/CommandPalette";
import ProtectedRoute from "./components/ProtectedRoute";
import { ToastProvider } from "./components/ToastContext";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import DashboardPage from "./pages/DashboardPage";
import UnauthorizedPage from "./pages/UnauthorizedPage";
import ServiceRecordList from "./serviceRecords/ServiceRecordList";
import ServiceRecordForm from "./serviceRecords/ServiceRecordForm";
import ServiceRecordDetails from "./serviceRecords/ServiceRecordDetails";
import TechnicianJobs from "./serviceRecords/TechnicianJobs";
import TechnicianJobDetails from "./serviceRecords/TechnicianJobDetails";

function App() {
  return (
    <BrowserRouter>
      <ToastProvider>
        <Navbar />
        <CommandPalette />
        <main className="app-main">
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/unauthorized" element={<UnauthorizedPage />} />

            <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
            <Route path="/service-records" element={<ProtectedRoute roles={["STAFF", "ADMIN"]}><ServiceRecordList /></ProtectedRoute>} />
            <Route path="/service-records/new" element={<ProtectedRoute roles={["STAFF", "ADMIN"]}><ServiceRecordForm /></ProtectedRoute>} />
            <Route path="/service-records/:id/edit" element={<ProtectedRoute roles={["STAFF", "ADMIN"]}><ServiceRecordForm /></ProtectedRoute>} />
            <Route path="/service-records/:id" element={<ProtectedRoute roles={["STAFF", "ADMIN"]}><ServiceRecordDetails /></ProtectedRoute>} />
            <Route path="/technician/jobs" element={<ProtectedRoute roles={["TECHNICIAN"]}><TechnicianJobs /></ProtectedRoute>} />
            <Route path="/technician/jobs/:id" element={<ProtectedRoute roles={["TECHNICIAN"]}><TechnicianJobDetails /></ProtectedRoute>} />

            <Route path="/" element={<Navigate to={localStorage.getItem("accessToken") ? "/dashboard" : "/login"} replace />} />
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </main>
      </ToastProvider>
    </BrowserRouter>
  );
}

export default App;
