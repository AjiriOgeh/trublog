package org.truBlog.dataTransferObjects.responses;

import lombok.Data;

@Data
public class DeletePostResponse {
    private String id;
    private String username;
    private String dateDeleted;
}
