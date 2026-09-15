import React, { useState } from "react";
import CustomerRegister from "./components/CustomerRegister";
import CustomerProfile from "./components/CustomerProfile";
import VehicleList from "./components/VehicleList";

// Simple state-based view switch (no router yet — this module can be wired
// into the shared app's routing once Authentication is integrated).
export default function App() {
  const [customer, setCustomer] = useState(null);

  if (!customer) {
    return (
      <div style={{ maxWidth: 480, margin: "40px auto" }}>
        <CustomerRegister onRegistered={setCustomer} />
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 480, margin: "40px auto" }}>
      <CustomerProfile customer={customer} onUpdated={setCustomer} />
      <hr />
      <VehicleList customerId={customer.id} />
    </div>
  );
}
