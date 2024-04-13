package org.truBlog.dataTransferObjects.responses;

import lombok.Data;

@Data
public class CommentOnPostResponse {
    private String comment;
    private String timeOfComment;

}
