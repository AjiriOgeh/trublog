package org.truBlog.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.truBlog.data.models.User;
import org.truBlog.data.repositories.PostRepository;
import org.truBlog.data.repositories.UserRepository;
import org.truBlog.data.repositories.ViewRepository;
import org.truBlog.dataTransferObjects.requests.*;
import org.truBlog.dataTransferObjects.responses.*;
import org.truBlog.exceptions.ProfileLockStateException;
import org.truBlog.exceptions.InvalidPasswordException;
import org.truBlog.exceptions.PostNotFoundException;
import org.truBlog.exceptions.UserNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceImplementationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ViewRepository viewRepository;
    private RegisterRequest registerRequest;
    private LogoutRequest logoutRequest;
    private LoginRequest loginRequest;
    private CreatePostRequest createPostRequest;
    private EditPostRequest editPostRequest;
    private DeletePostRequest deletePostRequest;
    private ViewPostRequest viewPostRequest;

    private CommentOnPostRequest commentOnPostRequest;


    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        postRepository.deleteAll();
        viewRepository.deleteAll();
        registerRequest = new RegisterRequest();
        logoutRequest = new LogoutRequest();
        loginRequest = new LoginRequest();
        createPostRequest = new CreatePostRequest();
        editPostRequest = new EditPostRequest();
        deletePostRequest = new DeletePostRequest();
        viewPostRequest = new ViewPostRequest();
        commentOnPostRequest = new CommentOnPostRequest();
    }

    @Test
    public void userCanSignUpTest() {
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        assertEquals(1, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());
    }

    @Test
    public void twoUsersSignUpTest(){
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        registerRequest.setFirstName("Jack");
        registerRequest.setLastName("Smith");
        registerRequest.setUsername("jack456");
        registerRequest.setPassword("password");

        RegisterResponse jackRegisterResponse = userService.signUp(registerRequest);

        assertEquals(2, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());
        assertEquals("jack456", jackRegisterResponse.getUsername());
    }

    @Test
    public void userSignsUp_FirstNameFieldIsEmpty_ThrowsExceptionTest(){
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        assertThrows(IllegalArgumentException.class, ()->userService.signUp(registerRequest));
    }

    @Test
    public void userSignsUp_LastNameFieldIsEmpty_ThrowsExceptionTest(){
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        assertThrows(IllegalArgumentException.class, ()->userService.signUp(registerRequest));
    }

    @Test
    public void userSignsUp_UsernameFieldIsEmpty_ThrowsExceptionTest(){
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("");
        registerRequest.setPassword("password");

        assertThrows(IllegalArgumentException.class, ()->userService.signUp(registerRequest));
    }

    @Test
    public void userSignsUp_UsernameContainsSpaceCharacter_ThrowsExceptionTest(){
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john 123");
        registerRequest.setPassword("password");

        assertThrows(IllegalArgumentException.class, ()->userService.signUp(registerRequest));
    }

    @Test
    public void userSignsUp_UserCanLogoutTest() {
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        assertEquals(1, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());

        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());
    }

    @Test
    public void userLogsOut_UserCanLoginTest() {
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        assertEquals(1, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());

        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());

        loginRequest.setUsername("john123");
        loginRequest.setPassword("password");
        LoginResponse johnLoginResponse = userService.login(loginRequest);

        assertFalse(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLoginResponse.getUsername());
    }

    @Test
    public void userLogsOut_NonExistentUserLogsIn_ThrowsExceptionTest() {
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        assertEquals(1, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());

        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());

        loginRequest.setUsername("jack456");
        loginRequest.setPassword("password");
        assertThrows(UserNotFoundException.class, ()->userService.login(loginRequest));
    }

    @Test
    public void userLogsOut_UserLogsIn_WithIncorrectPassword_ThrowExceptionTest() {
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        assertEquals(1, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());

        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());

        loginRequest.setUsername("john123");
        loginRequest.setPassword("incorrect password");
        assertThrows(InvalidPasswordException.class, ()->userService.login(loginRequest));
    }

    @Test
    public void userCanCreateAPostTest() {
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        assertEquals(1, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());

        createPostRequest.setUsername("john123");
        createPostRequest.setTitle("Inception");
        createPostRequest.setContent("Dreams within dreams.");

        CreatePostResponse createPostResponse = userService.createPost(createPostRequest);
        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals("Inception", createPostResponse.getTitle());
        assertEquals("Inception", john123.get().getPosts().getFirst().getTitle());
        assertEquals(1, userRepository.findAll().getFirst().getPosts().size());
        assertEquals(1, postRepository.count());
    }

    @Test
    public void nonExistentUserCreatesPost_ThrowsExceptionTestTest() {
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        assertEquals(1, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());

        createPostRequest.setUsername("jack456");
        createPostRequest.setTitle("Inception");
        createPostRequest.setContent("Dreams within dreams.");

        assertThrows(UserNotFoundException.class, ()->userService.createPost(createPostRequest));
    }

    @Test
    public void userCreatesPost_WhenLoggedOut_ThrowsExceptionTest() {
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        assertEquals(1, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());

        logoutRequest.setUsername("john123");
        userService.logout(logoutRequest);

        createPostRequest.setUsername("john123");
        createPostRequest.setTitle("Inception");
        createPostRequest.setContent("Dreams within dreams.");

        assertThrows(ProfileLockStateException.class, ()->userService.createPost(createPostRequest));
    }

    @Test
    public void userCreatesAPost_UserEditsPostTest() {
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        assertEquals(1, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());

        createPostRequest.setUsername("john123");
        createPostRequest.setTitle("Inception");
        createPostRequest.setContent("Dreams within dreams.");

        CreatePostResponse createPostResponse = userService.createPost(createPostRequest);
        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals(1, postRepository.count());
        assertEquals("Inception", createPostResponse.getTitle());
        assertEquals(1, john123.get().getPosts().size());
        assertEquals(1, userRepository.findAll().getFirst().getPosts().size());

        String id =  userService.createdPostId();
        editPostRequest.setPostId(id);
        editPostRequest.setUsername("john123");
        editPostRequest.setEditedTitle("Inception Concept");
        editPostRequest.setEditedContent("Planting an Idea");

        EditPostResponse editPostResponse = userService.editPost(editPostRequest);

        john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals("Inception Concept", john123.get().getPosts().getFirst().getTitle());
        assertEquals(1, john123.get().getPosts().size());
        assertEquals("Planting an Idea", postRepository.findById(id).get().getContent());
        assertEquals("Inception Concept", editPostResponse.getTitle());
    }

    @Test
    public void userCreatesAPost_LogsOut_UserEditsPost_ThrowsExceptionTest() {
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        assertEquals(1, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());

        createPostRequest.setUsername("john123");
        createPostRequest.setTitle("Inception");
        createPostRequest.setContent("Dreams within dreams.");

        CreatePostResponse createPostResponse = userService.createPost(createPostRequest);
        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals(1, postRepository.count());
        assertEquals("Inception", createPostResponse.getTitle());
        assertEquals(1, john123.get().getPosts().size());
        assertEquals(1, userRepository.findAll().getFirst().getPosts().size());

        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());

        String id =  userService.createdPostId();
        editPostRequest.setPostId(id);
        editPostRequest.setUsername("john123");
        editPostRequest.setEditedTitle("Inception Concept");
        editPostRequest.setEditedContent("Planting an Idea");

        assertThrows(ProfileLockStateException.class, ()->userService.editPost(editPostRequest));
    }

    @Test
    public void userCreatesPost_DifferentUserEditsPost_ThrowsExceptionTest() {
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        registerRequest.setFirstName("Jack");
        registerRequest.setLastName("Smith");
        registerRequest.setUsername("jack456");
        registerRequest.setPassword("password");

        RegisterResponse jackRegisterResponse = userService.signUp(registerRequest);

        assertEquals(2, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());
        assertEquals("jack456", jackRegisterResponse.getUsername());

        createPostRequest.setUsername("john123");
        createPostRequest.setTitle("Inception");
        createPostRequest.setContent("Dreams within dreams.");

        userService.createPost(createPostRequest);
        String id =  userService.createdPostId();
        assertEquals(1, postRepository.count());

        editPostRequest.setPostId(id);
        editPostRequest.setUsername("jack456");
        editPostRequest.setEditedTitle("Inception Concept");
        editPostRequest.setEditedContent("Planting an Idea");

        assertThrows(PostNotFoundException.class, ()->userService.editPost(editPostRequest));
    }

    @Test
    public void userCreatesPost_UserEditsNonExistentPost_ThrowsExceptionTest(){
        assertEquals(0, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        userService.signUp(registerRequest);

        assertEquals(1, userRepository.count());

        createPostRequest.setUsername("john123");
        createPostRequest.setTitle("Inception");
        createPostRequest.setContent("Dreams within dreams.");

        CreatePostResponse createPostResponse = userService.createPost(createPostRequest);
        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals(1, postRepository.count());
        assertEquals("Inception", createPostResponse.getTitle());
        assertEquals(1, john123.get().getPosts().size());
        assertEquals(1, userRepository.findAll().getFirst().getPosts().size());

        editPostRequest.setPostId("non existent post Id");
        editPostRequest.setUsername("john123");
        editPostRequest.setEditedTitle("Inception Concept");
        editPostRequest.setEditedContent("Planting an Idea");

        assertThrows(PostNotFoundException.class, ()->userService.editPost(editPostRequest));
    }
//////////////////////////////////////////////////////////////////////////////////////
//    @Test
//    public void userCreatesAPost_UserDeletesPostTest() {
//        assertEquals(0, userRepository.count());
//
//        registerRequest.setFirstName("John");
//        registerRequest.setLastName("Doe");
//        registerRequest.setUsername("john123");
//        registerRequest.setPassword("password");
//
//        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);
//
//        assertEquals(1, userRepository.count());
//        assertEquals("john123", johnRegisterResponse.getUsername());
//
//        createPostRequest.setUsername("john123");
//        createPostRequest.setTitle("Inception");
//        createPostRequest.setContent("Dreams within dreams.");
//
//        CreatePostResponse createPostResponse = userService.createPost(createPostRequest);
//        Optional<User> john123 = userRepository.findByUsername("john123");
//        assertTrue(john123.isPresent());
//
//        assertEquals("Inception", createPostResponse.getTitle());
//        assertEquals("Inception", john123.get().getPosts().get(0).getTitle());
//        assertEquals(1, userRepository.findAll().get(0).getPosts().size());
//        assertEquals(1, postRepository.count());
//
//        String id =  userService.createdPostId();
//        deletePostRequest.setUsername("john123");
//        deletePostRequest.setPostId(id);
//        System.out.println(john123.get().getPosts());
//
//        DeletePostResponse deletePostResponse = userService.deletePost(deletePostRequest);
//
//        john123 = userRepository.findByUsername("john123");
//        assertTrue(john123.isPresent());
//
//        assertEquals(0, postRepository.count());
//
//        System.out.println(john123.get().getPosts());
//
//        john123 = userRepository.findByUsername("john123");
//        System.out.println(john123.get().getPosts());
//        assertEquals(0, userRepository.findAll().get(0).getPosts().size());
//        assertEquals(0, john123.get().getPosts().size());
//
//    }
///////////////////////////////////////////////////////////////////////////////////////
    // need to run more edit tests

    @Test
    public void userCreatesPost_PostCanBeViewedTest(){
        userService.createAnonymousUser();
        assertEquals(1, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        assertEquals(2, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());

        createPostRequest.setUsername("john123");
        createPostRequest.setTitle("Inception");
        createPostRequest.setContent("Dreams within dreams.");

        CreatePostResponse createPostResponse = userService.createPost(createPostRequest);
        String id =  userService.createdPostId();
        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals("Inception", createPostResponse.getTitle());
        assertEquals("Inception", john123.get().getPosts().getFirst().getTitle());
        assertEquals(1, userRepository.findAll().get(1).getPosts().size());
        assertEquals(1, postRepository.count());

        viewPostRequest.setId(id);
        viewPostRequest.setUsername("john123");

        ViewPostResponse viewPostResponse = userService.viewPost(viewPostRequest);

        assertEquals(1, viewRepository.count());
        assertEquals(1, john123.get().getPosts().size());
        assertEquals("john123", viewPostResponse.getViewer());
    }

    @Test
    public void userCreatesPost_UnregisteredUserViewsPostTest(){
        userService.createAnonymousUser();
        assertEquals(1, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        assertEquals(2, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());

        createPostRequest.setUsername("john123");
        createPostRequest.setTitle("Inception");
        createPostRequest.setContent("Dreams within dreams.");

        CreatePostResponse createPostResponse = userService.createPost(createPostRequest);
        String id =  userService.createdPostId();
        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals("Inception", createPostResponse.getTitle());
        assertEquals("Inception", john123.get().getPosts().getFirst().getTitle());
        assertEquals(1, userRepository.findAll().get(1).getPosts().size());
        assertEquals(1, postRepository.count());

        viewPostRequest.setId(id);
        viewPostRequest.setUsername(null);

        ViewPostResponse viewPostResponse = userService.viewPost(viewPostRequest);

        assertEquals(1, viewRepository.count());
        assertEquals(1, john123.get().getPosts().size());
        assertEquals("anonymous", viewPostResponse.getViewer());
    }

    @Test
    public void userCreatesPost_NonExistentPostIsViewed_ThrowsExceptionTest(){
        userService.createAnonymousUser();
        assertEquals(1, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        assertEquals(2, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());

        createPostRequest.setUsername("john123");
        createPostRequest.setTitle("Inception");
        createPostRequest.setContent("Dreams within dreams.");

        CreatePostResponse createPostResponse = userService.createPost(createPostRequest);
        String id =  userService.createdPostId();
        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals("Inception", createPostResponse.getTitle());
        assertEquals("Inception", john123.get().getPosts().getFirst().getTitle());
        assertEquals(1, userRepository.findAll().get(1).getPosts().size());
        assertEquals(1, postRepository.count());

        viewPostRequest.setId("non existent post Id");
        viewPostRequest.setUsername(null);

        assertThrows(PostNotFoundException.class, ()->userService.viewPost(viewPostRequest));
    }

    @Test
    public void userCreatesPost_RegisteredUserViewsPost_WhileLoggedOutTest(){
        userService.createAnonymousUser();
        assertEquals(1, userRepository.count());

        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");

        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);

        registerRequest.setFirstName("Jack");
        registerRequest.setLastName("Smith");
        registerRequest.setUsername("jack456");
        registerRequest.setPassword("password");

        RegisterResponse jackRegisterResponse = userService.signUp(registerRequest);

        assertEquals(3, userRepository.count());
        assertEquals("john123", johnRegisterResponse.getUsername());
        assertEquals("jack456", jackRegisterResponse.getUsername());

        createPostRequest.setUsername("john123");
        createPostRequest.setTitle("Inception");
        createPostRequest.setContent("Dreams within dreams.");

        CreatePostResponse createPostResponse = userService.createPost(createPostRequest);
        String id =  userService.createdPostId();
        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals("Inception", createPostResponse.getTitle());
        assertEquals("Inception", john123.get().getPosts().getFirst().getTitle());
        assertEquals(1, userRepository.findAll().get(1).getPosts().size());
        assertEquals(1, postRepository.count());

        logoutRequest.setUsername("jack456");
        LogoutResponse jackLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("jack456").get().isLocked());
        assertEquals("jack456", jackLogoutResponse.getUsername());

        viewPostRequest.setId(id);
        viewPostRequest.setUsername("john456");

        ViewPostResponse viewPostResponse = userService.viewPost(viewPostRequest);

        assertEquals(1, viewRepository.count());
        assertEquals(1, john123.get().getPosts().size());
        assertEquals("anonymous", viewPostResponse.getViewer());
    }
//
//    @Test
//    public void userCreatesPost_PostCanBeViewed_commentsTest(){
//        assertEquals(0, userRepository.count());
//
//        registerRequest.setFirstName("John");
//        registerRequest.setLastName("Doe");
//        registerRequest.setUsername("john123");
//        registerRequest.setPassword("password");
//
//        RegisterResponse johnRegisterResponse = userService.signUp(registerRequest);
//
//        assertEquals(1, userRepository.count());
//        assertEquals("john123", johnRegisterResponse.getUsername());
//
//        createPostRequest.setUsername("john123");
//        createPostRequest.setTitle("Inception");
//        createPostRequest.setContent("Dreams within dreams.");
//
//        CreatePostResponse createPostResponse = userService.createPost(createPostRequest);
//        String id =  userService.createdPostId();
//        Optional<User> john123 = userRepository.findByUsername("john123");
//        assertTrue(john123.isPresent());
//
//        assertEquals("Inception", createPostResponse.getTitle());
//        assertEquals("Inception", john123.get().getPosts().get(0).getTitle());
//        assertEquals(1, userRepository.findAll().get(0).getPosts().size());
//        assertEquals(1, postRepository.count());
//
//        viewPostRequest.setId(id);
//        viewPostRequest.setUsername("john123");
//
//        ViewPostResponse viewPostResponse = userService.viewPost(viewPostRequest);
//
//        assertEquals(1, viewRepository.count());
//        assertEquals(1, john123.get().getPosts().size());
//        assertEquals("john123", viewPostResponse.getViewer());
//
//        commentOnPostRequest.setPostId(id);
//        commentOnPostRequest.setComment("This is interesting");
//
//        CommentOnPostResponse commentOnPostResponse = new CommentOnPostResponse();
//
//    }


}