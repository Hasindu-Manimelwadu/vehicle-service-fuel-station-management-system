package com.vehiclestation.booking.service;

import com.vehiclestation.booking.dto.BookingRequest;
import com.vehiclestation.booking.dto.BookingResponse;
import com.vehiclestation.booking.entity.ServiceBooking;
import com.vehiclestation.booking.enums.BookingStatus;
import com.vehiclestation.booking.exception.BookingNotFoundException;
import com.vehiclestation.booking.exception.InvalidVehicleOwnershipException;
import com.vehiclestation.booking.exception.SlotNotAvailableException;
import com.vehiclestation.booking.integration.VehicleOwnershipValidator;
import com.vehiclestation.booking.repository.ServiceBookingRepository;
import com.vehiclestation.schedule.entity.ServiceSchedule;
import com.vehiclestation.schedule.enums.ScheduleStatus;
import com.vehiclestation.schedule.repository.ServiceScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceBookingService {

    private final ServiceBookingRepository bookingRepository;
    private final ServiceScheduleRepository scheduleRepository;
    private final VehicleOwnershipValidator vehicleOwnershipValidator;

    public ServiceBookingService(
            ServiceBookingRepository bookingRepository,
            ServiceScheduleRepository scheduleRepository,
            VehicleOwnershipValidator vehicleOwnershipValidator
    ) {
        this.bookingRepository = bookingRepository;
        this.scheduleRepository = scheduleRepository;
        this.vehicleOwnershipValidator = vehicleOwnershipValidator;
    }

    /**
     * Creates and saves a new service booking directly as CONFIRMED.
     * Enforces vehicle ownership recheck and pessimistic locking on the schedule slot
     * to prevent race conditions or double-booking.
     *
     * @param customerId the authenticated customer ID resolved from JWT
     * @param request the booking details
     * @return the saved booking response
     */
    @Transactional
    public BookingResponse createBooking(Long customerId, BookingRequest request) {
        // 1. Recheck vehicle ownership via integration interface
        if (!vehicleOwnershipValidator.isVehicleOwnedByCustomer(request.getVehicleId(), customerId)) {
            throw new InvalidVehicleOwnershipException(
                    "Vehicle with ID " + request.getVehicleId() + " does not belong to the authenticated customer."
            );
        }

        // 2. Lock and recheck schedule availability
        ServiceSchedule schedule = scheduleRepository.findByIdWithLock(request.getScheduleId())
                .orElseThrow(() -> new SlotNotAvailableException(
                        "Schedule slot with ID " + request.getScheduleId() + " not found."
                ));

        if (schedule.getScheduleStatus() != ScheduleStatus.AVAILABLE) {
            throw new SlotNotAvailableException(
                    "The selected time slot on " + schedule.getScheduledDate() + " at " + schedule.getStartTime() + " is already booked."
            );
        }

        // 3. Mark schedule status as BOOKED
        schedule.setScheduleStatus(ScheduleStatus.BOOKED);
        scheduleRepository.save(schedule);

        // 4. Save the booking directly as CONFIRMED
        ServiceBooking booking = new ServiceBooking(
                customerId,
                request.getVehicleId(),
                schedule,
                request.getServiceType(),
                schedule.getScheduledDate(),
                schedule.getStartTime(),
                request.getDescription()
        );
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        ServiceBooking saved = bookingRepository.save(booking);

        return mapToResponse(saved);
    }

    /**
     * Retrieves all confirmed bookings belonging to the authenticated customer, newest first.
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(Long customerId) {
        return bookingRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Retrieves a single booking by ID ensuring it belongs to the authenticated customer.
     */
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId, Long customerId) {
        ServiceBooking booking = bookingRepository.findByBookingIdAndCustomerId(bookingId, customerId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking with ID " + bookingId + " was not found for this customer."
                ));

        return mapToResponse(booking);
    }

    /**
     * Updates an existing booking. When its slot changes, the old slot is released
     * and the new slot is locked and reserved in the same transaction.
     */
    @Transactional
    public BookingResponse updateBooking(Long bookingId, Long customerId, BookingRequest request) {
        ServiceBooking booking = findOwnedBooking(bookingId, customerId);

        if (!vehicleOwnershipValidator.isVehicleOwnedByCustomer(request.getVehicleId(), customerId)) {
            throw new InvalidVehicleOwnershipException(
                    "Vehicle with ID " + request.getVehicleId() + " does not belong to the authenticated customer."
            );
        }

        Long currentScheduleId = booking.getSchedule().getScheduleId();
        if (!currentScheduleId.equals(request.getScheduleId())) {
            ServiceSchedule newSchedule = scheduleRepository.findByIdWithLock(request.getScheduleId())
                    .orElseThrow(() -> new SlotNotAvailableException(
                            "Schedule slot with ID " + request.getScheduleId() + " not found."
                    ));

            if (newSchedule.getScheduleStatus() != ScheduleStatus.AVAILABLE) {
                throw new SlotNotAvailableException("The selected schedule slot is already booked.");
            }

            ServiceSchedule oldSchedule = booking.getSchedule();
            oldSchedule.setScheduleStatus(ScheduleStatus.AVAILABLE);
            scheduleRepository.save(oldSchedule);

            newSchedule.setScheduleStatus(ScheduleStatus.BOOKED);
            scheduleRepository.save(newSchedule);
            booking.setSchedule(newSchedule);
            booking.setBookingDate(newSchedule.getScheduledDate());
            booking.setBookingTime(newSchedule.getStartTime());
        }

        booking.setVehicleId(request.getVehicleId());
        booking.setServiceType(request.getServiceType());
        booking.setDescription(request.getDescription());
        return mapToResponse(bookingRepository.save(booking));
    }

    /** Deletes an owned booking and makes its schedule slot available again. */
    @Transactional
    public void deleteBooking(Long bookingId, Long customerId) {
        ServiceBooking booking = findOwnedBooking(bookingId, customerId);
        ServiceSchedule schedule = booking.getSchedule();
        bookingRepository.delete(booking);
        bookingRepository.flush();
        schedule.setScheduleStatus(ScheduleStatus.AVAILABLE);
        scheduleRepository.save(schedule);
    }

    private ServiceBooking findOwnedBooking(Long bookingId, Long customerId) {
        return bookingRepository.findByBookingIdAndCustomerId(bookingId, customerId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking with ID " + bookingId + " was not found for this customer."
                ));
    }

    private BookingResponse mapToResponse(ServiceBooking booking) {
        return new BookingResponse(
                booking.getBookingId(),
                booking.getCustomerId(),
                booking.getVehicleId(),
                booking.getSchedule() != null ? booking.getSchedule().getScheduleId() : null,
                booking.getServiceType(),
                booking.getBookingDate(),
                booking.getBookingTime(),
                booking.getDescription(),
                booking.getBookingStatus(),
                booking.getCreatedAt()
        );
    }
}
