package com.csaralameda.reservashotel.repositories;

import com.csaralameda.reservashotel.models.Room;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface RoomRepository extends CrudRepository<Room,Long> {

    @Query("SELECT r FROM Room r WHERE r.available = true")
    ArrayList<Room> buscarPorEstado();

}
