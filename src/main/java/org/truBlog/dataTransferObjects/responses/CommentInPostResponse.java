package org.truBlog.dataTransferObjects.responses;

import lombok.Data;

@Data
public class CommentInPostResponse {
    private String commentId;
    private String comment;
    private String timeOfComment;
    private String commenterUsername;
}
