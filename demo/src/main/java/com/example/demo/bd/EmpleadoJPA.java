package com.example.demo.bd;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface EmpleadoJPA extends MongoRepository<EmpleadoORM,Integer> {

    @Query("{EmpleadoEmail: ?0 }")
    EmpleadoORM findByEmail(String EmpleadoEmail);

}
