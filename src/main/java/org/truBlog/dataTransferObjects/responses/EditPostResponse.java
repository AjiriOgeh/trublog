package org.truBlog.dataTransferObjects.responses;

import lombok.Data;

@Data
public class EditPostResponse {
    private String id;
    private String title;
    private String dateEdited;
}
