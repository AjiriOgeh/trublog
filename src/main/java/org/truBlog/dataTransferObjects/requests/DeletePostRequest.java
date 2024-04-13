package org.truBlog.dataTransferObjects.requests;

import lombok.Data;

@Data
public class DeletePostRequest {
    private String username;
    private String postId;

}
