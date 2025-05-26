package com.csaralameda.reservashotel.dto;

import com.csaralameda.reservashotel.models.User;
import jakarta.validation.constraints.Email;


public record UserDTO(
        Long id,
        String username,
        @Email String email,
        String password,
        User.Role role
) {
    public static User fromDTO(UserDTO dto) {
        User user = new User();
        user.setId(dto.id());
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(dto.role());
        return user;
    }
}
