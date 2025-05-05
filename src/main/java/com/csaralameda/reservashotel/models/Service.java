package com.csaralameda.reservashotel.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "services")
    @JsonBackReference
    private Set<Room> rooms = new HashSet<>();

    @ManyToMany(mappedBy = "services")
    @JsonBackReference
    private Set<Booking> bookings = new HashSet<>();

    public String getName() {
        return name;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public String getDescription() {
        return description;
    }

    public Long getId() {
        return id;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }
}
