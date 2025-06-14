package com.csaralameda.reservashotel.controllers;

import com.csaralameda.reservashotel.dto.BookingDTO;
import com.csaralameda.reservashotel.dto.BookingPatchDTO;
import com.csaralameda.reservashotel.models.Booking;
import com.csaralameda.reservashotel.models.Room;
import com.csaralameda.reservashotel.models.Service;
import com.csaralameda.reservashotel.models.User;
import com.csaralameda.reservashotel.repositories.BookingRepository;
import com.csaralameda.reservashotel.repositories.RoomRepository;
import com.csaralameda.reservashotel.repositories.ServiceRepository;
import com.csaralameda.reservashotel.repositories.UsersRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/booking"})
public class BookingController {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ServiceRepository serviceRepository;
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    public BookingController(BookingRepository bookingRepository, RoomRepository roomRepository, ServiceRepository serviceRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.serviceRepository = serviceRepository;
    }

    @Operation(
            summary = "Obtiene una reserva por ID",
            description = "Este endpoint permite buscar una reserva en la base de datos usando su ID"
    )
    @GetMapping({"/{idBooking}"})
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONIST')")
    public ResponseEntity<Booking> getBookingById(@PathVariable("idBooking") Long idBooking) {
        Optional<Booking> booking = this.bookingRepository.findById(idBooking);
        if (booking.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            Booking bookingObj = booking.get();
            return ResponseEntity.ok(bookingObj);
        }
    }

    @Operation(
            summary = "Registro de reservas",
            description = "Este endpoint permite registrar reservas en la base de datos"
    )
    @PostMapping
    public ResponseEntity<Void> postUser(@Valid @RequestBody BookingDTO bookingDTO) {
        try {
            log.info("Creando reserva....");
            Booking booking = new Booking();
            //Long id, String startDate, String endDate, Integer idUser, Integer idRoom, Set< Service > services
            booking.setStartDate(bookingDTO.startDate());
            booking.setEndDate(bookingDTO.endDate());
            booking.setIdUser(bookingDTO.idUser());
            booking.setIdRoom(bookingDTO.idRoom());


            //accedo a los services de bookingDTO
            Set<Service> services = bookingDTO.services().stream()
                    .map(dto -> {
                        Service service = new Service();
                        service.setId(dto.id());
                        service.setName(dto.name());
                        service.setDescription(dto.description());
                        return service;
                    })
                    .collect(Collectors.toSet());

            booking.setServices(services);
            bookingRepository.save(booking);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DataIntegrityViolationException dive) {
            log.error("Violación de integridad al crear reserva {}: {}", bookingDTO.id(), dive.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (Exception e) {
            log.error("Error inesperado al crear reserva {}", bookingDTO.id(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Borrado de reservas",
            description = "Este endpoint permite borrar reservas en la base de datos"
    )
    @DeleteMapping("/{idBooking}")
    public ResponseEntity<Void> deleteBooking(@PathVariable("idBooking") Long idBooking) {
        log.info("Borrando Booking...");
        Optional<Booking> bookingOpt = bookingRepository.findById(idBooking);

        if (bookingOpt.isEmpty()) {
            log.warn("Intento de borrado de reserva no existente: id{}", idBooking);
            return ResponseEntity.notFound().build();
        }

        bookingRepository.deleteById(idBooking);
        log.info("Booking con id {} borrado correctamente", idBooking);


        return ResponseEntity.noContent().build();//204 NO content
    }

    @Operation(
            summary = "Editar de reservas",
            description = "Este endpoint permite editar reservas en la base de datos"
    )
    @PutMapping("/{idBooking}")
    public ResponseEntity<Void> putBooking(@PathVariable("idBooking") Long idBooking, @Valid @RequestBody BookingDTO bookingDTO) {



        log.info("Actualizando Usuario...");
        Optional<Booking>bookingOptional=bookingRepository.findById(idBooking);
        if(bookingOptional.isEmpty()){
            log.warn("Intento de actualización en una reserva no existente: id {}",idBooking);
            return ResponseEntity.notFound().build();

        }

     try{
         Booking bookingObj=bookingOptional.get();
         //String startDate, String endDate, Integer idUser, Integer idRoom, Set< Service > services

         //startDate
         bookingObj.setStartDate(bookingDTO.startDate());

         //endDate
        bookingObj.setEndDate(bookingDTO.endDate());


         //idRoom ( por si se quiere cambiar de habitacion bajo las mismas condiciones de servicio y fecha, habría que comprobar si la habitacion está disponible)
         //de todos modos lo comprobaré a la hora de hacer el put para añadir robustez
         Integer idRoom=bookingDTO.idRoom();
         if(idRoom!=null || idRoom!=0){
             Optional<Room> habitacionnueva=roomRepository.findById(Long.valueOf(idRoom));
             if (habitacionnueva.isEmpty()) {
                 log.warn("Intento de actualizar a la habitación fallido");
                 log.warn("ID no encontrada: {}", idRoom);
                 return ResponseEntity.notFound().build();
             }else if(habitacionnueva.get().getAvailable()){
                    bookingObj.setIdRoom(idRoom);
             }else{
                 log.warn("Habitacion ya reservada: {}", idRoom);
                 return ResponseEntity.status(HttpStatus.CONFLICT).build(); //409
             }
         }


         //cambiar los servicios
         bookingObj.getServices().clear();
         Set<Service> services = bookingDTO.services().stream()
                 .map(dto -> {
                     Service service = new Service();
                     service.setId(dto.id());
                     service.setName(dto.name());
                     service.setDescription(dto.description());
                     return service;
                 })
                 .collect(Collectors.toSet());

         bookingObj.setServices(services);



         bookingRepository.save(bookingObj);
         log.info("Reserva {} actualizada correctamente", idBooking);
         return ResponseEntity.ok().build();
     }catch (DataIntegrityViolationException dive) {
         log.error("Violación de integridad al actualizar la reserva {}: {}", idBooking, dive.getMessage());
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

     } catch (Exception e) {
         log.error("Error inesperado al actualizar la reserva {}", idBooking, e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
     }

    }
    @Operation(
            summary = "Modificación parcial de una reserva",
            description = "Este endpoint permite modificar parcialmente los campos de una reserva existente"
    )
    @PatchMapping("/{idBooking}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPCIONIST')")
    public ResponseEntity<Void> patchBooking(@PathVariable("idBooking") Long idBooking,
                                             @RequestBody BookingPatchDTO bookingPatchDTO) {
        log.info("Iniciando modificación parcial de reserva {}", idBooking);

        Optional<Booking> bookingOpt = bookingRepository.findById(idBooking);
        if (bookingOpt.isEmpty()) {
            log.warn("Reserva con ID {} no encontrada", idBooking);
            return ResponseEntity.notFound().build();
        }

        try {
            Booking booking = bookingOpt.get();

            Set<Service> serviciosFiltrados = null;
            if (bookingPatchDTO.serviceIds() != null) {
                serviciosFiltrados = bookingPatchDTO.serviceIds().stream()
                        .map(id -> serviceRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Servicio no encontrado con id=" + id)))
                        .collect(Collectors.toSet());
            }

            bookingPatchDTO.applyTo(booking, serviciosFiltrados);
            bookingRepository.save(booking);

            log.info("Reserva {} modificada parcialmente con éxito", idBooking);
            return ResponseEntity.ok().build();

        } catch (DataIntegrityViolationException dive) {
            log.error("Violación de integridad al modificar reserva {}: {}", idBooking, dive.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Error inesperado al modificar parcialmente la reserva {}", idBooking, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
