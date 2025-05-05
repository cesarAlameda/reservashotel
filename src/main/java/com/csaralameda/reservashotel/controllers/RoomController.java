package com.csaralameda.reservashotel.controllers;

import com.csaralameda.reservashotel.models.Room;
import com.csaralameda.reservashotel.repositories.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping({"/rooms"})
public class RoomController {
    private RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public Iterable<Room>getRooms(){return this.roomRepository.findAll();}


    @GetMapping({"/available"}) //saber las habitaciones que están disponibles de cara al apartado front
    public Iterable<Room> getRoomsAvaible() {
        ArrayList<Room> rooms = (ArrayList<Room>) this.roomRepository.buscarPorEstado();
        return rooms;
    }


}
