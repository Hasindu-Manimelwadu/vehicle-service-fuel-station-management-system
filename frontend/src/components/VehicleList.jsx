import React, { useEffect, useState, useCallback } from "react";
import { getVehiclesByCustomer, deleteVehicle } from "../api/api";
import VehicleForm from "./VehicleForm";

export default function VehicleList({ customerId }) {
  const [vehicles, setVehicles] = useState([]);
  const [error, setError] = useState("");

  const loadVehicles = useCallback(async () => {
    try {
      const response = await getVehiclesByCustomer(customerId);
      setVehicles(response.data);
    } catch (err) {
      setError("Could not load vehicles.");
    }
  }, [customerId]);

  useEffect(() => {
    loadVehicles();
  }, [loadVehicles]);

  const handleDelete = async (id) => {
    await deleteVehicle(id);
    loadVehicles();
  };

  return (
    <div>
      <h2>My Vehicles</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}

      <ul>
        {vehicles.map((v) => (
          <li key={v.id}>
            {v.make} {v.model} ({v.year}) — {v.licensePlateNumber}
            <button onClick={() => handleDelete(v.id)}>Remove</button>
          </li>
        ))}
      </ul>

      <VehicleForm customerId={customerId} onVehicleAdded={loadVehicles} />
    </div>
  );
}
