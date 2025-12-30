package com.kyle.template.todo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.kyle.template.todo.model.entity.AuthEntity;
import java.util.Optional;

@Repository
public interface AuthRepository extends MongoRepository<AuthEntity, String> {

    @Query(value = "{ 'username': { $regex: ?0, $options: 'i' } }", exists = true)
    boolean existsByUsernameIgnoreCase(String email);

    Optional<AuthEntity> findByUsernameIgnoreCase(String username);
}
