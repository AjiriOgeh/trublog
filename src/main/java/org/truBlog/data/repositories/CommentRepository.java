package org.truBlog.data.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.truBlog.data.models.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {
}
