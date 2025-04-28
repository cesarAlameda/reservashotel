package com.csaralameda.reservashotel.dto;

import com.csaralameda.reservashotel.services.Service;

public record ServiceDTO(
        Long id,
        String name,
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
