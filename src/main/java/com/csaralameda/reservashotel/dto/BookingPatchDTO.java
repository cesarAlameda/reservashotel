package com.csaralameda.reservashotel.dto;
import com.csaralameda.reservashotel.models.Booking;
import com.csaralameda.reservashotel.models.Service;

import java.util.Set;

public record BookingPatchDTO(
        String startDate,
        String endDate,
        Set<Long> serviceIds


) {

    public void applyTo(Booking booking, Set<Service> serviciosFiltrados) {
        if (this.startDate != null && !this.startDate.isBlank()) {
            booking.setStartDate(this.startDate);
        }
        if (this.endDate != null && !this.endDate.isBlank()) {
            booking.setEndDate(this.endDate);
        }
        if (serviciosFiltrados != null) {
            booking.setServices(serviciosFiltrados);
        }
    }

}
