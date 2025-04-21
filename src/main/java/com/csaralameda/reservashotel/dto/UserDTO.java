package com.csaralameda.reservashotel.dto;

import com.csaralameda.reservashotel.models.Users;

public record UserDTO(
        Long id,
        String username,
        String email,
        Users.Role role
) {
    public static UserDTO fromEntity(Users user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}
