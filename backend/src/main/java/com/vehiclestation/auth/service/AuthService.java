package com.vehiclestation.auth.service;

import com.vehiclestation.auth.dto.RegisterRequest;
import com.vehiclestation.auth.dto.RegisterResponse;
import com.vehiclestation.auth.entity.User;
import com.vehiclestation.auth.enums.AccountStatus;
import com.vehiclestation.auth.enums.Role;
import com.vehiclestation.auth.exception.DuplicateEmailException;
import com.vehiclestation.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vehiclestation.auth.dto.LoginRequest;
import com.vehiclestation.auth.dto.LoginResponse;
import com.vehiclestation.auth.exception.InactiveAccountException;
import com.vehiclestation.auth.exception.InvalidCredentialsException;
import com.vehiclestation.auth.security.JwtService;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        String normalizedEmail = request
                .getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateEmailException(
                    "An account already exists with this email address"
            );
        }

        String hashedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getFullName().trim(),
                normalizedEmail,
                request.getPhone().trim(),
                hashedPassword,
                Role.CUSTOMER,
                AccountStatus.ACTIVE
        );

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getUserId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole(),
                savedUser.getAccountStatus(),
                savedUser.getCreatedAt(),
                "Registration successful"
        );
    }

    public LoginResponse login(LoginRequest request) {

        String normalizedEmail = request
                .getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new InactiveAccountException(
                    "This user account is inactive"
            );
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                jwtService.getExpirationSeconds(),
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }
}