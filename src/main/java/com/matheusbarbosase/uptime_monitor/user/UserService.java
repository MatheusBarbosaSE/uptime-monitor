package com.matheusbarbosase.uptime_monitor.user;

import com.matheusbarbosase.uptime_monitor.model.User;
import com.matheusbarbosase.uptime_monitor.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public UserDetailsResponse getUserDetails() {
        User user = getAuthenticatedUser();
        return new UserDetailsResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    @Transactional
    public UserDetailsResponse updateUser(UpdateUserRequest request) {
        User user = getAuthenticatedUser();

        user.setUsername(request.username());
        user.setEmail(request.email());
        User savedUser = userRepository.save(user);

        return new UserDetailsResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = getAuthenticatedUser();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.getOldPassword()
                )
        );

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalStateException("New password cannot be the same as the old password.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}