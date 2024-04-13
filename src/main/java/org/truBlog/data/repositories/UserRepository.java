package org.truBlog.data.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.truBlog.data.models.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}
