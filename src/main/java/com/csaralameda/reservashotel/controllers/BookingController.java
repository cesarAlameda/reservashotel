package com.csaralameda.reservashotel.controllers;

import com.csaralameda.reservashotel.dto.BookingDTO;
import com.csaralameda.reservashotel.models.Booking;
import com.csaralameda.reservashotel.models.Room;
import com.csaralameda.reservashotel.models.Service;
import com.csaralameda.reservashotel.repositories.BookingRepository;
import com.csaralameda.reservashotel.repositories.RoomRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/booking"})
public class BookingController {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    public BookingController(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    @GetMapping({"/{idBooking}"})
    public ResponseEntity<Booking> getBookingById(@PathVariable("idBooking") Long idBooking) {
        Optional<Booking> booking = this.bookingRepository.findById(idBooking);
        if (booking.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            Booking bookingObj = (Booking) booking.get();
            return ResponseEntity.ok(bookingObj);
        }
    }

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
         //Long id, String startDate, String endDate, Integer idUser, Integer idRoom, Set< Service > services

         //startDate
         String startDate=bookingDTO.startDate();
         if (startDate != null) {
             if (!startDate.isBlank()) {
                 bookingObj.setStartDate(startDate);
             }
         } else {
             log.info("FECHA DE ENTRADA nulo o en blanco a la hora de actualizar la reserva");
         }

         //endDate
         String endDate=bookingDTO.endDate();
         if (endDate != null) {
             if (!endDate.isBlank()) {
                 bookingObj.setEndDate(endDate);
             }
         } else {
             log.info("FECHA DE ENTRADA nulo o en blanco a la hora de actualizar la reserva");
         }

         //idRoom ( por si se quiere cambiar de habitacion bajo las mismas condiciones de servicio y fecha, habría que comprobar si la habitacion está disponible)
         //para comprobar que la habitacion está disponible simplemente listar como opciones solo habitaciones que tengan el available en true
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


}
