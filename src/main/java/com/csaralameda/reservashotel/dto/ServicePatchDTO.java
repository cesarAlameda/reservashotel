package com.csaralameda.reservashotel.dto;


import com.csaralameda.reservashotel.models.Service;

public record ServicePatchDTO (
        String name,
        String description
){
    public void applyTo(Service service) {
        if (this.name != null && !this.name.isBlank()) {
            service.setName(this.name);
        }
        if (this.description != null && !this.description.isBlank()) {
            service.setDescription(this.description);
        }
    }
}
