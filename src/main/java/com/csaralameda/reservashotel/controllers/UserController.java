package com.csaralameda.reservashotel.controllers;

import com.csaralameda.reservashotel.dto.UserDTO;
import com.csaralameda.reservashotel.models.User;
import com.csaralameda.reservashotel.repositories.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@RestController
@RequestMapping({"/user"})
public class UserController {
    private final UsersRepository usersRepository;


    public UserController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @GetMapping
    public Iterable<User> getUsers() {
        return this.usersRepository.findAll();
    }

    @GetMapping({"/{idUser}"})
    public ResponseEntity<User> getUserById(@PathVariable("idUser") Long idUser) {
        Optional<User> users = this.usersRepository.findById(idUser);
        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            User userObj = (User) users.get();
            return ResponseEntity.ok(userObj);
        }
    }

    @PostMapping
    public ResponseEntity<Void> postUser(@RequestBody UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.username());
        this.usersRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping({"/{idUser}"})
    public ResponseEntity<Void> putUser(@PathVariable("idUser") Long idUsers, @RequestBody UserDTO userDTO) {
        Optional<User> usersOptional = this.usersRepository.findById(idUsers);
        if (usersOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            User userObj = (User) usersOptional.get();
            userObj.setUsername(userDTO.username());
            this.usersRepository.save(userObj);
            return ResponseEntity.ok().build();
        }
    }
}
