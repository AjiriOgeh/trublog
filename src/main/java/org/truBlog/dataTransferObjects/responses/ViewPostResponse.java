package org.truBlog.dataTransferObjects.responses;

import lombok.Data;

@Data
public class ViewPostResponse {
    private String viewId;
    private String timeOfView;
    private String viewer;
}
