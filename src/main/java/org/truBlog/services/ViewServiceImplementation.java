package org.truBlog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.data.models.View;
import org.truBlog.data.repositories.ViewRepository;
import org.truBlog.dataTransferObjects.requests.CommentOnPostRequest;
import org.truBlog.dataTransferObjects.requests.ViewPostRequest;
import org.truBlog.utilities.Mappers;

import java.util.Optional;


@Service
public class ViewServiceImplementation implements ViewService{

    @Autowired
    private ViewRepository viewRepository;

    @Override
    public View viewPost(ViewPostRequest viewPostRequest, Optional<User> user) {
        View newView = Mappers.createViewPost(user);
        viewRepository.save(newView);
        return newView;
    }

    @Override
    public void commentOnPost(CommentOnPostRequest commentOnPostRequest, Optional<User> user, Post post) {

    }

}
