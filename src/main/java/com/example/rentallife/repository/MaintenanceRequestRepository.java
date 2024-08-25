package com.example.rentallife.repository;

import com.example.rentallife.entity.MaintenanceRequest;
import com.example.rentallife.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByTenantId(Long tenantId);
    List<MaintenanceRequest> findByPropertyId(Long propertyId);
    List<MaintenanceRequest> findByProperty_Landlord(User landlord);
    List<MaintenanceRequest> findByTenant(User tenant);
}