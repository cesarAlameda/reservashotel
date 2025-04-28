package com.csaralameda.reservashotel.repositories;

import com.csaralameda.reservashotel.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<User, Long> {
    // JPQL
   // @Query("SELECT r FROM Reserva r WHERE r.estado = :estado")
   // List<Room> buscarPorEstado(@Param("estado") Boolean estado);
}
