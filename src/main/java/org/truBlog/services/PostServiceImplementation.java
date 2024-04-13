package org.truBlog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.data.models.View;
import org.truBlog.data.repositories.PostRepository;
import org.truBlog.dataTransferObjects.requests.*;
import org.truBlog.dataTransferObjects.responses.CommentOnPostResponse;
import org.truBlog.dataTransferObjects.responses.ViewPostResponse;
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

    @Override
    public String createdPostId() {
        int index = postRepository.findAll().size() - 1;
        return postRepository.findAll().get(index).getId();
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
    public void deletePost(DeletePostRequest deletePostRequest, User user) {
        Post post = findPostById(deletePostRequest.getPostId());
        //if (user.getPassword().equals(deletePostRequest.))
//        user.getPosts().remove(post);
        postRepository.delete(post);
    }

    @Override
    public ViewPostResponse viewPost(ViewPostRequest viewPostRequest, Optional<User> user) {
        Post post = findPostById(viewPostRequest.getId());
        View view = viewService.viewPost(viewPostRequest, user);
        post.getViews().add(view);
        postRepository.save(post);
        return viewPostResponseMap(view);
    }

    @Override
    public CommentOnPostResponse commentInPost(CommentOnPostRequest commentOnPostRequest, Optional<User> user) {
        Post post = findPostById(commentOnPostRequest.getPostId());
        viewService.commentOnPost(commentOnPostRequest, user, post);
        return null;
    }


}
