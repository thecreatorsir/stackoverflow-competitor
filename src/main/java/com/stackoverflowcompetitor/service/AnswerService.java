package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.common.AuthenticatedUserDetails;
import com.stackoverflowcompetitor.model.Answer;
import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.repository.AnswerRepository;
import com.stackoverflowcompetitor.repository.QuestionRepository;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;


@Service
@Slf4j
public class AnswerService {
    private final int minContentLength = 10;
    private final int maxContentLength = 2000;
    private final int minSearchStringLength = 1;
    private final int maxSearchStringLength = 20;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AuthenticatedUserDetails authenticatedUserDetails;

    @Autowired
    private MediaService mediaService;

    public Answer answerQuestion(Long questionId, String content, MultipartFile media) throws IOException {
        log.info("In answerQuestion method");
        try {
            if(validateLength(content, minContentLength, maxContentLength)){
                throw new ValidationException("Content length must be between " + minContentLength + " and " + maxContentLength + " characters");
            }
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));

            String mediaUrl = null;
            if (media != null && !media.isEmpty()) {
                mediaUrl = mediaService.uploadFile(media);
            }

            Answer answer = new Answer();
            answer.setContent(content);
            answer.setMediaUrl(mediaUrl);
            answer.setQuestion(question);

            User user = authenticatedUserDetails.getAuthenticatedUser();
            answer.setUser(user);

            log.info("Saving answer for question ID: {}", questionId);
            return answerRepository.save(answer);
        }catch (IOException e) {
            log.error("Error uploading media for question ID: {}", questionId);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload media", e);
        } catch (ValidationException e) {
            log.error("Content length must be between " + minContentLength + " and " + maxContentLength + " characters");
            throw e;
        } catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("Error answering question with ID: {}", questionId);
            throw e;
        }
    }

    public Answer answerToAnswer(Long answerId, String content, MultipartFile media, Long questionID) throws IOException {
        log.info("In answerToAnswer method");
        try {
            if(validateLength(content, minContentLength, maxContentLength)){
                throw new ValidationException("Content length must be between " + minContentLength + " and " + maxContentLength + " characters");
            }
            log.info("Finding parent answer with ID: {}", answerId);
            Answer parentAnswer = answerRepository.findById(answerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found with id: " + answerId));

            if (!parentAnswer.getQuestion().getId().equals(questionID)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Question ID mismatch");
            }

            log.info("Finding question with ID: {}", questionID);
            Question question = questionRepository.findById(questionID)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionID));

            String mediaUrl = null;
            if (media != null && !media.isEmpty()) {
                mediaUrl = mediaService.uploadFile(media);
            }

            Answer reply = new Answer();
            reply.setContent(content);
            reply.setMediaUrl(mediaUrl);
            reply.setQuestion(question);
            reply.setParentAnswer(parentAnswer);

            User user = authenticatedUserDetails.getAuthenticatedUser();
            reply.setUser(user);

            log.info("Saving reply for answer ID: {} and question ID: {}", answerId, questionID);
            return answerRepository.save(reply);
        } catch (IOException e) {
            log.error("Error uploading media for answer ID: {}", answerId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload media", e);
        }catch (ValidationException e) {
            log.error("Content length must be between " + minContentLength + " and " + maxContentLength + " characters");
            throw e;
        } catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("Error replying to answer with ID: {} for question ID: {}", answerId, questionID);
            throw e;
        }
    }

    private boolean validateLength(String input, int min, int max) {
        return (input.length() < min || input.length() > max);
    }

    public List<Answer> searchAnswers(String searchTerm) {
        log.info("In searchAnswers method");
        try {
            if(validateLength(searchTerm, minSearchStringLength, maxSearchStringLength)){
                throw new ValidationException("searchTerm length must be between " + minSearchStringLength + " and " + maxSearchStringLength + " characters");
            }
            return answerRepository.searchAnswerByContent(searchTerm);
        } catch (ValidationException e) {
            log.error("searchTerm length must be between " + minSearchStringLength + " and " + maxSearchStringLength + " characters");
            throw e;
        }
        catch (Exception e) {
            log.error("Error while searching an answer by text: {}", searchTerm);
            throw e;
        }
    }
}
