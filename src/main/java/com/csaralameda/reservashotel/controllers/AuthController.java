package com.csaralameda.reservashotel.controllers;

import com.csaralameda.reservashotel.config.JwtProperties;
import com.csaralameda.reservashotel.dto.LoginDTO;
import com.csaralameda.reservashotel.models.User;
import com.csaralameda.reservashotel.repositories.UsersRepository;
import com.csaralameda.reservashotel.util.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    public AuthController(UsersRepository usersRepository, PasswordEncoder passwordEncoder, JwtProperties jwtProperties) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        User user = usersRepository.findByUsername(loginDTO.username()).orElse(null);

        if (user == null || !passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        String token = JWTUtil.generateToken(user.getUsername(), List.of(String.valueOf(user.getRole())),jwtProperties.getSecret());

        return ResponseEntity.ok().body(
                new TokenResponse(token)
        );
    }

    public record TokenResponse(String token) {}
}
