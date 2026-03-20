package com.alirizakaygusuz.gymcrm.service.role;

import com.alirizakaygusuz.gymcrm.dao.RoleRepository;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.model.Role;
import com.alirizakaygusuz.gymcrm.model.RoleType;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.model.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;


    @Override
    @Transactional
    public void assignRoleToUser(User user, RoleType roleType) {
       log.info("Assigning role {} to user {}", roleType, user.getUsername());

        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleType.name()));


        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setAssignedAt(LocalDateTime.now());

        user.getUserRoles().add(userRole);

        log.info("Role {} assigned to user {}", roleType, user.getUsername());
    }
}
