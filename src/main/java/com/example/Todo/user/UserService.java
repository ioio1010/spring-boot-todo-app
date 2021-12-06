package com.example.Todo.user;

import com.example.Todo.exception.BadRequestException;
import com.example.Todo.storage.StorageService;
import com.example.Todo.user.dto.SignUpRequest;
import com.example.Todo.user.dto.UserFilterRequest;
import com.example.Todo.user_role.Role;
import com.example.Todo.user_role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService fileStorageService;
    private final RoleService roleService;
    private final Validator validator;

    // Create user endpoint
    public User createUser(SignUpRequest signUpRequest) {
        Objects.requireNonNull(signUpRequest);

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(signUpRequest);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);

        Role defaultRole = roleService.getDefaultRole();
        User user = new User(
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                List.of(defaultRole)
        );
        userRepository.save(user);

        return user;
    }

    public User createUser(String email, String password, List<Role> roles) {
        Objects.requireNonNull(email);
        Objects.requireNonNull(password);
        Objects.requireNonNull(roles);
        if (roles.isEmpty()) throw new IllegalArgumentException("Roles is empty");

        User user = new User(email, passwordEncoder.encode(password), roles);
        userRepository.save(user);

        return user;
    }

    // Get users endpoint
    public Page<User> getUsers(UserFilterRequest filterParams, Pageable pageable) {
        return userRepository.findAll(buildSpecification(filterParams), pageable);
    }

    // Update user endpoint
    @Transactional
    public User updateUser(User user, MultipartFile avatarFile) throws IOException {
        Objects.requireNonNull(user);

        if (!avatarFile.isEmpty()) updateAvatar(user, avatarFile);

        userRepository.save(user);

        return user;
    }

    private void updateAvatar(User user, MultipartFile avatarFile) throws IOException {
        isImage(avatarFile);
        String fileName = fileStorageService.save(avatarFile);
        user.setAvatar(fileName);
    }

    public Optional<User> getUserBy(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserBy(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        } else {
            return Optional.empty();
        }
    }

    private Specification<User> buildSpecification(UserFilterRequest filterParams) {
        if (filterParams == null) return null;

        return UserSpecification.emailContains(filterParams.email())
                .and(UserSpecification.createdAtDateMatch(filterParams.createdAt()));
    }

    private void isImage(MultipartFile file) {
        List<String> imageFormats = Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType());

        if (!imageFormats.contains(file.getContentType())) {
            throw new BadRequestException("Unacceptable image format [" + file.getContentType() + "]");
        }
    }
}
