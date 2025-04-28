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
        Integer idUser,
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
