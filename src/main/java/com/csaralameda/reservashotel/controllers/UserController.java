package com.csaralameda.reservashotel.controllers;

import com.csaralameda.reservashotel.dto.UserDTO;
import com.csaralameda.reservashotel.models.User;
import com.csaralameda.reservashotel.repositories.UsersRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public Iterable<User> getUsers() {
        return usersRepository.findAll();
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<User> getUserById(@PathVariable("idUser") Long idUser) {
        Optional<User> userOpt = usersRepository.findById(idUser);
        return userOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Usuario con id {} no encontrado", idUser);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<Void> postUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            User user = new User();
            user.setUsername(userDTO.username());
            user.setEmail(userDTO.email());
            user.setRole(userDTO.role());
            user.setPassword(passwordEncoder.encode(userDTO.password()));

            usersRepository.save(user);
            log.info("Usuario creado: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (DataIntegrityViolationException dive) {
            log.error("Violación de integridad al crear usuario {}: {}", userDTO.username(), dive.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (Exception e) {
            log.error("Error inesperado al crear usuario {}", userDTO.username(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{idUser}")
    public ResponseEntity<Void> putUser(
            @PathVariable("idUser") Long idUser,
            @Valid @RequestBody UserDTO userDTO
    ) {
        Optional<User> usersOptional = usersRepository.findById(idUser);
        if (usersOptional.isEmpty()) {
            log.warn("Intento de actualización de usuario no existente: id {}", idUser);
            return ResponseEntity.notFound().build();
        }

        try {
            User userObj = usersOptional.get();
            userObj.setUsername(userDTO.username());
            userObj.setEmail(userDTO.email());
            userObj.setRole(userDTO.role());

            if (userDTO.password() != null && !userDTO.password().isEmpty()) {
                userObj.setPassword(passwordEncoder.encode(userDTO.password()));
            }

            usersRepository.save(userObj);
            log.info("Usuario {} actualizado correctamente", idUser);
            return ResponseEntity.ok().build();

        } catch (DataIntegrityViolationException dive) {
            log.error("Violación de integridad al actualizar usuario {}: {}", idUser, dive.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (Exception e) {
            log.error("Error inesperado al actualizar usuario {}", idUser, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
