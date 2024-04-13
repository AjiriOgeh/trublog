package org.truBlog.utilities;

import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.data.models.View;
import org.truBlog.dataTransferObjects.requests.CreatePostRequest;
import org.truBlog.dataTransferObjects.requests.EditPostRequest;
import org.truBlog.dataTransferObjects.requests.RegisterRequest;
import org.truBlog.dataTransferObjects.requests.ViewPostRequest;
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
        post.setTitle(editPostRequest.getEditedTitle());
        post.setContent(editPostRequest.getEditedContent());
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

    public static View createViewPost(Optional<User> user) {
        View view = new View();
        view.setViewer(user.get().getUsername());
        return view;
    }

    public static ViewPostResponse viewPostResponseMap(View view){
        ViewPostResponse viewPostResponse = new ViewPostResponse();
        viewPostResponse.setViewId(view.getId());
        viewPostResponse.setViewer(view.getViewer());
        viewPostResponse.setTimeOfView(view.getTimeOfView().format(DateTimeFormatter.ofPattern("MMM dd, yyy hh: mm: ss a")));
        return viewPostResponse;
    }
}
