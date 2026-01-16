package fpt.haidd69.ecommerce.services.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fpt.haidd69.ecommerce.dto.auth.AuthResponse;
import fpt.haidd69.ecommerce.dto.auth.LoginRequest;
import fpt.haidd69.ecommerce.dto.auth.RegisterRequest;
import fpt.haidd69.ecommerce.entities.User;
import fpt.haidd69.ecommerce.enums.Role;
import fpt.haidd69.ecommerce.exceptions.UnauthorizedException;
import fpt.haidd69.ecommerce.repositories.UserRepository;
import fpt.haidd69.ecommerce.services.AuthService;
import fpt.haidd69.ecommerce.services.TokenService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = tokenService.generateToken(user, user.getRole().name());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
        );
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(Role.CUSTOMER); // Mặc định là CUSTOMER
        user.setActive(true);

        user = userRepository.save(user);

        String token = tokenService.generateToken(user, user.getRole().name());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
        );
    }
}
