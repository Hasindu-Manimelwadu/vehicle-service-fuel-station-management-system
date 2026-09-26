package com.vehiclestation.booking.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Production and default profile implementation of {@link VehicleOwnershipValidator}.
 * <p>
 * MODULE INTEGRATION CONTRACT:
 * ----------------------------
 * This module (UC-03 Service Booking & Scheduling) integrates with the shared
 * Customer & Vehicle Management module owned by another team member.
 * <p>
 * Expected Shared Schema Contract:
 * - Table name:                {@value #TABLE_VEHICLES}
 * - Vehicle Primary-Key Column: {@value #COLUMN_VEHICLE_ID} (BIGINT / Long)
 * - Customer Ownership Column:  {@value #COLUMN_CUSTOMER_ID} (BIGINT / Long, references users.user_id)
 * <p>
 * To maintain loose coupling across module boundaries prior to final integration,
 * we do not duplicate or redefine a JPA Vehicle entity here. Instead, ownership
 * verification is performed via direct centralized parameterized SQL query.
 */
@Service
@Profile("!dev")
public class DatabaseVehicleOwnershipValidator implements VehicleOwnershipValidator {

    private static final Logger log = LoggerFactory.getLogger(DatabaseVehicleOwnershipValidator.class);

    // =========================================================================
    // Centralized Shared Vehicle Table Contract
    // =========================================================================
    public static final String TABLE_VEHICLES = "vehicles";
    public static final String COLUMN_VEHICLE_ID = "id";
    public static final String COLUMN_CUSTOMER_ID = "customer_id";

    private static final String OWNERSHIP_COUNT_QUERY =
            "SELECT COUNT(*) FROM " + TABLE_VEHICLES +
            " WHERE " + COLUMN_VEHICLE_ID + " = ? AND " + COLUMN_CUSTOMER_ID + " = ?";

    private final JdbcTemplate jdbcTemplate;

    public DatabaseVehicleOwnershipValidator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean isVehicleOwnedByCustomer(Long vehicleId, Long customerId) {
        if (vehicleId == null || customerId == null) {
            return false;
        }

        try {
            Integer count = jdbcTemplate.queryForObject(
                    OWNERSHIP_COUNT_QUERY,
                    Integer.class,
                    vehicleId,
                    customerId
            );
            return count != null && count > 0;
        } catch (Exception e) {
            log.warn("Could not verify vehicle ownership for vehicleId={} and customerId={} using query [{}]: {}",
                    vehicleId, customerId, OWNERSHIP_COUNT_QUERY, e.getMessage());
            return false;
        }
    }
}
