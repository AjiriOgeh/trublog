package org.truBlog.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document("Users")
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isLocked;
    private LocalDateTime dateOfRegistration = LocalDateTime.now();
    @DBRef
    private List<Post> posts = new ArrayList<>();
}
