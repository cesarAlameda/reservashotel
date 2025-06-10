package com.csaralameda.reservashotel.dto;



import com.csaralameda.reservashotel.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public record UserPatchDTO(
        String username,
        String email,
        String password
        //no incluyo el role por seguridad
) {

    //aplica los cambios recibidos actualizando solo los campos proporcionados del objeto User en una petición PATCH.
    public void applyTo(User user, PasswordEncoder encoder) {
        if (this.username != null && !this.username.isBlank()) {
            user.setUsername(this.username);
        }
        //VALIDAR QUE OTRO USUARIO NO TIENE ESTE CORREO
        if (this.email != null && !this.email.isBlank()) {
            user.setEmail(this.email);
        }
        if (this.password != null && !this.password.isBlank()) {
            user.setPassword(encoder.encode(this.password));
        }
    }
}