package com.example.rentallife.repository;

import com.example.rentallife.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByTenantId(Long tenantId);
    List<Payment> findByPropertyId(Long propertyId);
    List<Payment> findByPropertyLandlordId(Long landlordId);
}