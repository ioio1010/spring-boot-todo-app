package com.example.Todo.user;

import com.example.Todo.exception.BadRequestException;
import com.example.Todo.storage.StorageService;
import com.example.Todo.user.dto.SignUpRequest;
import com.example.Todo.user.dto.UserFilterRequest;
import com.example.Todo.user_role.Role;
import com.example.Todo.user_role.RoleService;
import com.example.Todo.user_role.RoleType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Validator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private StorageService fileStorageService;

    @Mock
    private RoleService roleService;

    @Mock
    private Validator validator;

    @InjectMocks
    private UserService userService;

    @Nested
    class CreateUserWithSignUpForm {
        @Test
        void givenValidSignUpFormWhenCreateUserThenReturnUser() {
            SignUpRequest givenSignUpRequest = new SignUpRequest(
                    "test@example.com",
                    "Password@1",
                    "Password@1"
            );

            when(validator.validate(givenSignUpRequest)).thenReturn(Set.of());
            when(roleService.getDefaultRole()).thenReturn(new Role(RoleType.USER));
            when(passwordEncoder.encode(any(String.class))).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenReturn(any(User.class));

            User actualUser = userService.createUser(givenSignUpRequest);

            assertThat(actualUser).isNotNull();
            assertThat(actualUser.getEmail()).isEqualTo(givenSignUpRequest.getEmail());

            verify(validator).validate(givenSignUpRequest);
            verify(roleService).getDefaultRole();
            verify(passwordEncoder).encode(any(String.class));
            verify(userRepository).save(any(User.class));
        }

        @Test
        void givenValidSignUpFormWhenCreateUserThenReturnUserWithOnlyDefaultRole() {
            SignUpRequest givenSignUpRequest = new SignUpRequest(
                    "test@example.com",
                    "Password@1",
                    "Password@1"
            );
            Role expectedRole = new Role(RoleType.USER);

            when(validator.validate(givenSignUpRequest)).thenReturn(Set.of());
            when(roleService.getDefaultRole()).thenReturn(expectedRole);
            when(passwordEncoder.encode(any(String.class))).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenReturn(any(User.class));

            User actualUser = userService.createUser(givenSignUpRequest);

            assertThat(actualUser).isNotNull();
            assertThat(actualUser.getEmail()).isEqualTo(givenSignUpRequest.getEmail());
            assertThat(actualUser.getRoles()).isEqualTo(List.of(expectedRole));

            verify(validator).validate(givenSignUpRequest);
            verify(roleService).getDefaultRole();
            verify(passwordEncoder).encode(any(String.class));
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    class CreateUser {
        @Test
        void givenValidInputWhenCreateUserThenReturnUser() {
            String expectedEmail = "test@example.com";
            String expectedPassword = "encoded";
            List<Role> expectedRoles = List.of(new Role(RoleType.USER));

            when(passwordEncoder.encode(any(String.class))).thenReturn(expectedPassword);
            when(userRepository.save(any(User.class))).thenReturn(any(User.class));

            User actualUser = userService.createUser(expectedEmail, "Password@1", expectedRoles);

            assertThat(actualUser).isNotNull();
            assertThat(actualUser.getEmail()).isEqualTo(expectedEmail);
            assertThat(actualUser.getPassword()).isEqualTo(expectedPassword);
            assertThat(actualUser.getRoles()).isEqualTo(expectedRoles);

            verify(userRepository).save(any(User.class));
            verify(passwordEncoder).encode(any(String.class));
        }

        @Test
        void givenEmptyRolesListWhenCreateUserThenReturnException() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(
                            () -> userService.createUser(
                                    "test@example.com",
                                    "Password@1",
                                    List.of())
                    ).withMessage("Roles is empty");
        }
    }

    @Nested
    class GetUserWithEmailAndPassword {
        @Test
        void givenValidInputWhenGetUserThenReturnUser() {
            String givenPassword = "encoded";
            List<Role> givenRoles = List.of(new Role(RoleType.USER));

            String expectedEmail = "test@example.com";
            User expectedUser = new User(expectedEmail, givenPassword, givenRoles);

            when(userRepository.findByEmail(expectedEmail)).thenReturn(Optional.of(expectedUser));
            when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);

            Optional<User> actualUser = userService.getUserBy(expectedEmail, givenPassword);

            assertThat(actualUser.isEmpty()).isFalse();
            assertThat(actualUser.get().getEmail()).isEqualTo(expectedEmail);

            verify(userRepository).findByEmail(expectedEmail);
            verify(passwordEncoder).matches(any(String.class), any(String.class));
        }

        @Test
        void givenInvalidEmailWhenGetUserThenReturnOptionalEmpty() {
            String givenEmail = "test@example.com";
            String givenPassword = "encoded";

            when(userRepository.findByEmail(givenEmail)).thenReturn(Optional.empty());

            Optional<User> actualUser = userService.getUserBy(givenEmail, givenPassword);

            assertThat(actualUser.isEmpty()).isTrue();

            verify(userRepository).findByEmail(givenEmail);
        }

        @Test
        void givenInvalidPasswordWhenGetUserThenReturnOptionalEmpty() {
            String givenEmail = "test@example.com";
            String givenPassword = "encoded";
            List<Role> givenRoles = List.of(new Role(RoleType.USER));
            User givenUser = new User(givenEmail, givenPassword, givenRoles);

            when(userRepository.findByEmail(givenEmail)).thenReturn(Optional.of(givenUser));
            when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);

            Optional<User> actualUser = userService.getUserBy(givenEmail, givenPassword);

            assertThat(actualUser.isEmpty()).isTrue();

            verify(userRepository).findByEmail(givenEmail);
        }
    }

    @Nested
    class GetUsers {
        @Test
        void givenEmailFilterWhenGetUsersThenReturnUsersContainsEmail() {
            List<User> givenList = List.of(new User(
                    1L,
                    "test1@example.com",
                    "password",
                    List.of(new Role(RoleType.USER)),
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            ));
            Pageable givenPageable = PageRequest.of(0, 10);
            UserFilterRequest givenFilterRequest = new UserFilterRequest("test1", null);

            when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(givenList, givenPageable, givenList.size()));

            Page<User> actualUsers = userService.getUsers(givenFilterRequest, givenPageable);

            assertThat(actualUsers.getTotalElements()).isEqualTo(givenList.size());
            assertThat(actualUsers.getContent().stream().filter(
                    (user) -> !user.getEmail().contains(givenFilterRequest.email()))
            ).isEmpty();

            verify(userRepository).findAll(ArgumentMatchers.<Specification<User>>any(), any(Pageable.class));
        }

        @Test
        void givenCreatedAtFilterWhenGetUsersThenReturnUsersMatchCreatedAt() {
            LocalDateTime expectedCreatedAt = LocalDateTime.now();
            List<User> expectedList = List.of(new User(
                    1L,
                    "test1@example.com",
                    "password",
                    List.of(new Role(RoleType.USER)),
                    null,
                    expectedCreatedAt,
                    expectedCreatedAt
            ));

            Pageable givenPageable = PageRequest.of(0, 10);
            UserFilterRequest givenFilterRequest = new UserFilterRequest(null, expectedCreatedAt.toLocalDate());

            when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(expectedList, givenPageable, expectedList.size()));

            Page<User> actualUsers = userService.getUsers(givenFilterRequest, givenPageable);

            assertThat(actualUsers.getTotalElements()).isEqualTo(expectedList.size());
            assertThat(actualUsers.getContent().stream().filter(
                    (actualUser) -> !actualUser.getCreatedAt().toLocalDate().equals(expectedCreatedAt.toLocalDate()))
            ).isEmpty();

            verify(userRepository).findAll(ArgumentMatchers.<Specification<User>>any(), any(Pageable.class));
        }

        @Test
        void givenEmailAndCreatedAtFilterWhenGetUsersThenReturnUsersContainsEmailAndMatchCreatedAt() {
            LocalDateTime expectedCreatedAt = LocalDateTime.now();
            List<User> expectedList = List.of(new User(
                    1L,
                    "test1@example.com",
                    "password",
                    List.of(new Role(RoleType.USER)),
                    null,
                    expectedCreatedAt,
                    expectedCreatedAt
            ));

            Pageable givenPageable = PageRequest.of(0, 10);
            UserFilterRequest givenFilterRequest = new UserFilterRequest("test1", expectedCreatedAt.toLocalDate());

            when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(expectedList, givenPageable, expectedList.size()));

            Page<User> actualUsers = userService.getUsers(givenFilterRequest, givenPageable);

            assertThat(actualUsers.getTotalElements()).isEqualTo(1);
            assertThat(actualUsers.getContent().stream().filter(
                    (actualUser) -> !actualUser.getEmail().contains(givenFilterRequest.email()) &&
                    !actualUser.getCreatedAt().toLocalDate().equals(expectedCreatedAt.toLocalDate()))
            ).isEmpty();

            verify(userRepository).findAll(ArgumentMatchers.<Specification<User>>any(), any(Pageable.class));
        }
    }

    @Nested
    class UpdateUser {
        @Test
        void givenAvatarWhenUpdateUserThenReturnUserWithNewAvatar() throws IOException {
            User givenUser = new User(
                    "test@example.com",
                    "password",
                    List.of(new Role(RoleType.USER))
            );
            MockMultipartFile givenFile = new MockMultipartFile(
                    "file",
                    "file.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "Image bytes".getBytes()
            );

            String expectedFileName = "33b491ad-0e90-4623-85e1-dbf332d5d96d.jpg";

            when(fileStorageService.save(givenFile)).thenReturn(expectedFileName);

            User actualUser = userService.updateUser(givenUser, givenFile);

            assertThat(actualUser.getAvatar()).isEqualTo(expectedFileName);

            verify(fileStorageService).save(givenFile);
        }

        @Test
        void givenAvatarWithInvalidFormatWhenUpdateUserThenReturnBadRequestException() {
            User givenUser = new User(
                    "test@example.com",
                    "password",
                    List.of(new Role(RoleType.USER))
            );
            MockMultipartFile givenFile = new MockMultipartFile(
                    "file",
                    "file.txt",
                    MediaType.TEXT_PLAIN_VALUE,
                    "Image bytes".getBytes()
            );

            assertThatExceptionOfType(BadRequestException.class)
                    .isThrownBy(
                            () -> userService.updateUser(givenUser, givenFile)
                    ).withMessage("Unacceptable image format [" + givenFile.getContentType() + "]");
        }
    }
}