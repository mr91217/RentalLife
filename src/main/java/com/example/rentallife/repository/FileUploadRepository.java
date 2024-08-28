package com.example.rentallife.repository;

import com.example.rentallife.entity.FileUploadModel;
import com.example.rentallife.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUploadModel, Long> {
    //List<FileUploadModel> findByTenantId(Long tenantId);
    List<FileUploadModel> findByTenant(User tenant);
    List<FileUploadModel> findByLandlord(User landlord);
}