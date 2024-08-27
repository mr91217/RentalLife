package com.example.rentallife.service;


import com.example.rentallife.dto.UserDTO;
import com.example.rentallife.entity.User;
import com.example.rentallife.entity.UserType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


public interface UserService extends UserDetailsService {
    public UserDetails loadUserByUsername(String userName);
    public void creat(UserDTO userDTO, UserType userType);
    public User findUserByEmail(String email);
    public User findUserByName(String name);
    List<User> getAllLandlords();
    List<User> getAllTenants();
    User findUserById(Long id);
    public User saveUser(User user);

}
