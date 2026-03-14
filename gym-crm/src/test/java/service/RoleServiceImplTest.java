package service;

import com.alirizakaygusuz.gymcrm.dao.RoleRepository;
import com.alirizakaygusuz.gymcrm.exception.ResourceNotFoundException;
import com.alirizakaygusuz.gymcrm.model.Role;
import com.alirizakaygusuz.gymcrm.model.RoleType;
import com.alirizakaygusuz.gymcrm.model.User;
import com.alirizakaygusuz.gymcrm.model.UserRole;
import com.alirizakaygusuz.gymcrm.service.role.RoleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    @DisplayName("assignRoleToUser should add a UserRole entry when role exists")
    void assignRoleToUser_shouldAddUserRoleEntry_whenRoleExists() {
        User user = new User();
        user.setUsername("john.doe");

        RoleType roleType = RoleType.TRAINEE;

        Role role = new Role();
        role.setName(roleType);

        when(roleRepository.findByName(roleType)).thenReturn(Optional.of(role));

        LocalDateTime beforeCall = LocalDateTime.now();

        roleService.assignRoleToUser(user, roleType);

        LocalDateTime afterCall = LocalDateTime.now();

        assertNotNull(user.getUserRoles());
        assertEquals(1, user.getUserRoles().size());

        UserRole userRole = user.getUserRoles().iterator().next();

        assertSame(user, userRole.getUser());
        assertSame(role, userRole.getRole());

        assertNotNull(userRole.getAssignedAt());
        assertFalse(userRole.getAssignedAt().isBefore(beforeCall));
        assertFalse(userRole.getAssignedAt().isAfter(afterCall));

        verify(roleRepository).findByName(roleType);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    @DisplayName("assignRoleToUser should throw ResourceNotFoundException when role does not exist")
    void assignRoleToUser_shouldThrowResourceNotFoundException_whenRoleMissing() {
        User user = new User();
        user.setUsername("john.doe");

        RoleType roleType = RoleType.TRAINER;

        when(roleRepository.findByName(roleType)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> roleService.assignRoleToUser(user, roleType));

        assertNotNull(user.getUserRoles());
        assertTrue(user.getUserRoles().isEmpty());

        verify(roleRepository).findByName(roleType);
        verifyNoMoreInteractions(roleRepository);
    }
}