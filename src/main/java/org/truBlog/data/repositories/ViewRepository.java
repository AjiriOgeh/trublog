package org.truBlog.data.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.truBlog.data.models.View;

public interface ViewRepository extends MongoRepository<View, String> {
}
