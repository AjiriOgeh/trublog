package org.truBlog.dataTransferObjects.requests;

import lombok.Data;

@Data
public class CommentInPostRequest {
    private String postId;
    private String username;
    private String comment;
}
