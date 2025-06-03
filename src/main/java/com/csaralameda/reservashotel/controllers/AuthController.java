package com.csaralameda.reservashotel.controllers;

import com.csaralameda.reservashotel.config.JwtProperties;
import com.csaralameda.reservashotel.dto.LoginDTO;
import com.csaralameda.reservashotel.dto.UserDTO;
import com.csaralameda.reservashotel.models.User;
import com.csaralameda.reservashotel.repositories.UsersRepository;
import com.csaralameda.reservashotel.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    public AuthController(UsersRepository usersRepository, PasswordEncoder passwordEncoder, JwtProperties jwtProperties) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
    }
    @Operation(
            summary = "Logueo de Usuario",
            description = "Este endpoint permite loguearse a los usuarios"
    )
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

    @Operation(
            summary = "Registro de Usuario",
            description = "Este endpoint permite registrar usuarios en la base de datos"
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        try {
            log.info("Registrando nuevo usuario...");
            User user = new User();
            user.setUsername(userDTO.username());
            user.setEmail(userDTO.email());
            user.setPassword(passwordEncoder.encode(userDTO.password()));

            user.setRole(User.Role.USER);

            usersRepository.save(user);
            log.info("Usuario registrado: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (DataIntegrityViolationException dive) {
            log.error("Error de integridad al registrar usuario {}: {}", userDTO.username(), dive.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nombre de usuario o email ya en uso");

        } catch (Exception e) {
            log.error("Error inesperado al registrar usuario {}", userDTO.username(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
    public record TokenResponse(String token) {}
}
