package org.truBlog.services;

import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.dataTransferObjects.requests.*;
import org.truBlog.dataTransferObjects.responses.CommentInPostResponse;
import org.truBlog.dataTransferObjects.responses.DeleteCommentInPostResponse;
import org.truBlog.dataTransferObjects.responses.DeletePostResponse;
import org.truBlog.dataTransferObjects.responses.ViewPostResponse;

import java.util.Optional;

public interface PostService {
    Post createPost(CreatePostRequest createPostRequest);

    Post editPost(EditPostRequest editPostRequest, User user);

    DeletePostResponse deletePost(DeletePostRequest deletePostRequest, User user);

    ViewPostResponse viewPost(ViewPostRequest viewPostRequest, User user);

    CommentInPostResponse commentInPost(CommentInPostRequest commentInPostRequest, User user);

    DeleteCommentInPostResponse deleteCommentInPost(DeleteCommentInPostRequest deleteCommentInPostRequest, User user);
}
