package com.example.rentallife.service;

import com.example.rentallife.dto.PropertyDTO;
import com.example.rentallife.entity.Property;
import com.example.rentallife.entity.User;
import com.example.rentallife.repository.PropertyRepository;
import com.example.rentallife.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void addProperty(PropertyDTO propertyDTO) {
        // 查找房东用户
        User landlord = userRepository.findById(propertyDTO.getLandlordId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid landlord ID"));

        // 创建并设置Property实体
        Property property = new Property();
        property.setAddress(propertyDTO.getAddress());
        property.setCity(propertyDTO.getCity());
        property.setState(propertyDTO.getState());
        property.setZip(propertyDTO.getZip());
        property.setRooms(propertyDTO.getRooms());
        property.setPrice(propertyDTO.getPrice());
        property.setTerm(propertyDTO.getTerm());
        property.setLandlord(landlord);

        // 保存物业信息
        propertyRepository.save(property);
    }
    public User findUserByName(String userName) {
        return userRepository.findByUserName(userName);
    }
    public List<Property> getPropertiesByLandlord(User landlord) {
        return propertyRepository.findByLandlord(landlord);
    }
    @Transactional
    public void addTenantToProperty(Long propertyId, Long tenantId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid property ID"));

        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tenant ID"));

        property.getTenants().add(tenant);
        propertyRepository.save(property);
    }
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }
    public Property findPropertyById(Long id) {
        Optional<Property> propertyOptional = propertyRepository.findById(id);
        if (propertyOptional.isPresent()) {
            return propertyOptional.get();
        } else {
            throw new IllegalArgumentException("Property not found with id: " + id);
        }
    }
    @Transactional
    public void deletePropertyById(Long propertyId) {
        propertyRepository.deleteById(propertyId);
    }
    public void saveProperty(Property property) {
        propertyRepository.save(property);
    }
    public void removeTenantsFromProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new EntityNotFoundException("Property not found"));
        property.setTenants(new ArrayList<>()); // 移除所有租户
        propertyRepository.save(property);
    }

    public List<Property> getPropertiesByTenant(User tenant) {
        return propertyRepository.findByTenantsContains(tenant);
    }
}
