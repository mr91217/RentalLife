package com.example.rentallife.repository;

import com.example.rentallife.entity.Property;
import com.example.rentallife.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    // 根据房东查询物业的方法
    List<Property> findByLandlord(User landlord);
}