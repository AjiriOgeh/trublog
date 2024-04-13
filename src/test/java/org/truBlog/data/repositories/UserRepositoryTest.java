package org.truBlog.data.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.truBlog.data.models.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserCanBeCreated() {
        User user = new User();
        userRepository.save(user);
        assertEquals(1, userRepository.count());
    }

    @Test
    public void testUsersTwoCanBeCreated() {
        User user = new User();
        userRepository.save(user);
        userRepository.save(user);
        assertEquals(1, userRepository.count());
    }



}