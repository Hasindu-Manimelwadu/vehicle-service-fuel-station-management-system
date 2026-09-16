package com.vehiclestation.servicerecord.service;

import com.vehiclestation.auth.entity.User;
import com.vehiclestation.auth.enums.AccountStatus;
import com.vehiclestation.auth.enums.Role;
import com.vehiclestation.auth.repository.UserRepository;
import com.vehiclestation.servicerecord.dto.*;
import com.vehiclestation.servicerecord.entity.ServicePartUsage;
import com.vehiclestation.servicerecord.entity.ServiceRecord;
import com.vehiclestation.servicerecord.entity.TechnicianAssignment;
import com.vehiclestation.servicerecord.enums.AssignmentStatus;
import com.vehiclestation.servicerecord.enums.ServiceStatus;
import com.vehiclestation.servicerecord.exception.InvalidServiceStateException;
import com.vehiclestation.servicerecord.exception.ServiceAccessDeniedException;
import com.vehiclestation.servicerecord.exception.ServiceRecordNotFoundException;
import com.vehiclestation.servicerecord.repository.ServicePartUsageRepository;
import com.vehiclestation.servicerecord.repository.ServiceRecordRepository;
import com.vehiclestation.servicerecord.repository.TechnicianAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;
    private final TechnicianAssignmentRepository assignmentRepository;
    private final ServicePartUsageRepository partUsageRepository;
    private final UserRepository userRepository;

    public ServiceRecordService(
            ServiceRecordRepository serviceRecordRepository,
            TechnicianAssignmentRepository assignmentRepository,
            ServicePartUsageRepository partUsageRepository,
            UserRepository userRepository
    ) {
        this.serviceRecordRepository = serviceRecordRepository;
        this.assignmentRepository = assignmentRepository;
        this.partUsageRepository = partUsageRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ServiceRecordResponse create(CreateServiceRecordRequest request) {
        ServiceRecord record = new ServiceRecord();
        record.setBookingId(request.getBookingId());
        record.setServiceDescription(request.getServiceDescription().trim());
        record.setCustomerComplaint(trimToNull(request.getCustomerComplaint()));
        record.setMileage(request.getMileage());
        record.setRemarks(trimToNull(request.getRemarks()));
        record.setServiceStatus(ServiceStatus.PENDING);

        return toResponse(serviceRecordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<ServiceRecordResponse> getAll() {
        return serviceRecordRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TechnicianOptionResponse> getAvailableTechnicians() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.TECHNICIAN)
                .filter(user -> user.getAccountStatus() == AccountStatus.ACTIVE)
                .map(user -> new TechnicianOptionResponse(
                        user.getUserId(),
                        user.getFullName(),
                        user.getEmail()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceRecordResponse getById(Long id) {
        return toResponse(findRecord(id));
    }

    @Transactional
    public ServiceRecordResponse update(Long id, UpdateServiceRecordRequest request) {
        ServiceRecord record = findRecord(id);

        requireStatus(record, ServiceStatus.PENDING,
                "Only pending service records can be edited by staff");

        record.setServiceDescription(request.getServiceDescription().trim());
        record.setCustomerComplaint(trimToNull(request.getCustomerComplaint()));
        record.setMileage(request.getMileage());
        record.setRemarks(trimToNull(request.getRemarks()));

        return toResponse(serviceRecordRepository.save(record));
    }

    @Transactional
    public void delete(Long id) {
        ServiceRecord record = findRecord(id);

        requireStatus(record, ServiceStatus.PENDING,
                "Only pending service records can be deleted");

        if (assignmentRepository.findByServiceRecord_ServiceRecordId(id).isPresent()) {
            throw new InvalidServiceStateException(
                    "Remove or change the technician assignment before deleting this record"
            );
        }

        partUsageRepository.deleteAll(
                partUsageRepository.findByServiceRecord_ServiceRecordIdOrderByRecordedAtAsc(id)
        );
        serviceRecordRepository.delete(record);
    }

    @Transactional
    public ServiceRecordResponse assignTechnician(Long id, AssignTechnicianRequest request) {
        ServiceRecord record = findRecord(id);

        requireStatus(record, ServiceStatus.PENDING,
                "Technician can only be assigned before the service starts");

        User technician = userRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new ServiceRecordNotFoundException("Technician user not found"));

        if (technician.getRole() != Role.TECHNICIAN) {
            throw new InvalidServiceStateException("Selected user is not a technician");
        }

        if (technician.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new InvalidServiceStateException("Selected technician account is inactive");
        }

        TechnicianAssignment assignment = assignmentRepository
                .findByServiceRecord_ServiceRecordId(id)
                .orElseGet(TechnicianAssignment::new);

        assignment.setServiceRecord(record);
        assignment.setTechnician(technician);
        assignment.setTaskDescription(request.getTaskDescription().trim());
        assignment.setAssignedDate(LocalDateTime.now());
        assignment.setAssignmentStatus(AssignmentStatus.ASSIGNED);
        assignment.setCompletedDate(null);

        assignmentRepository.save(assignment);
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    public List<ServiceRecordResponse> getMyJobs(Long technicianId) {
        return assignmentRepository
                .findByTechnician_UserIdOrderByAssignedDateDesc(technicianId)
                .stream()
                .map(TechnicianAssignment::getServiceRecord)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceRecordResponse getMyJob(Long technicianId, Long serviceRecordId) {
        ServiceRecord record = findRecord(serviceRecordId);
        requireAssignedTechnician(serviceRecordId, technicianId);
        return toResponse(record);
    }

    @Transactional
    public ServiceRecordResponse startService(Long serviceRecordId, Long technicianId) {
        ServiceRecord record = findRecord(serviceRecordId);
        TechnicianAssignment assignment = requireAssignedTechnician(serviceRecordId, technicianId);

        requireStatus(record, ServiceStatus.PENDING,
                "Only a pending service can be started");

        record.setServiceStatus(ServiceStatus.IN_PROGRESS);
        record.setStartDate(LocalDateTime.now());
        assignment.setAssignmentStatus(AssignmentStatus.IN_PROGRESS);

        assignmentRepository.save(assignment);
        serviceRecordRepository.save(record);
        return toResponse(record);
    }

    @Transactional
    public ServiceRecordResponse updateProgress(
            Long serviceRecordId,
            Long technicianId,
            UpdateServiceProgressRequest request
    ) {
        ServiceRecord record = findRecord(serviceRecordId);
        requireAssignedTechnician(serviceRecordId, technicianId);

        requireStatus(record, ServiceStatus.IN_PROGRESS,
                "Progress can only be updated while the service is in progress");

        record.setWorkPerformed(request.getWorkPerformed().trim());
        record.setRemarks(trimToNull(request.getRemarks()));

        serviceRecordRepository.save(record);
        return toResponse(record);
    }

    @Transactional
    public ServiceRecordResponse addPart(
            Long serviceRecordId,
            Long technicianId,
            AddPartUsageRequest request
    ) {
        ServiceRecord record = findRecord(serviceRecordId);
        requireAssignedTechnician(serviceRecordId, technicianId);

        requireStatus(record, ServiceStatus.IN_PROGRESS,
                "Parts can only be recorded while the service is in progress");

        ServicePartUsage usage = new ServicePartUsage();
        usage.setServiceRecord(record);
        usage.setPartId(request.getPartId());
        usage.setPartName(request.getPartName().trim());
        usage.setQuantityUsed(request.getQuantityUsed());
        partUsageRepository.save(usage);

        return toResponse(record);
    }

    @Transactional
    public ServiceRecordResponse removePart(
            Long serviceRecordId,
            Long technicianId,
            Long usageId
    ) {
        ServiceRecord record = findRecord(serviceRecordId);
        requireAssignedTechnician(serviceRecordId, technicianId);

        requireStatus(record, ServiceStatus.IN_PROGRESS,
                "Parts can only be changed while the service is in progress");

        ServicePartUsage usage = partUsageRepository
                .findByUsageIdAndServiceRecord_ServiceRecordId(usageId, serviceRecordId)
                .orElseThrow(() -> new ServiceRecordNotFoundException("Part usage record not found"));

        partUsageRepository.delete(usage);
        return toResponse(record);
    }

    @Transactional
    public ServiceRecordResponse completeService(Long serviceRecordId, Long technicianId) {
        ServiceRecord record = findRecord(serviceRecordId);
        TechnicianAssignment assignment = requireAssignedTechnician(serviceRecordId, technicianId);

        requireStatus(record, ServiceStatus.IN_PROGRESS,
                "Only an in-progress service can be completed");

        if (record.getWorkPerformed() == null || record.getWorkPerformed().isBlank()) {
            throw new InvalidServiceStateException(
                    "Work performed details must be recorded before completing the service"
            );
        }

        LocalDateTime now = LocalDateTime.now();
        record.setServiceStatus(ServiceStatus.COMPLETED);
        record.setCompletionDate(now);
        assignment.setAssignmentStatus(AssignmentStatus.COMPLETED);
        assignment.setCompletedDate(now);

        assignmentRepository.save(assignment);
        serviceRecordRepository.save(record);
        return toResponse(record);
    }

    private ServiceRecord findRecord(Long id) {
        return serviceRecordRepository.findById(id)
                .orElseThrow(() -> new ServiceRecordNotFoundException(
                        "Service record not found: " + id
                ));
    }

    private TechnicianAssignment requireAssignedTechnician(Long serviceRecordId, Long technicianId) {
        TechnicianAssignment assignment = assignmentRepository
                .findByServiceRecord_ServiceRecordId(serviceRecordId)
                .orElseThrow(() -> new InvalidServiceStateException(
                        "No technician is assigned to this service record"
                ));

        if (!assignment.getTechnician().getUserId().equals(technicianId)) {
            throw new ServiceAccessDeniedException(
                    "This service job is assigned to another technician"
            );
        }

        return assignment;
    }

    private void requireStatus(ServiceRecord record, ServiceStatus required, String message) {
        if (record.getServiceStatus() != required) {
            throw new InvalidServiceStateException(message);
        }
    }

    private ServiceRecordResponse toResponse(ServiceRecord record) {
        TechnicianAssignment assignment = assignmentRepository
                .findByServiceRecord_ServiceRecordId(record.getServiceRecordId())
                .orElse(null);

        List<PartUsageResponse> parts = partUsageRepository
                .findByServiceRecord_ServiceRecordIdOrderByRecordedAtAsc(record.getServiceRecordId())
                .stream()
                .map(usage -> new PartUsageResponse(
                        usage.getUsageId(),
                        usage.getPartId(),
                        usage.getPartName(),
                        usage.getQuantityUsed(),
                        usage.getRecordedAt()
                ))
                .toList();

        return new ServiceRecordResponse(
                record.getServiceRecordId(),
                record.getBookingId(),
                record.getServiceDate(),
                record.getServiceDescription(),
                record.getCustomerComplaint(),
                record.getMileage(),
                record.getWorkPerformed(),
                record.getRemarks(),
                record.getServiceStatus(),
                record.getStartDate(),
                record.getCompletionDate(),
                assignment == null ? null : assignment.getTechnician().getUserId(),
                assignment == null ? null : assignment.getTechnician().getFullName(),
                assignment == null ? null : assignment.getAssignmentStatus(),
                assignment == null ? null : assignment.getTaskDescription(),
                parts,
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
