package com.example.RuleEngineBackend.repository;


import com.example.RuleEngineBackend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RuleRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
