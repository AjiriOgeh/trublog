package org.truBlog.services;

import org.truBlog.data.models.Comment;
import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.dataTransferObjects.requests.CommentOnPostRequest;

public interface CommentService {
    Comment commentOnPost(CommentOnPostRequest commentOnPostRequest, User user, Post post);
}
