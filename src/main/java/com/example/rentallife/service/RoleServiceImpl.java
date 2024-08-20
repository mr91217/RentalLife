package com.example.rentallife.service;

import com.example.rentallife.entity.Role;
import com.example.rentallife.repository.RoleRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;
    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Override
    @Transactional
    public void saveRole(Role role) {
        roleRepository.save(role);
    }
    @Override
    @Transactional
    public Role findRoleByRoleName(String name) {
        return roleRepository.findRoleByName(name);
    }
    @Override
    public List<Role> getAllRoles() {
        return (List<Role>) roleRepository.findAll();
    }
    @Override
    public List<Role> getRolesByUser(long id) {

        //return roleRepository.findRoleByUser(id);
        List<Role> roles = roleRepository.findRoleByUser(id); // 获取用户角色
        log.info("Roles for user {}: {}", id, roles); // 正确地记录日志
        return roles;

    }
}
