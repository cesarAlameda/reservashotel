package com.csaralameda.reservashotel.dto;

import com.csaralameda.reservashotel.models.Booking;
import com.csaralameda.reservashotel.models.Room;

import java.util.Set;
import java.util.stream.Collectors;


public record  BookingDTO(
        Long id,
        String startDate,
        String endDate,
        Integer idUser,
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
