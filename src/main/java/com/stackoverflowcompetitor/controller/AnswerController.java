package com.stackoverflowcompetitor.controller;

import com.stackoverflowcompetitor.model.Answer;
import com.stackoverflowcompetitor.service.AnswerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @param answer     (the answer to be posted)
     * @return (the posted answer)
     */
    @PostMapping("/question/{questionId}")
    public ResponseEntity<Answer> answerQuestion(@PathVariable Long questionId, @RequestBody Answer answer) {
        log.info("Answering question with ID: {}", questionId);
        try {
            Answer postedAnswer = answerService.answerQuestion(questionId, answer);
            return ResponseEntity.ok(postedAnswer);
        } catch (Exception e) {
            log.error("Error answering question: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Post a reply to an existing answer.
     *
     * @param answerId   (the ID of the parent answer)
     * @param reply      (the reply to be posted)
     * @param questionID (the ID of the question associated with the answer)
     * @return (the posted reply)
     */
    @PostMapping("/reply/{questionID}/{answerId}")
    public ResponseEntity<Answer> answerToAnswer(@PathVariable Long answerId, @RequestBody Answer reply, @PathVariable Long questionID) {
        log.info("Replying to answer with ID: {} for question ID: {}", answerId, questionID);
        try {
            Answer postedReply = answerService.answerToAnswer(answerId, reply, questionID);
            return ResponseEntity.ok(postedReply);
        } catch (Exception e) {
            log.error("Error replying to answer: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Retrieves answers based in searchTerm.
     * @param searchTerm (the searchTerm)
     * @return (the list of answers)
     */

    @GetMapping("/search")
    public ResponseEntity<List<Answer>> searchQuestions(@RequestParam String searchTerm) {
        log.info("Fetching answers by term: {}", searchTerm);
        try {
            List<Answer> answers = answerService.searchAnswers(searchTerm);
            return ResponseEntity.ok(answers);
        } catch (Exception e) {
            log.error("Error in searching the answer by text: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
}
