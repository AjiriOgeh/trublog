package org.truBlog.services;

import org.springframework.stereotype.Service;
import org.truBlog.data.models.Comment;
import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.dataTransferObjects.requests.CommentOnPostRequest;

@Service
public class CommentServiceImplementation implements CommentService {
    @Override
    public Comment commentOnPost(CommentOnPostRequest commentOnPostRequest, User user, Post post) {
        return null;
    }
}
