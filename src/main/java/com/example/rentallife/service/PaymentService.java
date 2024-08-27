package com.example.rentallife.service;

import com.example.rentallife.entity.Payment;
import com.example.rentallife.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public Payment savePayment(Payment payment) {
        payment.setPaymentDate(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    // 获取租客的支付历史
    public List<Payment> getPaymentsByTenant(Long tenantId) {
        return paymentRepository.findByTenantId(tenantId);
    }

    public List<Payment> getPaymentsByProperty(Long propertyId) {
        return paymentRepository.findByPropertyId(propertyId);
    }
    // 获取房东的支付历史（所有租客的支付记录）
    public List<Payment> getPaymentsByLandlord(Long landlordId) {
        return paymentRepository.findByPropertyLandlordId(landlordId);
    }

}