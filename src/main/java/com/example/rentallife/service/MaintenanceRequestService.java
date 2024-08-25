package com.example.rentallife.service;

import com.example.rentallife.entity.MaintenanceRequest;
import com.example.rentallife.entity.RequestStatus;
import com.example.rentallife.entity.User;
import com.example.rentallife.repository.MaintenanceRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceRequestService {

    @Autowired
    private MaintenanceRequestRepository maintenanceRequestRepository;

    public void submitRequest(MaintenanceRequest request) {
        request.setRequestDate(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);
        maintenanceRequestRepository.save(request);
    }

    public List<MaintenanceRequest> getRequestsForTenant(Long tenantId) {
        return maintenanceRequestRepository.findByTenantId(tenantId);
    }

    public List<MaintenanceRequest> getRequestsForProperty(Long propertyId) {
        return maintenanceRequestRepository.findByPropertyId(propertyId);
    }
    public List<MaintenanceRequest> getRequestsForLandlord(User landlord) {
        return maintenanceRequestRepository.findByProperty_Landlord(landlord);
    }
    public MaintenanceRequest findById(Long requestId) {
        return maintenanceRequestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException("Request not found"));
    }

    public void save(MaintenanceRequest request) {
        maintenanceRequestRepository.save(request);
    }
    public List<MaintenanceRequest> getRequestsForTenant(User tenant) {
        return maintenanceRequestRepository.findByTenant(tenant);
    }
}