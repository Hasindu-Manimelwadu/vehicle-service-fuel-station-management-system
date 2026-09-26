package com.vehiclestation.booking.integration;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class MockVehicleOwnershipValidator implements VehicleOwnershipValidator {

    @Override
    public boolean isVehicleOwnedByCustomer(Long vehicleId, Long customerId) {
        return vehicleId != null
                && vehicleId >= 1
                && vehicleId <= 3
                && customerId != null
                && customerId > 0;
    }
}
