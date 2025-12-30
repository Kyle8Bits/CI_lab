package com.kyle.template.todo.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.kyle.template.todo.model.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUserId(String userId);

    @Query(value = "{ 'userId': ?0 }", fields = "{ 'fullName': 1, '_id': 0 }")
    Optional<String> findFullNameByUserId(String userId);
}
