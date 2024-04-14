package org.truBlog.services;

import org.truBlog.data.models.Comment;
import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.dataTransferObjects.requests.CommentInPostRequest;
import org.truBlog.dataTransferObjects.requests.DeleteCommentInPostRequest;
import org.truBlog.dataTransferObjects.responses.DeleteCommentInPostResponse;

public interface CommentService {
    Comment commentOnPost(CommentInPostRequest commentInPostRequest, User user);

    DeleteCommentInPostResponse deleteCommentInPost(DeleteCommentInPostRequest deleteCommentInPostRequest, Post post);
}
