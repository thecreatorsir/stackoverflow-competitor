package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.common.AuthenticatedUserDetails;
import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.model.Tag;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.repository.QuestionRepository;
import com.stackoverflowcompetitor.repository.TagRepository;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class QuestionService {
    private final int minContentLength = 10;
    private final int maxContentLength = 2000;
    private final int minTitleLength = 5;
    private final int maxTitleLength = 250;
    private final int minSearchStringLength = 1;
    private final int maxSearchStringLength = 20;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AuthenticatedUserDetails authenticatedUserDetails;


    public Question postQuestion(Question question, List<Long> tagIds) {
        log.info("In a postQuestion method");
        try {
            if(validateLength(question.getContent(), minContentLength, maxContentLength)){
                log.error("Content length must be between " + minContentLength + " and " + maxContentLength + " characters");
                throw new ValidationException("Content length must be between " + minContentLength + " and " + maxContentLength + " characters");
            }

            if(validateLength(question.getTitle(), minTitleLength, maxTitleLength)){
                log.error("Title length must be between " + minTitleLength + " and " + maxTitleLength + " characters");
                throw new ValidationException("Title length must be between " + minTitleLength + " and " + maxTitleLength + " characters");
            }

            List<Tag> tags = tagRepository.findAllById(tagIds);
            if (tags.isEmpty()) {
                log.warn("No tags found for IDs: {}", tagIds);
            }
            question.setTags(tags);

            User user = authenticatedUserDetails.getAuthenticatedUser();
            question.setUser(user);

            log.info("Saving question by user: {}", user.getUsername());
            return questionRepository.save(question);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error posting question: {}", question.toString());
            throw e;
        }
    }


    public Page<Question> getTopVotedQuestions(Pageable pageable) {
        log.info("In a topVotedQuestions method");
        try {
            return questionRepository.findTopVotedQuestions(pageable);
        } catch (Exception e) {
            log.error("Error in getting topVotedQuestions");
            throw e;
        }
    }


    public List<Question> findByTagName(String tagName) {
        log.info("In a findByTagName method");
        try {
            return questionRepository.findByTags_Name(tagName);
        } catch (Exception e) {
            log.error("Error in findByTagName: {}", tagName);
            throw e;
        }
    }

    public List<Question> getAllQuestions() {
        log.info("In a findAllQuestions method");
        try {
            return questionRepository.findAll();
        } catch (Exception e) {
            log.error("Error in getting all questions");
            throw e;
        }
    }

    private boolean validateLength(String input, int min, int max) {
        return (input.length() < min || input.length() > max);
    }

    public List<Question> searchQuestions(String searchTerm) {
        log.info("In a searchQuestions method");
        try {
            if(validateLength(searchTerm, minSearchStringLength, maxSearchStringLength)){
                throw new ValidationException("searchTerm length must be between " + minSearchStringLength + " and " + maxSearchStringLength + " characters");
            }
            return questionRepository.searchQuestionsByTitleOrContent(searchTerm);
        } catch (ValidationException e) {
            log.error("searchTerm length must be between " + minSearchStringLength + " and " + maxSearchStringLength + " characters");
            throw e;
        }
        catch (Exception e) {
            log.error("Error in searching question by text: {}", searchTerm);
            throw e;
        }
    }
}
