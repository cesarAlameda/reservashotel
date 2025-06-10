package com.csaralameda.reservashotel.dto;

import com.csaralameda.reservashotel.models.Room;
import com.csaralameda.reservashotel.models.Service;

import java.util.Set;
public record RoomPatchDTO(
        String type,
        Double price,
        Integer capacity,
        Boolean isAvailable,
        Set<Long> serviceIds //uso de ids para optimización 
) {
    public void applyTo(Room room, Set<Service> serviciosFiltrados) {
        if (this.type != null && !this.type.isBlank()) {
            room.setType(this.type);
        }
        if (this.price != null) {
            room.setPrice(this.price);
        }
        if (this.capacity != null) {
            room.setCapacity(this.capacity);
        }
        if (this.isAvailable != null) {
            room.setAvailable(this.isAvailable);
        }
        if (serviciosFiltrados != null) {
            room.setServices(serviciosFiltrados);
        }
    }
}
