package com.example.Todo.user_role;

import com.example.Todo.exception.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.Todo.user_role.RoleService.DEFAULT_ROLE;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Nested
    class GetDefaultRole {
        @Test
        void givenExistedDefaultRoleWhenGetDefaultRoleThenReturnDefaultRole() {
            Role expectedRole = new Role(DEFAULT_ROLE);

            when(roleRepository.findByRoleType(DEFAULT_ROLE)).thenReturn(Optional.of(expectedRole));

            Role actualRole = roleService.getDefaultRole();

            assertThat(actualRole).isNotNull();
            assertThat(actualRole).isEqualTo(expectedRole);

            verify(roleRepository).findByRoleType(DEFAULT_ROLE);
        }

        @Test
        void givenNotExistedDefaultRoleWhenGetDefaultRoleThenReturnEntityNotFoundException() {
            when(roleRepository.findByRoleType(DEFAULT_ROLE)).thenReturn(Optional.empty());

            assertThatExceptionOfType(EntityNotFoundException.class)
                    .isThrownBy(
                            () -> roleService.getDefaultRole()
                    ).withMessage("Role not found");

            verify(roleRepository).findByRoleType(DEFAULT_ROLE);
        }
    }
}