package com.csaralameda.reservashotel.dto;

import com.csaralameda.reservashotel.models.Booking;
import jakarta.validation.constraints.NotBlank;


import java.util.Set;
import java.util.stream.Collectors;


public record BookingDTO(
        Long id,
        @NotBlank(message = "La fecha de comienzo de la reserva es obligatoria")
        String startDate,

        @NotBlank(message = "La fecha de finalización de la reserva es obligatoria")
        String endDate,

        @NotBlank(message = "El idUser de reserva es obligatoria")
        Integer idUser,

        @NotBlank(message = "El idRoom de reserva es obligatoria")
        Integer idRoom,
        Set<ServiceDTO> services
) {
    public static BookingDTO fromEntity(Booking booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getIdUser(),
                booking.getIdRoom(),
                booking.getServices().stream()
                        .map(ServiceDTO::fromEntity)
                        .collect(Collectors.toSet())
        );
    }
}
