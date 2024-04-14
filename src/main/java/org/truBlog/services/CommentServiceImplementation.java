package org.truBlog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truBlog.data.models.Comment;
import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.data.repositories.CommentRepository;
import org.truBlog.dataTransferObjects.requests.CommentInPostRequest;
import org.truBlog.dataTransferObjects.requests.DeleteCommentInPostRequest;
import org.truBlog.dataTransferObjects.requests.DeletePostRequest;
import org.truBlog.dataTransferObjects.responses.DeleteCommentInPostResponse;
import org.truBlog.dataTransferObjects.responses.DeletePostResponse;
import org.truBlog.exceptions.CommentNotFoundException;

import static org.truBlog.utilities.Mappers.*;

@Service
public class CommentServiceImplementation implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment commentOnPost(CommentInPostRequest commentInPostRequest, User user) {
        Comment comment = commentOnPostRequestMap(commentInPostRequest, user);
        return commentRepository.save(comment);
    }

    @Override
    public DeleteCommentInPostResponse deleteCommentInPost(DeleteCommentInPostRequest deleteCommentInPostRequest, Post post) {
        Comment comment = findCommentById(deleteCommentInPostRequest.getCommentId(), post);
        if (!deleteCommentInPostRequest.getUsername().equals(comment.getCommenter().getUsername())) throw new IllegalArgumentException(String.format("User %s did not make comment", deleteCommentInPostRequest.getUsername()));
        DeleteCommentInPostResponse deleteCommentInPostResponse = deleteCommentInPostResponseMap(comment, post);
        commentRepository.delete(comment);
        return deleteCommentInPostResponse;
    }

    private Comment findCommentById(String id, Post post) {
        for(int count = 0; count < post.getComments().size(); count++) {
            if (post.getComments().get(count).getId().equals(id)) {
                return post.getComments().get(count);
            }
        }
        throw new CommentNotFoundException("Comment does not exist");
    }


}
