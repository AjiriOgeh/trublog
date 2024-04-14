package org.truBlog.services;

import org.truBlog.data.models.Comment;
import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.data.models.View;
import org.truBlog.dataTransferObjects.requests.CommentInPostRequest;
import org.truBlog.dataTransferObjects.requests.ViewPostRequest;

import java.util.Optional;

public interface ViewService {
    View viewPost(ViewPostRequest viewPostRequest, User user);

    View commentOnPost(CommentInPostRequest commentInPostRequest, User user, Post post);
}
