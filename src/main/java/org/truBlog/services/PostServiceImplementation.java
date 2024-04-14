package org.truBlog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truBlog.data.models.Comment;
import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.data.models.View;
import org.truBlog.data.repositories.PostRepository;
import org.truBlog.dataTransferObjects.requests.*;
import org.truBlog.dataTransferObjects.responses.CommentInPostResponse;
import org.truBlog.dataTransferObjects.responses.DeleteCommentInPostResponse;
import org.truBlog.dataTransferObjects.responses.DeletePostResponse;
import org.truBlog.dataTransferObjects.responses.ViewPostResponse;
import org.truBlog.exceptions.CommentNotFoundException;
import org.truBlog.exceptions.PostNotFoundException;

import java.util.Optional;

import static org.truBlog.utilities.Mappers.*;


@Service
public class PostServiceImplementation implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ViewService viewService;

    @Autowired
    private CommentService commentService;

    @Override
    public Post createPost(CreatePostRequest createPostRequest) {
        Post newPost = createPostRequestMap(createPostRequest);
        postRepository.save(newPost);
        return newPost;
    }

    private Post findPostById(String id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) throw new PostNotFoundException(String.format("Post %s does not Exist", id));
        return post.get();
    }

    @Override
    public Post editPost(EditPostRequest editPostRequest, User user) {
        Post post = editPostRequestMap(editPostRequest, user);
        postRepository.save(post);
        return post;
    }

    @Override
    public DeletePostResponse deletePost(DeletePostRequest deletePostRequest, User user) {
        Post post = findPostById(deletePostRequest.getPostId());
        DeletePostResponse deletePostResponse = deletePostResponseMap(post);
        postRepository.delete(post);
        return deletePostResponse;
    }

    @Override
    public ViewPostResponse viewPost(ViewPostRequest viewPostRequest, User user) {
        Post post = findPostById(viewPostRequest.getId());
        View view = viewService.viewPost(viewPostRequest, user);
        post.getViews().add(view);
        postRepository.save(post);
        return viewPostResponseMap(view);
    }

    @Override
    public CommentInPostResponse commentInPost(CommentInPostRequest commentInPostRequest, User user) {
        Post post = findPostById(commentInPostRequest.getPostId());
        Comment newComment = commentService.commentOnPost(commentInPostRequest,  user);
        View newView = viewService.commentOnPost(commentInPostRequest, user, post);
        post.getViews().add(newView);
        post.getComments().add(newComment);
        postRepository.save(post);
        return commentOnPostResponseMap(newComment);
    }

    @Override
    public DeleteCommentInPostResponse deleteCommentInPost(DeleteCommentInPostRequest deleteCommentInPostRequest, User user) {
        Post post = findPostById(deleteCommentInPostRequest.getPostId());
        DeleteCommentInPostResponse deleteCommentInPostResponse = commentService.deleteCommentInPost(deleteCommentInPostRequest, post);
        Comment comment = findCommentById(deleteCommentInPostRequest.getCommentId(), post);
        post.getComments().remove(comment);
        postRepository.save(post);
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
