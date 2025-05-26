package com.csaralameda.reservashotel.controllers;

import com.csaralameda.reservashotel.models.Booking;
import com.csaralameda.reservashotel.repositories.BookingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping({"/booking"})
public class BookingController {
    private final BookingRepository bookingRepository;

    public BookingController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
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





}
