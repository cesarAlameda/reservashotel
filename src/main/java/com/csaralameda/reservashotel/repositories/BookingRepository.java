package com.csaralameda.reservashotel.repositories;

import com.csaralameda.reservashotel.models.Booking;
import org.springframework.data.repository.CrudRepository;

public interface BookingRepository extends CrudRepository<Booking, Long> {


}
