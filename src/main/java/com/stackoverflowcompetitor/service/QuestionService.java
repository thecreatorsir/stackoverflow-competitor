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


    public Question postQuestion(Question question, List<Long> tagIds) {
        log.info("In a postQuestion method");
        try {
            List<Tag> tags = tagRepository.findAllById(tagIds);
            if (tags.isEmpty()) {
                log.warn("No tags found for IDs: {}", tagIds);
            }
            question.setTags(tags);

            User user = authenticatedUserDetails.getAuthenticatedUser();
            question.setUser(user);

            log.info("Saving question by user: {}", user.getUsername());
            return questionRepository.save(question);
        } catch (Exception e) {
            log.error("Error posting question: {}", e.getMessage());
            throw new RuntimeException("Error posting question", e);
        }
    }


    public Page<Question> getTopVotedQuestions(Pageable pageable) {
        log.info("In a topVotedQuestions method");
        return questionRepository.findTopVotedQuestions(pageable);
    }


    public List<Question> findByTagName(String tagName) {
        log.info("In a findByTagName method");
        return questionRepository.findByTags_Name(tagName);
    }

    public List<Question> getAllQuestions() {
        log.info("In a findAllQuestions method");
        return questionRepository.findAll();
    }

    public List<Question> searchQuestions(String searchTerm) {
        log.info("In a searchQuestions method");
        return questionRepository.searchQuestionsByTitleOrContent(searchTerm);
    }
}
