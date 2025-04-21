package com.csaralameda.reservashotel.controllers;

import com.csaralameda.reservashotel.dto.UserDTO;
import com.csaralameda.reservashotel.models.Users;
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
    public Iterable<Users> getUsers() {
        return this.usersRepository.findAll();
    }

    @GetMapping({"/{idUser}"})
    public ResponseEntity<Users> getUserById(@PathVariable("idUser") Long idUser) {
        Optional<Users> users = this.usersRepository.findById(idUser);
        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            Users usersObj = (Users) users.get();
            return ResponseEntity.ok(usersObj);
        }
    }

    @PostMapping
    public ResponseEntity<Void> postUser(@RequestBody UserDTO userDTO) {
        Users users = new Users();
        users.setUsername(userDTO.username());
        this.usersRepository.save(users);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping({"/{idUser}"})
    public ResponseEntity<Void> putUser(@PathVariable("idUser") Long idUsers, @RequestBody UserDTO userDTO) {
        Optional<Users> usersOptional = this.usersRepository.findById(idUsers);
        if (usersOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            Users usersObj = (Users) usersOptional.get();
            usersObj.setUsername(userDTO.username());
            this.usersRepository.save(usersObj);
            return ResponseEntity.ok().build();
        }
    }
}
