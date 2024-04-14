package org.truBlog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.truBlog.data.models.Comment;
import org.truBlog.data.models.Post;
import org.truBlog.data.models.User;
import org.truBlog.data.models.View;
import org.truBlog.data.repositories.ViewRepository;
import org.truBlog.dataTransferObjects.requests.CommentInPostRequest;
import org.truBlog.dataTransferObjects.requests.ViewPostRequest;
import org.truBlog.utilities.Mappers;

import java.util.Optional;


@Service
public class ViewServiceImplementation implements ViewService{

    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private CommentService commentService;

    @Override
    public View viewPost(ViewPostRequest viewPostRequest, User user) {
        View newView = Mappers.createViewPost(user);
        viewRepository.save(newView);
        return newView;
    }

    @Override
    public View commentOnPost(CommentInPostRequest commentInPostRequest, User user, Post post) {
        View newView = Mappers.createViewPost(user);
        return viewRepository.save(newView);
    }

}
