package org.truBlog.services;

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

//    private boolean validateUsername(String username) {
//        Optional<User> user = userRepository.findByUsername(username);
//        return user.isPresent();
//    }


    private User findUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) throw new UserNotFoundException("Invalid Login Details. Please Try again");
        return user.get();
    }

    private User findUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) throw new UserNotFoundException(String.format("%s not Found. Please create an account.", id));
        return user.get();
    }

    private static void validateInputs(RegisterRequest registerRequest) {
        if (registerRequest.getFirstName().isEmpty()) throw new IllegalArgumentException("First name field cannot be empty. Please Enter a valid first name.");
        if (registerRequest.getLastName().isEmpty()) throw new IllegalArgumentException("Last name field cannot be empty. Please Enter a valid last name.");
        if (registerRequest.getUsername().isEmpty()) throw new IllegalArgumentException("Username field cannot be empty. Please enter a valid username");
        if (registerRequest.getUsername().contains(" ")) throw new IllegalArgumentException("Username cannot contain space character. Please enter a valid username");
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
    public String registeredUserId() {
        int index = userRepository.findAll().size() - 1;
        return userRepository.findAll().get(index).getId();
    }

    @Override
    public String createdPostId() {
        return postService.createdPostId();
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
        user.getPosts().remove(post);
        userRepository.save(user);
        postService.deletePost(deletePostRequest, user);
        return null;
    } // fix the null in delete. ensure that it is a delete response

    private Post findPost(String postId, User user) {
        for (Post post : user.getPosts()) {
            if (post.getId().equals(postId)) return post;
        }
        throw new PostNotFoundException(String.format("Post %s does not exist in your collection. Please Create a Post", postId));
    }

    @Override
    public ViewPostResponse viewPost(ViewPostRequest viewPostRequest) {
        Optional<User> user = userRepository.findByUsername(viewPostRequest.getUsername());
        User anonymous = findUserByUsername("anonymous");
        if (user.isEmpty()) user = Optional.of(anonymous);
        if (user.get().isLocked()) user = Optional.of(anonymous);
        return postService.viewPost(viewPostRequest, user);
    }

    @Override
    public void createAnonymousUser() {
        User anonymous = new User();
        anonymous.setUsername("anonymous");
        anonymous.setLocked(true);
        userRepository.save(anonymous);
    }

    @Override
    public CommentOnPostResponse commentInPost(CommentOnPostRequest commentOnPostRequest){
        Optional<User> user = userRepository.findByUsername(commentOnPostRequest.getUsername());
        User anonymous = findUserByUsername("anonymous");
        if (user.isEmpty()) user = Optional.of(anonymous);
        if (user.get().isLocked()) user = Optional.of(anonymous);
        return postService.commentInPost(commentOnPostRequest, user);
    }

}
