package com.csaralameda.reservashotel.dto;

import com.csaralameda.reservashotel.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDTO(
        Long id,
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotNull User.Role role
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
