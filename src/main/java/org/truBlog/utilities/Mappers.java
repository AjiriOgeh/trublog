package org.truBlog.utilities;

import org.truBlog.data.models.Comment;
import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.data.models.View;
import org.truBlog.dataTransferObjects.requests.*;
import org.truBlog.dataTransferObjects.responses.*;
import org.truBlog.exceptions.PostNotFoundException;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Mappers {

    public static User registerRequestMap(RegisterRequest registerRequest) {
        User newUser = new User();
        newUser.setFirstName(registerRequest.getFirstName());
        newUser.setLastName(registerRequest.getLastName());
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(registerRequest.getPassword());
        return newUser;
    }
    public static RegisterResponse registerResponseMap(User user) {
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setId(user.getId());
        registerResponse.setUsername(user.getUsername());
        registerResponse.setDateOfRegistration(user.getDateOfRegistration().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh: mm: ss a")));
        return registerResponse;
    }

    public static LoginResponse loginResponseMap(User user) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(user.getId());
        loginResponse.setUsername(user.getUsername());
        return loginResponse;
    }

    public static LogoutResponse logoutResponseMap(User user) {
        LogoutResponse logoutResponse = new LogoutResponse();
        logoutResponse.setId(user.getId());
        logoutResponse.setUsername(user.getUsername());
        return logoutResponse;
    }
    public static Post createPostRequestMap(CreatePostRequest createPostRequest) {
        Post newPost = new Post();
        newPost.setTitle(createPostRequest.getTitle());
        newPost.setContent(createPostRequest.getContent());
        return newPost;
    }
    public static CreatePostResponse createPostResponseMap(Post post){
        CreatePostResponse createPostResponse = new CreatePostResponse();
        createPostResponse.setId(post.getId());
        createPostResponse.setTitle(post.getTitle());
        createPostResponse.setDateCreated(post.getDateCreated().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh: mm: ss a")));
        return createPostResponse;
    }

    public static Post editPostRequestMap(EditPostRequest editPostRequest, User user){
        Post post = findUserPost(editPostRequest.getPostId(), user);
        if (editPostRequest.getEditedTitle() != null) post.setTitle(editPostRequest.getEditedTitle());
        if (editPostRequest.getEditedContent() != null) post.setContent(editPostRequest.getEditedContent());
        return post;
    }

    private static Post findUserPost(String id, User user) {
        for (int count = 0; count < user.getPosts().size(); count++){
            if (user.getPosts().get(count).getId().equals(id)) {
                return user.getPosts().get(count);
            }
        }
        throw new  PostNotFoundException(String.format("Post %s does not exist in your collection. Please Create a Post", id));
    }

    public static EditPostResponse editPostResponseMap(Post post){
        EditPostResponse editPostResponse = new EditPostResponse();
        editPostResponse.setId(post.getId());
        editPostResponse.setTitle(post.getTitle());
        editPostResponse.setDateEdited(post.getDateCreated().format(DateTimeFormatter.ofPattern("MMM dd, yyy hh: mm: ss a")));
        return editPostResponse;
    }

    public static View createViewPost(User user) {
        View view = new View();
        view.setViewer(user.getUsername());
        return view;
    }

    public static ViewPostResponse viewPostResponseMap(View view){
        ViewPostResponse viewPostResponse = new ViewPostResponse();
        viewPostResponse.setViewId(view.getId());
        viewPostResponse.setViewer(view.getViewer());
        viewPostResponse.setTimeOfView(view.getTimeOfView().format(DateTimeFormatter.ofPattern("MMM dd, yyy hh: mm: ss a")));
        return viewPostResponse;
    }

    public static DeletePostResponse deletePostResponseMap(Post post) {
        DeletePostResponse deletePostResponse = new DeletePostResponse();
        deletePostResponse.setId(post.getId());
        deletePostResponse.setTitle(post.getTitle());
        return deletePostResponse;
    }

    public static Comment commentOnPostRequestMap(CommentInPostRequest commentInPostRequest, User user) {
        Comment comment = new Comment();
        comment.setComment(commentInPostRequest.getComment());
        comment.setCommenter(user);
        return comment;
    }

    public static CommentInPostResponse commentOnPostResponseMap(Comment comment) {
        CommentInPostResponse commentInPostResponse = new CommentInPostResponse();
        commentInPostResponse.setComment(comment.getComment());
        commentInPostResponse.setCommentId(comment.getId());
        commentInPostResponse.setCommenterUsername(comment.getCommenter().getUsername());
        commentInPostResponse.setTimeOfComment(comment.getTimeOfComment().format(DateTimeFormatter.ofPattern("MMM dd, yyy hh: mm: ss a")));
        return commentInPostResponse;
    }

    public static DeleteCommentInPostResponse deleteCommentInPostResponseMap(Comment comment, Post post) {
        DeleteCommentInPostResponse deleteCommentInPostResponse = new DeleteCommentInPostResponse();
        deleteCommentInPostResponse.setComment(comment.getComment());
        deleteCommentInPostResponse.setCommentId(comment.getId());
        deleteCommentInPostResponse.setPostId(post.getId());
        return deleteCommentInPostResponse;
    }
}
