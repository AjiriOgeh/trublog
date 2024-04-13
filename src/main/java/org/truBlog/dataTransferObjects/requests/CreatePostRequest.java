package org.truBlog.dataTransferObjects.requests;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String username;
    private String title;
    private String content;

}
