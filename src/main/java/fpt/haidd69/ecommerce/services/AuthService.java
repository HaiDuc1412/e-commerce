package fpt.haidd69.ecommerce.services;

import fpt.haidd69.ecommerce.dto.auth.AuthResponse;
import fpt.haidd69.ecommerce.dto.auth.LoginRequest;
import fpt.haidd69.ecommerce.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);
}
