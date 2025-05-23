package com.csaralameda.reservashotel.dto;

import com.csaralameda.reservashotel.models.Room;
import java.util.Set;
import java.util.stream.Collectors;

public record RoomDTO(
        Long id,
        String type,
        Double price,
        Integer capacity,
        Boolean isAvailable,
        Integer idUser, //el id user es para saber que usuario la ha creado, si no se quiere saber o no se necesita se pondra 0
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
