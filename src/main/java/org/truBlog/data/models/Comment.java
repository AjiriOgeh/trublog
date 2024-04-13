package org.truBlog.data.models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("Posts")
public class Comment {
    @Id
    private String id;
    private LocalDateTime timeOfComment = LocalDateTime.now();
    private String comment;
    @DBRef
    private User commenter;

}
