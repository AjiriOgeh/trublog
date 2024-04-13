package org.truBlog.services;

import org.truBlog.dataTransferObjects.requests.*;
import org.truBlog.dataTransferObjects.responses.*;

public interface UserService {
    RegisterResponse signUp(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    LogoutResponse logout(LogoutRequest logoutRequest);
    CreatePostResponse createPost(CreatePostRequest createPostRequest);
    String registeredUserId();
    String createdPostId();
    EditPostResponse editPost(EditPostRequest editPostRequest);
    DeletePostResponse deletePost(DeletePostRequest deletePostRequest);
    ViewPostResponse viewPost(ViewPostRequest viewPostRequest);
    void createAnonymousUser();
    CommentOnPostResponse commentInPost(CommentOnPostRequest commentOnPostRequest);
}
