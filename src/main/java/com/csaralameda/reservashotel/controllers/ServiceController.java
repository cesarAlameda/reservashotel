package com.csaralameda.reservashotel.controllers;


import com.csaralameda.reservashotel.dto.ServiceDTO;
import com.csaralameda.reservashotel.dto.ServicePatchDTO;
import com.csaralameda.reservashotel.dto.UserPatchDTO;
import com.csaralameda.reservashotel.models.Service;
import com.csaralameda.reservashotel.models.User;
import com.csaralameda.reservashotel.repositories.ServiceRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/service")
public class ServiceController {
    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);
    private final ServiceRepository serviceRepository;

    public ServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }


    @Operation(
            summary = "Lista todos los servicios",
            description = "Este endpoint permite listar todos los servicios de la base de datos"
    )
    @GetMapping
    public Iterable<Service> getService() {
        return serviceRepository.findAll();
    }


    @Operation(
            summary = "Obtiene un servicio por ID",
            description = "Este endpoint permite buscar un servicio en la base de datos usando su ID"
    )
    @GetMapping("/{idService}")
    public ResponseEntity<Service> getServiceId(@PathVariable("idService") Long idService) {
        Optional<Service> serviceOpt = serviceRepository.findById(idService);
        return serviceOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Service con id {} no encontrado", idService);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(
            summary = "Registro de un servicio",
            description = "Este endpoint permite registrar servicios en la base de datos"
    )
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONIST')")
    public ResponseEntity<Void> postService(@Valid @RequestBody ServiceDTO serviceDTO) {
        try {
            log.info("Creando Servicio...");
            Service service = new Service();

            service.setName(serviceDTO.name());
            service.setDescription(serviceDTO.description());

            serviceRepository.save(service);
            log.info("Servicio creado: {}", service.getName());
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (DataIntegrityViolationException dive) {
            log.error("Violación de integridad al crear servicio {}: {}",
                    serviceDTO.name(), dive.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Error inesperado al crear servicio {}", serviceDTO.name(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @Operation(
            summary = "Borrado de un servicio",
            description = "Este endpoint permite borrar servicios en la base de datos"
    )
    @DeleteMapping("/{idService}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONIST')")
    public ResponseEntity<Void> deleteService(@PathVariable("idService") Long idService) {
        log.info("Borrando Servicio...");
        Optional<Service> serviceOpt = serviceRepository.findById(idService);

        if (serviceOpt.isEmpty()) {
            log.warn("Intento de borrado de servicio no existente: id {}", idService);
            return ResponseEntity.notFound().build();
        }


        serviceRepository.deleteById(idService);
        log.info("Servicio con id {} borrado correctamente", idService);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


    @Operation(
            summary = "Editado de un servicio",
            description = "Este endpoint permite editar servicios en la base de datos"
    )
    @PutMapping("/{idService}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONIST')")
    public ResponseEntity<Void> putService(
            @PathVariable("idService") Long idService,
            @Valid @RequestBody ServiceDTO serviceDTO
    ) {
        log.info("Actualizando Servicio...");

        Optional<Service> serviceOptional = serviceRepository.findById(idService);
        if (serviceOptional.isEmpty()) {
            log.warn("Intento de actualizacion del servicio no existente: id {}", idService);
            return ResponseEntity.notFound().build();
        }
        //name, description
        try {
            Service serviceObj = serviceOptional.get();


            serviceObj.setName(serviceDTO.name());
            serviceObj.setDescription(serviceDTO.description());


            serviceRepository.save(serviceObj);
            log.info("Servicio {} actualizado correctamente", idService);
            return ResponseEntity.ok().build();


        } catch (DataIntegrityViolationException dive) {
            log.error("Violación de integridad al actualizar servicio {}: {}", idService, dive.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (Exception e) {
            log.error("Error inesperado al actualizar servicio {}", idService, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }
    @Operation(
            summary = "Actualiza parcialmente un servicio",
            description = "Este endpoint permite actualizar campos específicos de un servicio"
    )
    @PatchMapping("/{idService}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> patchService(
            @PathVariable("idService") Long idService,
            @RequestBody ServicePatchDTO patchDTO
    ) {
        log.info("Actualización parcial del servicio con id {}", idService);

        Optional<Service> serviceOpt = serviceRepository.findById(idService);
        if (serviceOpt.isEmpty()) {
            log.warn("Servicio con id {} no encontrado", idService);
            return ResponseEntity.notFound().build();
        }

        try {
            Service service = serviceOpt.get();
            patchDTO.applyTo(service);
            serviceRepository.save(service);
            log.info("Servicio {} actualizado parcialmente con éxito", idService);
            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException dive) {
            log.error("Error de integridad al actualizar parcialmente el servicio: {}", dive.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Error inesperado al hacer PATCH al servicio {}", idService, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
