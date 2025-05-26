package com.csaralameda.reservashotel.controllers;


import com.csaralameda.reservashotel.dto.ServiceDTO;
import com.csaralameda.reservashotel.models.Service;
import com.csaralameda.reservashotel.repositories.ServiceRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public Iterable<Service> getService() {
        return serviceRepository.findAll();
    }

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

    @PostMapping
    public ResponseEntity<Void>postService(@Valid @RequestBody ServiceDTO serviceDTO){
        try{
            log.info("Creando Servicio...");
            Service service=new Service();
            //name, description

            service.setName(serviceDTO.name());
            service.setDescription(serviceDTO.description());

            serviceRepository.save(service);
            log.info("Servicio creado: {}", service.getName());
            return ResponseEntity.status(HttpStatus.CREATED).build();

        }catch(DataIntegrityViolationException dive){
            log.error("Violación de integridad al crear servicio {}: {}",
                    serviceDTO.name(), dive.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (Exception e){
            log.error("Error inesperado al crear servicio {}", serviceDTO.name(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @DeleteMapping("/{idService}")
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

    @PutMapping("/{idService}")
    public ResponseEntity<Void> putService(
            @PathVariable("idService") Long idService,
            @Valid @RequestBody ServiceDTO serviceDTO
    ) {
        log.info("Actualizando Servicio...");

        Optional<Service> serviceOptional = serviceRepository.findById(idService);
        if(serviceOptional.isEmpty()){
            log.warn("Intento de actualizacion del servicio no existente: id {}", idService);
            return ResponseEntity.notFound().build();
        }
        //name, description
        try{
            Service serviceObj=serviceOptional.get();

            if(serviceDTO.name()!=null || serviceDTO.name().isEmpty()){
                serviceObj.setName(serviceDTO.name());
            }

            if(serviceDTO.description()!=null || serviceDTO.description().isEmpty()){
                serviceObj.setDescription(serviceDTO.description());
            }

            serviceRepository.save(serviceObj);
            log.info("Servicio {} actualizado correctamente", idService);
            return ResponseEntity.ok().build();


        }catch(DataIntegrityViolationException dive) {
            log.error("Violación de integridad al actualizar servicio {}: {}", idService, dive.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (Exception e) {
            log.error("Error inesperado al actualizar servicio {}", idService, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }




    }






}
