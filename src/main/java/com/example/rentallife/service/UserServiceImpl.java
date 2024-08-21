package com.example.rentallife.service;

import com.example.rentallife.dto.UserDTO;
import com.example.rentallife.entity.Property;
import com.example.rentallife.entity.Role;
import com.example.rentallife.entity.User;
import com.example.rentallife.entity.UserType;
import com.example.rentallife.repository.PropertyRepository;
import com.example.rentallife.repository.UserRepository;
import com.example.rentallife.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.midi.SysexMessage;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @implNote
 * UserPrincipal class which implements UserDetails interface.
 * This way you get more flexibility and control over user authorization and authentication process.
 */

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(userName);
        log.debug(userName);
        if (user == null) {
            log.warn("Invalid username or password {}", userName);
            throw new UsernameNotFoundException("Invalid username or password.");
        }
      /* return new org.springframework.security.core.userdetails.User(user.getUserName(),
user.getPassword(),   mapRolesToAuthorities(user.getRoles()));*/
        return new UserPrincipal(user, roleService.getRolesByUser(user.getId()));
    }
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new
                SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
    /** Using model mapper helps to avoid extra coding
     * @param userDTO
     */
    @Transactional
//    public void creat(UserDTO userDTO)
//    {
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        User user = modelMapper.map(userDTO, User.class);
//        user.setPassword(encoder.encode(user.getPassword()));
//        user.setRoles(Arrays.asList(roleService.findRoleByRoleName("ROLE_USER")));
//        userRepository.save(user);
//
//    }
    public void creat(UserDTO userDTO, UserType userType) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setUserType(userType);

        // 查找或创建角色
        Role userRole = roleService.findRoleByRoleName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role("ROLE_USER");
            roleService.saveRole(userRole);
        }

        // 设置用户角色并保存
        user.setRoles(Collections.singletonList(userRole));
        userRepository.save(user);

        log.info("User {} saved with roles: {}", user.getUserName(), user.getRoles());
    }

//    In this example login and email has the same values @param email @return

    public User findUserByEmail(String email)
    {
        return userRepository.findUserByEmail(email);
    }
    public User findUserByName(String name)
    {
        return userRepository.findUserByUserName(name);
    }

    @Transactional
    public void addPropertyToLandlord(User landlord, Property property) {
        property.setLandlord(landlord);
        propertyRepository.save(property);
    }
    @Override
    public List<User> getAllLandlords() {
        return userRepository.findByUserType(UserType.LANDLORD);
    }

}
