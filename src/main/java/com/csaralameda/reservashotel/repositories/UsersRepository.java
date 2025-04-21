package com.csaralameda.reservashotel.repositories;

import com.csaralameda.reservashotel.models.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<Users, Long> {
}
