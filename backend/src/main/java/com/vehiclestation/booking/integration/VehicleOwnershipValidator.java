package com.vehiclestation.booking.integration;

public interface VehicleOwnershipValidator {

    /**
     * Validates whether the specified vehicle belongs to the authenticated customer.
     *
     * @param vehicleId the vehicle ID to validate
     * @param customerId the authenticated customer ID
     * @return true if vehicle is valid and belongs to the customer; false otherwise
     */
    boolean isVehicleOwnedByCustomer(Long vehicleId, Long customerId);
}
