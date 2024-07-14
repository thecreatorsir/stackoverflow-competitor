package com.stackoverflowcompetitor.controller;

import com.stackoverflowcompetitor.model.Answer;
import com.stackoverflowcompetitor.service.AnswerService;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/answers")
@Slf4j
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    /**
     * Post an answer to a specific question.
     *
     * @param questionId (the ID of the question being answered)
     * @param media     (the media to be posted)
     * @param content     (the content to be posted)
     * @return (the posted answer)
     */
    @PostMapping("/question/{questionId}")
    public ResponseEntity<Answer> answerQuestion(@PathVariable Long questionId,
                                                 @RequestParam("content") String content,
                                                 @RequestParam(value = "media", required = false) MultipartFile media) {
        log.info("Answering question with ID: {}", questionId);
        try {
            Answer postedAnswer = answerService.answerQuestion(questionId, content, media);
            return ResponseEntity.ok(postedAnswer);
        } catch (ResponseStatusException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while answering the question", e);
        }
    }

    /**
     * Post a reply to an existing answer.
     *
     * @param answerId   (the ID of the parent answer)
     * @param media      (the media to be posted)
     * @param content    (the content to be posted)
     * @param questionID (the ID of the question associated with the answer)
     * @return (the posted reply)
     */
    @PostMapping("/reply/{questionID}/{answerId}")
    public ResponseEntity<Answer> answerToAnswer(@PathVariable Long answerId,
                                                 @RequestParam("content") String content,
                                                 @RequestParam(value = "media", required = false) MultipartFile media,
                                                 @PathVariable Long questionID) {
        log.info("Replying to answer with ID: {} for question ID: {}", answerId, questionID);
        try {
            Answer postedReply = answerService.answerToAnswer(answerId, content, media, questionID);
            return ResponseEntity.ok(postedReply);
        } catch (ResponseStatusException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while replying to the answer", e);
        }
    }

    /**
     * Retrieves answers based in searchTerm.
     * @param searchTerm (the searchTerm)
     * @return (the list of answers)
     */

    @GetMapping("/search")
    public ResponseEntity<List<Answer>> searchAnswers(@RequestParam String searchTerm) {
        log.info("Fetching answers by term: {}", searchTerm);
        try {
            List<Answer> answers = answerService.searchAnswers(searchTerm);
            return ResponseEntity.ok(answers);
        } catch (ValidationException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while searching for answers", e);
        }
    }
}
