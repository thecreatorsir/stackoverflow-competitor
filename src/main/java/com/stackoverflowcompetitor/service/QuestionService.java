package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.common.AuthenticatedUserDetails;
import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.model.Tag;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.repository.QuestionRepository;
import com.stackoverflowcompetitor.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AuthenticatedUserDetails authenticatedUserDetails;

    public Question postQuestion(Question question,List<Long> tagIds) {

        // TODO:add tag to tag db if not present
        log.info("tags element" + tagIds);
         List<Tag> tags = tagRepository.findAllById(tagIds);
        log.info("tags element" + tags);
        question.setTags(tags);
        User user = authenticatedUserDetails.getAuthenticatedUser();
        question.setUser(user);
        return questionRepository.save(question);
    }

    public Page<Question> getTopVotedQuestions(Pageable pageable) {
        return questionRepository.findTopVotedQuestions(pageable);
    }

    public List<Question> findByTagName(String tagName) {
        return questionRepository.findByTags_Name(tagName);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
}

