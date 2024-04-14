package org.truBlog.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.truBlog.data.models.User;
import org.truBlog.data.repositories.CommentRepository;
import org.truBlog.data.repositories.PostRepository;
import org.truBlog.data.repositories.UserRepository;
import org.truBlog.data.repositories.ViewRepository;
import org.truBlog.dataTransferObjects.requests.*;
import org.truBlog.dataTransferObjects.responses.*;
import org.truBlog.exceptions.*;

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

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        postRepository.deleteAll();
        viewRepository.deleteAll();
        commentRepository.deleteAll();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("john");
        registerRequest.setLastName("doe");
        registerRequest.setUsername("john123");
        registerRequest.setPassword("password");
        userService.signUp(registerRequest);

        CreatePostRequest createPostRequest = new CreatePostRequest();
        createPostRequest.setUsername("john123");
        createPostRequest.setTitle("the prestige");
        createPostRequest.setContent("magician illusion.");
        userService.createPost(createPostRequest);

        String id = postRepository.findAll().getFirst().getId();
        CommentInPostRequest commentInPostRequest = new CommentInPostRequest();
        commentInPostRequest.setPostId(id);
        commentInPostRequest.setUsername("john123");
        commentInPostRequest.setComment("this is amazing");
        userService.commentInPost(commentInPostRequest);
    }

    @Test
    public void userCanSignUpTest() {
        RegisterRequest registerRequest= new RegisterRequest();
        registerRequest.setFirstName("jack");
        registerRequest.setLastName("smith");
        registerRequest.setUsername("jack123");
        registerRequest.setPassword("password");
        RegisterResponse jackRegisterResponse = userService.signUp(registerRequest);

        assertEquals(2, userRepository.count());
        assertEquals("jack123", jackRegisterResponse.getUsername());
    }

    @Test
    public void multipleUsersSignUpTest(){
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("jack");
        registerRequest.setLastName("smith");
        registerRequest.setUsername("jack123");
        registerRequest.setPassword("password");
        RegisterResponse jackRegisterResponse = userService.signUp(registerRequest);

        registerRequest.setFirstName("jim");
        registerRequest.setLastName("brown");
        registerRequest.setUsername("jim456");
        registerRequest.setPassword("password");
        RegisterResponse jimRegisterResponse = userService.signUp(registerRequest);

        assertEquals(3, userRepository.count());
        assertEquals("jack123", jackRegisterResponse.getUsername());
        assertEquals("jim456", jimRegisterResponse.getUsername());
    }

    @Test
    public void userSignsUp_FirstNameFieldIsEmpty_ThrowsExceptionTest(){
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("");
        registerRequest.setLastName("smith");
        registerRequest.setUsername("jack123");
        registerRequest.setPassword("password");

        assertThrows(IllegalArgumentException.class, ()->userService.signUp(registerRequest));
    }

    @Test
    public void userSignsUp_LastNameFieldIsEmpty_ThrowsExceptionTest(){
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("jack");
        registerRequest.setLastName("");
        registerRequest.setUsername("jack123");
        registerRequest.setPassword("password");

        assertThrows(IllegalArgumentException.class, ()->userService.signUp(registerRequest));
    }

    @Test
    public void userSignsUp_UsernameFieldIsEmpty_ThrowsExceptionTest(){
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("jack");
        registerRequest.setLastName("smith");
        registerRequest.setUsername("");
        registerRequest.setPassword("password");

        assertThrows(IllegalArgumentException.class, ()->userService.signUp(registerRequest));
    }

    @Test
    public void userSignsUp_UsernameContainsSpaceCharacter_ThrowsExceptionTest(){
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("jack");
        registerRequest.setLastName("smith");
        registerRequest.setUsername("jim 123");
        registerRequest.setPassword("password");

        assertThrows(IllegalArgumentException.class, ()->userService.signUp(registerRequest));
    }

    @Test
    public void userCanLogsOutTest() {
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());
    }

    @Test
    public void nonExistentUserLogsOutTest() {
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setUsername("jack123");

        assertThrows(UserNotFoundException.class, ()->userService.logout(logoutRequest));
    }

    @Test
    public void userLogsOut_UserLogsInTest() {
        LogoutRequest logoutRequest = new LogoutRequest();

        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("john123");
        loginRequest.setPassword("password");
        LoginResponse johnLoginResponse = userService.login(loginRequest);

        assertFalse(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLoginResponse.getUsername());
    }

    @Test
    public void nonExistentUserLogsIn_ThrowsExceptionTest() {
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("jack123");
        loginRequest.setPassword("password");
        assertThrows(UserNotFoundException.class, ()->userService.login(loginRequest));
    }

    @Test
    public void userLogsIn_WithIncorrectPassword_ThrowsExceptionTest() {;
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("john123");
        loginRequest.setPassword("incorrect password");
        assertThrows(InvalidPasswordException.class, ()->userService.login(loginRequest));
    }

    @Test
    public void userCreatesAPostTest() {
        CreatePostRequest createPostRequest = new CreatePostRequest();
        createPostRequest.setUsername("john123");
        createPostRequest.setTitle("inception");
        createPostRequest.setContent("dreams within dreams.");
        CreatePostResponse createPostResponse = userService.createPost(createPostRequest);

        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals("inception", createPostResponse.getTitle());
        assertEquals("inception", john123.get().getPosts().get(1).getTitle());
        assertEquals(2, userRepository.findAll().getFirst().getPosts().size());
        assertEquals(2, postRepository.count());
    }

    @Test
    public void nonExistentUserCreatesPost_ThrowsExceptionTestTest() {
        CreatePostRequest createPostRequest = new CreatePostRequest();
        createPostRequest.setUsername("jack123");
        createPostRequest.setTitle("inception");
        createPostRequest.setContent("dreams within dreams.");

        assertThrows(UserNotFoundException.class, ()->userService.createPost(createPostRequest));
    }

    @Test
    public void userLogsOut_CreatesPost_ThrowsExceptionTest() {
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());

        CreatePostRequest createPostRequest = new CreatePostRequest();
        createPostRequest.setUsername("john123");
        createPostRequest.setTitle("inception");
        createPostRequest.setContent("dreams within dreams.");

        assertThrows(ProfileLockStateException.class, ()->userService.createPost(createPostRequest));
    }

    @Test
    public void userEditsPostTest() {
        String id = postRepository.findAll().getFirst().getId();

        EditPostRequest editPostRequest = new EditPostRequest();
        editPostRequest.setPostId(id);
        editPostRequest.setUsername("john123");
        editPostRequest.setEditedTitle("source code");
        editPostRequest.setEditedContent("stop a bomb attack.");
        EditPostResponse editPostResponse = userService.editPost(editPostRequest);

        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals(1, john123.get().getPosts().size());
        assertEquals("source code", john123.get().getPosts().getFirst().getTitle());
        assertEquals("stop a bomb attack.", postRepository.findById(id).get().getContent());
        assertEquals("source code", editPostResponse.getTitle());
    }

    @Test
    public void userLogsOut_EditsPost_ThrowsExceptionTest() {
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());

        String id = postRepository.findAll().getFirst().getId();

        EditPostRequest editPostRequest = new EditPostRequest();
        editPostRequest.setPostId(id);
        editPostRequest.setUsername("john123");
        editPostRequest.setEditedTitle("source code");
        editPostRequest.setEditedContent("stop a bomb attack.");

        assertThrows(ProfileLockStateException.class, ()->userService.editPost(editPostRequest));
    }

    @Test
    public void userCreatesPost_DifferentUserEditsPost_ThrowsExceptionTest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("jack");
        registerRequest.setLastName("Smith");
        registerRequest.setUsername("jack123");
        registerRequest.setPassword("password");
        RegisterResponse jackRegisterResponse = userService.signUp(registerRequest);

        assertEquals(2, userRepository.count());
        assertEquals("jack123", jackRegisterResponse.getUsername());

        String id = postRepository.findAll().getFirst().getId();

        EditPostRequest editPostRequest = new EditPostRequest();
        editPostRequest.setPostId(id);
        editPostRequest.setUsername("jack123");
        editPostRequest.setEditedTitle("source code");
        editPostRequest.setEditedContent("stop a bomb attack.");

        assertThrows(PostNotFoundException.class, ()->userService.editPost(editPostRequest));
    }

    @Test
    public void userEditsNonExistentPost_ThrowsExceptionTest(){
        EditPostRequest editPostRequest = new EditPostRequest();
        editPostRequest.setPostId("non existent post Id");
        editPostRequest.setUsername("john123");
        editPostRequest.setEditedTitle("source code");
        editPostRequest.setEditedContent("stop a bomb attack.");

        assertThrows(PostNotFoundException.class, ()->userService.editPost(editPostRequest));
    }

    @Test
    public void userDeletesPostTest() {
        String id = postRepository.findAll().getFirst().getId();

        DeletePostRequest deletePostRequest = new DeletePostRequest();
        deletePostRequest.setUsername("john123");
        deletePostRequest.setPostId(id);
        DeletePostResponse deletePostResponse = userService.deletePost(deletePostRequest);

        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals(0, postRepository.count());
        assertEquals(0, userRepository.findAll().getFirst().getPosts().size());
        assertEquals(0, john123.get().getPosts().size());
        assertEquals("the prestige", deletePostResponse.getTitle());
    }

    @Test
    public void userDeletesNonExistentPostTest() {
        DeletePostRequest deletePostRequest = new DeletePostRequest();
        deletePostRequest.setUsername("john123");
        deletePostRequest.setPostId("non existentPost");

        assertThrows(PostNotFoundException.class, ()->userService.deletePost(deletePostRequest));
    }

    @Test
    public void nonExistentUserDeletesPostTest() {
        String id = postRepository.findAll().getFirst().getId();

        DeletePostRequest deletePostRequest = new DeletePostRequest();
        deletePostRequest.setUsername("jack123");
        deletePostRequest.setPostId(id);

        assertThrows(UserNotFoundException.class, ()->userService.deletePost(deletePostRequest));
    }

    @Test
    public void userLogsOut_DeletesPostTest() {
        String id = postRepository.findAll().getFirst().getId();

        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());

        DeletePostRequest deletePostRequest = new DeletePostRequest();
        deletePostRequest.setUsername("john123");
        deletePostRequest.setPostId(id);

        assertThrows(ProfileLockStateException.class, ()->userService.deletePost(deletePostRequest));
    }

    @Test
    public void userCreatesPost_PostCanBeViewedTest(){
        String id = postRepository.findAll().getFirst().getId();

        ViewPostRequest viewPostRequest = new ViewPostRequest();
        viewPostRequest.setId(id);
        viewPostRequest.setUsername("john123");
        ViewPostResponse viewPostResponse = userService.viewPost(viewPostRequest);

        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals(2, viewRepository.count());
        assertEquals(1, john123.get().getPosts().size());
        assertEquals("john123", viewPostResponse.getViewer());
    }

    @Test
    public void unregisteredUserViewsPostTest(){
        String id = postRepository.findAll().getFirst().getId();

        ViewPostRequest viewPostRequest = new ViewPostRequest();
        viewPostRequest.setId(id);
        viewPostRequest.setUsername(null);
        ViewPostResponse viewPostResponse = userService.viewPost(viewPostRequest);

        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals(2, viewRepository.count());
        assertEquals(1, john123.get().getPosts().size());
        assertEquals("anonymous", viewPostResponse.getViewer());
    }

    @Test
    public void nonExistentPostIsViewed_ThrowsExceptionTest(){
        ViewPostRequest viewPostRequest = new ViewPostRequest();
        viewPostRequest.setId("non existent post Id");
        viewPostRequest.setUsername("john123");

        assertThrows(PostNotFoundException.class, ()->userService.viewPost(viewPostRequest));
    }

