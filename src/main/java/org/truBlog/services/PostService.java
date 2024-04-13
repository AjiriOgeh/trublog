package org.truBlog.services;

import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.dataTransferObjects.requests.*;
import org.truBlog.dataTransferObjects.responses.CommentOnPostResponse;
import org.truBlog.dataTransferObjects.responses.ViewPostResponse;

import java.util.Optional;

public interface PostService {
    Post createPost(CreatePostRequest createPostRequest);
    String createdPostId();
    Post editPost(EditPostRequest editPostRequest, User user);
    void deletePost(DeletePostRequest deletePostRequest, User user);
    ViewPostResponse viewPost(ViewPostRequest viewPostRequest, Optional<User> user);

    CommentOnPostResponse commentInPost(CommentOnPostRequest commentOnPostRequest, Optional<User> user);
}
