package com.csaralameda.reservashotel.dto;

import com.csaralameda.reservashotel.models.User;

public record UserDTO(
        Long id,
        String username,
        String email,
        User.Role role
) {
    public static UserDTO fromEntity(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}
