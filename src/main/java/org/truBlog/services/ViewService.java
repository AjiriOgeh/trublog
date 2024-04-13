package org.truBlog.services;

import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.data.models.View;
import org.truBlog.dataTransferObjects.requests.CommentOnPostRequest;
import org.truBlog.dataTransferObjects.requests.ViewPostRequest;

import java.util.Optional;

public interface ViewService {
    View viewPost(ViewPostRequest viewPostRequest, Optional<User> user);

    void commentOnPost(CommentOnPostRequest commentOnPostRequest, Optional<User> user, Post post);
}
