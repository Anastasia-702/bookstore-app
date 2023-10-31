package com.example.bookstore.init;

import com.example.bookstore.model.Role;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.RoleRepository;
import com.example.bookstore.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        Role adminRole = new Role();
        adminRole.setRoleName(Role.RoleName.ROLE_ADMIN);
        roleRepository.save(adminRole);
        Role userRole = new Role();
        userRole.setRoleName(Role.RoleName.ROLE_USER);
        roleRepository.save(userRole);
        User user = new User();
        user.setEmail("admin@i.ua");
        user.setPassword(passwordEncoder.encode("admin1234"));
        user.setFirstName("Admin");
        user.setLastName("Admin");
        user.setRoles(Set.of(roleRepository.findByRoleName(Role.RoleName.ROLE_ADMIN)));
        userRepository.save(user);
    }
}
