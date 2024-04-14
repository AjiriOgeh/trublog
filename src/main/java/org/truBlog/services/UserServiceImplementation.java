package org.truBlog.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.data.repositories.UserRepository;
import org.truBlog.dataTransferObjects.requests.*;
import org.truBlog.dataTransferObjects.responses.*;
import org.truBlog.exceptions.PostNotFoundException;
import org.truBlog.exceptions.ProfileLockStateException;
import org.truBlog.exceptions.InvalidPasswordException;
import org.truBlog.exceptions.UserNotFoundException;

import java.util.Optional;

import static org.truBlog.utilities.Mappers.*;


@Service
public class UserServiceImplementation implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;
    @Override
    public RegisterResponse signUp(RegisterRequest registerRequest) {
        validateInputs(registerRequest);
        User newUser = registerRequestMap(registerRequest);
        userRepository.save(newUser);
        return registerResponseMap(newUser);
    }

    private void validateInputs(RegisterRequest registerRequest) {
        if (registerRequest.getFirstName().isEmpty()) throw new IllegalArgumentException("First name field cannot be empty. Please Enter a valid first name.");
        if (registerRequest.getLastName().isEmpty()) throw new IllegalArgumentException("Last name field cannot be empty. Please Enter a valid last name.");
        if (registerRequest.getUsername().isEmpty()) throw new IllegalArgumentException("Username field cannot be empty. Please enter a valid username");
        if (registerRequest.getUsername().contains(" ")) throw new IllegalArgumentException("Username cannot contain space character. Please enter a valid username");
        if (doesUsernameExist(registerRequest.getUsername().toLowerCase())) throw new IllegalArgumentException("Username Exists. Please enter a different username");
        if (registerRequest.getUsername().equalsIgnoreCase("anonymous")) throw new IllegalArgumentException("Username cannot be anonymous. Please enter a different username");
    }


    private boolean doesUsernameExist(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent();
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = findUserByUsername(loginRequest.getUsername());
        if (!user.getPassword().equals(loginRequest.getPassword())) throw new InvalidPasswordException("Invalid Login Details");
        user.setLocked(false);
        userRepository.save(user);
        return loginResponseMap(user);
    }

    @Override
    public LogoutResponse logout(LogoutRequest logoutRequest) {
        User user = findUserByUsername(logoutRequest.getUsername());
        user.setLocked(true);
        userRepository.save(user);
        return logoutResponseMap(user);
    }


    private User findUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) throw new UserNotFoundException(String.format("%s does not exist", username));
        return user.get();
    }


    @Override
    public CreatePostResponse createPost(CreatePostRequest createPostRequest) {
        User user = findUserByUsername(createPostRequest.getUsername());
        if (user.isLocked()) throw new ProfileLockStateException("Please login to create a post");
        Post newPost = postService.createPost(createPostRequest);
        user.getPosts().add(newPost);
        userRepository.save(user);
        return createPostResponseMap(newPost);
    }


    @Override
    public EditPostResponse editPost(EditPostRequest editPostRequest) {
        User user = findUserByUsername(editPostRequest.getUsername());
        if (user.isLocked()) throw new ProfileLockStateException("Please login to edit post");
        Post post = postService.editPost(editPostRequest, user);
        return editPostResponseMap(post);
    }

    @Override
    public DeletePostResponse deletePost(DeletePostRequest deletePostRequest) {
        User user = findUserByUsername((deletePostRequest.getUsername()));
        if (user.isLocked()) throw new ProfileLockStateException("Please login to delete post");
        Post post = findPost(deletePostRequest.getPostId(), user);
        DeletePostResponse deletePostResponse = postService.deletePost(deletePostRequest, user);
        user.getPosts().remove(post);
        userRepository.save(user);
        return deletePostResponse;
    }

    private Post findPost(String postId, User user) {
        for (Post post : user.getPosts()) {
            if (post.getId().equals(postId)) return post;
        }
        throw new PostNotFoundException(String.format("Post %s does not exist in your collection. Please Create a Post", postId));
    }

    @Override
    public ViewPostResponse viewPost(ViewPostRequest viewPostRequest) {
//        createAnonymousUser();
//        User anonymous = findUserByUsername("anonymous");
//        if (viewPostRequest.getUsername() == null) return postService.viewPost(viewPostRequest, anonymous);

        Optional<User> user = userRepository.findByUsername(viewPostRequest.getUsername());
        //if (user.get().isLocked()) return postService.viewPost(viewPostRequest, anonymous);
        return postService.viewPost(viewPostRequest, user.get());
    }

    @PostConstruct
    private void createAnonymousUser() {
        User anonymous = new User();
        anonymous.setUsername("anonymous");
        anonymous.setLocked(true);
        userRepository.save(anonymous);
        //System.out.println(userRepository.findByUsername("anonymous"));
    }

    @Override
    public CommentInPostResponse commentInPost(CommentInPostRequest commentInPostRequest){
        User user = findUserByUsername((commentInPostRequest.getUsername()));
        if (user.isLocked()) throw new ProfileLockStateException("Please login to comment in the post");
        return postService.commentInPost(commentInPostRequest, user);
    }

    @Override
    public DeleteCommentInPostResponse deleteCommentInPost(DeleteCommentInPostRequest deleteCommentInPostRequest) {
        User user = findUserByUsername((deleteCommentInPostRequest.getUsername()));
        if (user.isLocked()) throw new ProfileLockStateException("Please login to delete comment");
        //Post post = findPost(deleteCommentInPostRequest.getPostId(), user);
        DeleteCommentInPostResponse deleteCommentInPostResponse = postService.deleteCommentInPost(deleteCommentInPostRequest, user);
        userRepository.save(user);
        return deleteCommentInPostResponse;
    }

}
