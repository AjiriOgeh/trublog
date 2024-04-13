package org.truBlog.dataTransferObjects.requests;

import lombok.Data;

@Data
public class EditPostRequest {
    private String postId;
    private String username;
    private String editedTitle;
    private String editedContent;
}
