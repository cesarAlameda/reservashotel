package com.csaralameda.reservashotel.dto;

import com.csaralameda.reservashotel.models.Room;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.stream.Collectors;

public record RoomDTO(
        Long id,
        @NotBlank(message = "El tipo de habitación es obligatorio")
        @Size(min = 3, max = 20, message = "El tipo de habitación debe tener entre 3 y 20 caracteres")
        String type,

        @NotBlank(message = "El precio de habitación es obligatorio")
        Double price,

        @NotBlank(message = "La capacidad de habitación es obligatoria")
        Integer capacity,

        @NotBlank(message = "La disponibilidad de habitación es obligatoria")
        Boolean isAvailable,

        @NotBlank(message = "El idUser de habitación es obligatoria")
        Integer idUser,
        //el id user es para saber que usuario la ha creado, si no se quiere saber o no se necesita se pondra 0
        Set<ServiceDTO> services
) {
    public static RoomDTO fromEntity(Room room) {
        return new RoomDTO(
                room.getId(),
                room.getType(),
                room.getPrice(),
                room.getCapacity(),
                room.getAvailable(),
                room.getIdUser(),
                room.getServices().stream()
                        .map(ServiceDTO::fromEntity)
                        .collect(Collectors.toSet())
        );
    }
}
