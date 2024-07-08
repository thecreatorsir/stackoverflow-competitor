package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.common.AuthenticatedUserDetails;
import com.stackoverflowcompetitor.model.Answer;
import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.repository.AnswerRepository;
import com.stackoverflowcompetitor.repository.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@Slf4j
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AuthenticatedUserDetails authenticatedUserDetails;


    public Answer answerQuestion(Long questionId, Answer answer) {
        log.info("In answerQuestion method");
        try {
            log.info("Finding question with ID: {}", questionId);
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));
            log.info("Setting question and user for the answer.");
            answer.setQuestion(question);
            User user = authenticatedUserDetails.getAuthenticatedUser();
            answer.setUser(user);
            log.info("Saving answer for question ID: {}", questionId);
            return answerRepository.save(answer);
        } catch (ResponseStatusException e) {
            log.error("Error finding question with ID: {}", questionId, e);
            throw e;
        } catch (Exception e) {
            log.error("Error answering question with ID: {}", questionId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while answering the question", e);
        }
    }


    public Answer answerToAnswer(Long answerId, Answer reply, Long questionID) {
        log.info("In answerToAnswer method");
        try {
            log.info("Finding parent answer with ID: {}", answerId);
            Answer parentAnswer = answerRepository.findById(answerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found with id: " + answerId));

            if (!parentAnswer.getQuestion().getId().equals(questionID)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Question ID mismatch");
            }

            log.info("Finding question with ID: {}", questionID);
            Question question = questionRepository.findById(questionID)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionID));

            log.info("Setting question, user, and parent answer for the reply.");
            User user = authenticatedUserDetails.getAuthenticatedUser();
            reply.setUser(user);
            reply.setQuestion(question);
            reply.setParentAnswer(parentAnswer);
            log.info("Saving reply for answer ID: {} and question ID: {}", answerId, questionID);
            return answerRepository.save(reply);
        } catch (ResponseStatusException e) {
            log.error("Error handling reply to answer with ID: {} for question ID: {}", answerId, questionID, e);
            throw e;
        } catch (Exception e) {
            log.error("Error replying to answer with ID: {} for question ID: {}", answerId, questionID, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while replying to the answer", e);
        }
    }
    public List<Answer> searchAnswers(String searchTerm) {
        log.info("In searchAnswers method");
        return answerRepository.searchAnswerByContent(searchTerm);
    }
}