//    @Test
//    public void userViewsPost_WhileLoggedOutTest(){
//        String id = postRepository.findAll().getFirst().getId();
//
//        LogoutRequest logoutRequest = new LogoutRequest();
//        logoutRequest.setUsername("john123");
//        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);
//
//        assertTrue(userRepository.findByUsername("john123").get().isLocked());
//        assertEquals("john123", johnLogoutResponse.getUsername());
//
//        ViewPostRequest viewPostRequest = new ViewPostRequest();
//        viewPostRequest.setId(id);
//        viewPostRequest.setUsername("john123");
//        ViewPostResponse viewPostResponse = userService.viewPost(viewPostRequest);
//
//        Optional<User> john123 = userRepository.findByUsername("john123");
//        assertTrue(john123.isPresent());
//
//        assertEquals(2, viewRepository.count());
//        assertEquals(1, john123.get().getPosts().size());
//        assertEquals("anonymous", viewPostResponse.getViewer());
//    }

    @Test
    public void userCommentsInPostTest(){
        String id = postRepository.findAll().getFirst().getId();

        CommentInPostRequest commentInPostRequest = new CommentInPostRequest();
        commentInPostRequest.setPostId(id);
        commentInPostRequest.setUsername("john123");
        commentInPostRequest.setComment("this is interesting");
        CommentInPostResponse commentInPostResponse = userService.commentInPost(commentInPostRequest);

        Optional<User> john123 = userRepository.findByUsername("john123");
        assertTrue(john123.isPresent());

        assertEquals(2, commentRepository.count());
        assertEquals(2, viewRepository.count());
        assertEquals(2, john123.get().getPosts().getFirst().getComments().size());
        assertEquals("this is interesting", john123.get().getPosts().getFirst().getComments().get(1).getComment());
        assertEquals("this is interesting", commentInPostResponse.getComment());
        assertEquals("john123", commentInPostResponse.getCommenterUsername());
    }

    @Test
    public void nonExistentUserCommentsOnPostTest(){
        String id = postRepository.findAll().getFirst().getId();

        CommentInPostRequest commentInPostRequest = new CommentInPostRequest();
        commentInPostRequest.setPostId(id);
        commentInPostRequest.setUsername("jack123");
        commentInPostRequest.setComment("this is interesting");

        assertThrows(UserNotFoundException.class, ()->userService.commentInPost(commentInPostRequest));
    }
    @Test
    public void userCommentsOnNonExistentPostTest(){
        CommentInPostRequest commentInPostRequest = new CommentInPostRequest();
        commentInPostRequest.setPostId("non existent post");
        commentInPostRequest.setUsername("john123");
        commentInPostRequest.setComment("this is interesting");

        assertThrows(PostNotFoundException.class, ()->userService.commentInPost(commentInPostRequest));
    }

    @Test
    public void userLogsOut_CommentsOnPostTest() {
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());

        String id = postRepository.findAll().getFirst().getId();

        CommentInPostRequest commentInPostRequest = new CommentInPostRequest();
        commentInPostRequest.setPostId(id);
        commentInPostRequest.setUsername("john123");
        commentInPostRequest.setComment("this is interesting");

        assertThrows(ProfileLockStateException.class, ()->userService.commentInPost(commentInPostRequest));
    }

    @Test
    public void userDeletesCommentInPostTest() {
        String postId = postRepository.findAll().getFirst().getId();
        String commentId = commentRepository.findAll().getFirst().getId();

        DeleteCommentInPostRequest deleteCommentInPostRequest = new DeleteCommentInPostRequest();
        deleteCommentInPostRequest.setUsername("john123");
        deleteCommentInPostRequest.setPostId(postId);
        deleteCommentInPostRequest.setCommentId(commentId);
        DeleteCommentInPostResponse deleteCommentInPostResponse = userService.deleteCommentInPost(deleteCommentInPostRequest);

        assertEquals(0, commentRepository.count());
        assertEquals("this is amazing", deleteCommentInPostResponse.getComment());
        assertEquals(commentId, deleteCommentInPostResponse.getCommentId());
    }

    @Test
    public void nonExistentUserDeletesCommentTest() {
        String postId = postRepository.findAll().getFirst().getId();
        String commentId = commentRepository.findAll().getFirst().getId();

        DeleteCommentInPostRequest deleteCommentInPostRequest = new DeleteCommentInPostRequest();
        deleteCommentInPostRequest.setUsername("jack123");
        deleteCommentInPostRequest.setPostId(postId);
        deleteCommentInPostRequest.setCommentId(commentId);

        assertThrows(UserNotFoundException.class, ()->userService.deleteCommentInPost(deleteCommentInPostRequest));
    }

    @Test
    public void userDeletesNonExistentCommentTest() {
        String postId = postRepository.findAll().getFirst().getId();

        DeleteCommentInPostRequest deleteCommentInPostRequest = new DeleteCommentInPostRequest();
        deleteCommentInPostRequest.setUsername("john123");
        deleteCommentInPostRequest.setPostId(postId);
        deleteCommentInPostRequest.setCommentId("non existent comment");

        assertThrows(CommentNotFoundException.class, ()->userService.deleteCommentInPost(deleteCommentInPostRequest));
    }

    @Test
    public void userLogsOut_DeletesCommentTest() {
        String id = postRepository.findAll().getFirst().getId();

        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setUsername("john123");
        LogoutResponse johnLogoutResponse = userService.logout(logoutRequest);

        assertTrue(userRepository.findByUsername("john123").get().isLocked());
        assertEquals("john123", johnLogoutResponse.getUsername());

        String postId = postRepository.findAll().getFirst().getId();
        String commentId = commentRepository.findAll().getFirst().getId();

        DeleteCommentInPostRequest deleteCommentInPostRequest = new DeleteCommentInPostRequest();
        deleteCommentInPostRequest.setUsername("john123");
        deleteCommentInPostRequest.setPostId(postId);
        deleteCommentInPostRequest.setCommentId(commentId);

        assertThrows(ProfileLockStateException.class, ()->userService.deleteCommentInPost(deleteCommentInPostRequest));

    }


}