package org.truBlog.data.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.truBlog.data.models.Post;

public interface PostRepository extends MongoRepository<Post, String> {
}
