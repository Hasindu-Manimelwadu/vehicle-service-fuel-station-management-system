import { useEffect, useState } from "react";
import api from "./api/api";

function App() {
  const [message, setMessage] = useState("Connecting to backend...");
  const [error, setError] = useState("");

  useEffect(() => {
    api
      .get("/api/test")
      .then((response) => {
        setMessage(response.data);
      })
      .catch((requestError) => {
        console.error(requestError);
        setError("Unable to connect to the Spring Boot backend.");
      });
  }, []);

  return (
    <div className="container min-vh-100 d-flex align-items-center justify-content-center">
      <div className="card shadow p-4 text-center">
        <h1 className="mb-3">
          Vehicle Service and Fuel Station Management System
        </h1>

        {error ? (
          <div className="alert alert-danger">{error}</div>
        ) : (
          <div className="alert alert-success">{message}</div>
        )}

        <p className="text-muted mb-0">
          React frontend connected to Spring Boot backend.
        </p>
      </div>
    </div>
  );
}

export default App;