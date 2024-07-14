package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.common.AuthenticatedUserDetails;
import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.model.Tag;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.repository.QuestionRepository;
import com.stackoverflowcompetitor.repository.TagRepository;
import com.stackoverflowcompetitor.util.Constants;
import com.stackoverflowcompetitor.util.ValidationUtil;
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

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AuthenticatedUserDetails authenticatedUserDetails;


    public Question postQuestion(Question question, List<Long> tagIds) {
        log.info("In a postQuestion method");
        try {
            if(ValidationUtil.validateLength(question.getContent(), Constants.MIN_CONTENT_LENGTH, Constants.MAX_CONTENT_LENGTH)){
                log.error("Invalid Content length");
                throw new ValidationException("Content length must be between " + Constants.MIN_CONTENT_LENGTH + " and " + Constants.MAX_CONTENT_LENGTH + " characters");
            }

            if(ValidationUtil.validateLength(question.getTitle(), Constants.MIN_TITLE_LENGTH, Constants.MAX_TITLE_LENGTH)){
                log.error("Invalid Title length");
                throw new ValidationException("Title length must be between " + Constants.MIN_TITLE_LENGTH + " and " + Constants.MAX_TITLE_LENGTH + " characters");
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

    public List<Question> searchQuestions(String searchTerm) {
        log.info("In a searchQuestions method");
        try {
            if(ValidationUtil.validateLength(searchTerm, Constants.MIN_SEARCH_STRING_LENGTH, Constants.MAX_SEARCH_STRING_LENGTH)){
                log.error("Invalid searchTerm length");
                throw new ValidationException("searchTerm length must be between " + Constants.MIN_SEARCH_STRING_LENGTH + " and " + Constants.MAX_SEARCH_STRING_LENGTH + " characters");
            }
            return questionRepository.searchQuestionsByTitleOrContent(searchTerm);
        } catch (ValidationException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("Error in searching question by text: {}", searchTerm);
            throw e;
        }
    }
}
