package org.truBlog.dataTransferObjects.responses;

import lombok.Data;

@Data
public class CreatePostResponse {
    private String id;
    private String title;
    private String dateCreated;
}
