package com.csaralameda.reservashotel.controllers;

import com.csaralameda.reservashotel.dto.RoomDTO;
import com.csaralameda.reservashotel.models.Room;
import com.csaralameda.reservashotel.models.Service;
import com.csaralameda.reservashotel.repositories.RoomRepository;
import com.csaralameda.reservashotel.repositories.ServiceRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/rooms"})
public class RoomController {
    private RoomRepository roomRepository;
    private static final Logger log = LoggerFactory.getLogger(RoomController.class);
    private final ServiceRepository serviceRepository;

    public RoomController(RoomRepository roomRepository, ServiceRepository serviceRepository) {
        this.roomRepository = roomRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public Iterable<Room> getRooms() {
        return this.roomRepository.findAll();
    }


    @GetMapping("/{idRoom}")
    public ResponseEntity<Room> getRoombyId(@PathVariable("idRoom") Long idRoom) {
        Optional<Room> roomOpt = roomRepository.findById(idRoom);
        return roomOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Usuario con id {} no encontrado", idRoom);
                    return ResponseEntity.notFound().build();
                });

    }

    @GetMapping({"/available"}) //saber las habitaciones que están disponibles de cara al apartado front
    public Iterable<Room> getRoomsAvaible() {
        ArrayList<Room> rooms = (ArrayList<Room>) this.roomRepository.buscarPorEstado();
        return rooms;
    }

    @PostMapping
    public ResponseEntity<Void> postRoom(@Valid @RequestBody RoomDTO roomDTO) {
        try {
            log.info("Creando Habitacion...");
            Room room = new Room();
            room.setType(roomDTO.type());
            room.setPrice(roomDTO.price());
            room.setCapacity(roomDTO.capacity());
            room.setAvailable(roomDTO.isAvailable());
            room.setIdUser(roomDTO.idUser());

            Set<Service> servicios = roomDTO.services().stream()
                    .map(dto -> serviceRepository.findById(dto.id())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Servicio no encontrado con id=" + dto.id())))
                    .collect(Collectors.toSet());

            servicios.forEach(room::addService);

            roomRepository.save(room);
            log.info("Habitacion creada con servicios");
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (DataIntegrityViolationException dive) {
            log.error("Violación de integridad al crear la habitacion : {}", dive.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (Exception e) {
            log.error("Error inesperado al crear la habitacion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{idRoom}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("idRoom") Long idRoom) {
        log.info("Borrando habitacion...");
        Optional<Room> roomOpt = roomRepository.findById(idRoom);

        if (roomOpt.isEmpty()) {
            log.warn("Intento de borrar a la habitación fallido");
            log.warn("ID no encontrada: {}", idRoom);
            return ResponseEntity.notFound().build();
        }

        roomRepository.deleteById(idRoom);
        log.info("Habitacion encontrada y borrada perfectamente con id {}", idRoom);
        return ResponseEntity.noContent().build();

    }


    @PutMapping("/{idRoom}")
    public ResponseEntity<Void> putRoom(@PathVariable("idRoom") Long idRoom,
                                        @Valid @RequestBody RoomDTO roomDTO) {
        log.info("Actualizando Habitacion...");

        Optional<Room> roomOptional = roomRepository.findById(idRoom);
        if (roomOptional.isEmpty()) {
            log.warn("Intento de actualizacion sobre una habitacion no existente: id {}", idRoom);
            return ResponseEntity.notFound().build();
        }
        //CAMBIO DE type, price, capacity, available, lista de servicios (el resto se mantiene)

        try {
            Room roomObj = roomOptional.get();

            //type
            String type = roomDTO.type();
            if (type != null) {
                if (!type.isBlank()) {
                    roomObj.setType(type);
                }
            } else {
                log.info("Tipo de habitación nulo o en blanco al actualizar");
            }

            //price
            Double price = roomDTO.price();
            if (price != null) {
                if (price > 0) {
                    roomObj.setPrice(price);
                }
            } else {
                log.info("Precio nulo al actualizar");
            }

            //capacity
            Integer capacity = roomDTO.capacity();
            if (capacity != null) {
                if (capacity > 0) {
                    roomObj.setCapacity(capacity);
                }
            } else {
                log.info("Capacidad nula al actualizar");
            }

            //available (nunca será null puesto que es un boolean primitivo)
            roomObj.setAvailable(roomDTO.isAvailable());

            //services(dando por hecho que alguna habitacion puede no tener ningun servicio)
            if (roomDTO.services() != null) {


                roomObj.getServices().clear();

                //cargo los servicios de la base de datos
                Set<Service> nuevosServicios = roomDTO.services().stream()
                        .map(svcDto -> serviceRepository.findById(svcDto.id())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Servicio no encontrado con id=" + svcDto.id())))
                        .collect(Collectors.toSet());


                nuevosServicios.forEach(roomObj::addService);
            }

            roomRepository.save(roomObj);
            log.info("Habitación {} actualizada correctamente", idRoom);
            return ResponseEntity.ok().build();

        } catch (DataIntegrityViolationException dive) {
            log.error("Violación de integridad al actualizar la habitación {}: {}", idRoom, dive.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Error inesperado al actualizar habitación {}", idRoom, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }


}
