package com.csaralameda.reservashotel.controllers;

import com.csaralameda.reservashotel.dto.UserDTO;
import com.csaralameda.reservashotel.models.User;
import com.csaralameda.reservashotel.repositories.UsersRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(
            summary = "Lista todos los usuarios",
            description = "Este endpoint permite listar todos los usuarios de la base de datos"
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Iterable<User> getUsers() {
        return usersRepository.findAll();
    }

    @Operation(
            summary = "Obtiene un usuario por ID",
            description = "Este endpoint permite buscar un usuario en la base de datos usando su ID"
    )
    @GetMapping("/{idUser}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable("idUser") Long idUser) {
        Optional<User> userOpt = usersRepository.findById(idUser);
        return userOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Usuario con id {} no encontrado", idUser);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(
            summary = "Borrado de Usuario",
            description = "Este endpoint permite eliminar usuarios en la base de datos usando su ID"
    )
    @DeleteMapping("/{idUser}")
    public ResponseEntity<Void> deleteUser(@PathVariable("idUser") Long idUser) {
        log.info("Borrando Usuario...");
        Optional<User> userOpt = usersRepository.findById(idUser);

        if (userOpt.isEmpty()) {
            log.warn("Intento de borrado de usuario no existente: id {}", idUser);
            return ResponseEntity.notFound().build();
        }

        usersRepository.deleteById(idUser);
        log.info("Usuario con id {} borrado correctamente", idUser);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @Operation(
            summary = "Editar Usuario",
            description = "Este endpoint permite editar usuarios en la base de datos usando su ID"
    )
    @PutMapping("/{idUser}")
    public ResponseEntity<Void> putUser(
            @PathVariable("idUser") Long idUser,
            @Valid @RequestBody UserDTO userDTO
    ) {
        log.info("Actualizando Usuario...");
        Optional<User> usersOptional = usersRepository.findById(idUser);
        if (usersOptional.isEmpty()) {
            log.warn("Intento de actualizacion de usuario no existente: id {}", idUser);
            return ResponseEntity.notFound().build();
        }

        try {
            User userObj = usersOptional.get();

            //solo actualizo los campos permitidos validados en dto
            userObj.setUsername(userDTO.username());
            userObj.setEmail(userDTO.email());
            userObj.setPassword(passwordEncoder.encode(userDTO.password()));
            // userObj.setRole(userDTO.role()); //no cambiar el rol desde aquí

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
