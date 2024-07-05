package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.model.Tag;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.repository.QuestionRepository;
import com.stackoverflowcompetitor.repository.TagRepository;
import com.stackoverflowcompetitor.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    public Question postQuestion(Question question,List<Long> tagIds) {

        // add tag to tag db if not present
        log.info("tags element" + tagIds);
         List<Tag> tags = tagRepository.findAllById(tagIds);
        log.info("tags element" + tags);
        question.setTags(tags);

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername = authentication.getName();
//
//        log.info("current user name is {}", currentUsername);
//        // Fetch the user from the database using the username
//        User user = userRepository.findByUsername(currentUsername)
//                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));

        return questionRepository.save(question);
    }
}

