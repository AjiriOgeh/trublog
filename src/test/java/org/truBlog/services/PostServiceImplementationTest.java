package org.truBlog.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.truBlog.data.models.Post;
import org.truBlog.data.repositories.PostRepository;
import org.truBlog.dataTransferObjects.requests.CreatePostRequest;
import org.truBlog.dataTransferObjects.requests.EditPostRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PostServiceImplementationTest {

    @Autowired
    PostService postService;

    @Autowired
    private PostRepository postRepository;

    private CreatePostRequest createPostRequest;

    private EditPostRequest editPostRequest;

    @BeforeEach
    public void setUp() {
        postRepository.deleteAll();
        createPostRequest = new CreatePostRequest();
        editPostRequest = new EditPostRequest();
    }

    @Test
    public void createPostTest() {
        assertEquals(0, postRepository.count());

        createPostRequest.setTitle("Inception");
        createPostRequest.setContent("Dreams within dreams.");

        Post post = postService.createPost(createPostRequest);
        assertEquals(1, postRepository.count());
        assertEquals("Inception", post.getTitle());
    }

//    @Test
//    public void userCanCreateAPost_UserEditsAPostTest() {
//        assertEquals(0, postRepository.count());
//
//        createPostRequest.setUsername("john123");
//        createPostRequest.setTitle("Inception");
//        createPostRequest.setContent("Dreams within dreams.");
//
//        postService.createPost(createPostRequest);
//        String id =  postService.createdPostId();
//        assertEquals(1, postRepository.count());
//
//        editPostRequest.setPostId(id);
//        editPostRequest.setUsername("john123");
//        editPostRequest.setEditedTitle("Inception Concept");
//        editPostRequest.setEditedContent("Planting an Idea");
//
//        postService.editPost(editPostRequest);
//        assertEquals("Inception Concept", postRepository.findById(id).get().getTitle());
//    }
}