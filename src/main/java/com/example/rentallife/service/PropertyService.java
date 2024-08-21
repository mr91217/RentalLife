package com.example.rentallife.service;

import com.example.rentallife.dto.PropertyDTO;
import com.example.rentallife.entity.Property;
import com.example.rentallife.entity.User;
import com.example.rentallife.repository.PropertyRepository;
import com.example.rentallife.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        property.setLandlord(landlord);

        // 保存物业信息
        propertyRepository.save(property);
    }
}
