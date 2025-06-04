package com.csaralameda.reservashotel.dto;

import com.csaralameda.reservashotel.models.Service;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ServiceDTO(
        Long id,
        @NotBlank(message = "El nombre del servicio es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre del servicio debe tener entre 3 y 50 caracteres")
        String name,

        @NotBlank(message = "La description del servicio es obligatorio")
        @Size(min = 3, max = 200, message = "La description del servicio debe tener entre 3 y 200 caracteres")
        String description
) {
    public static ServiceDTO fromEntity(Service service) {
        return new ServiceDTO(
                service.getId(),
                service.getName(),
                service.getDescription()
        );
    }
}
