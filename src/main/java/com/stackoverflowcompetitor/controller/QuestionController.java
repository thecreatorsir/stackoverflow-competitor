package com.stackoverflowcompetitor.controller;

import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.service.QuestionService;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/questions")
@Slf4j
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * Posts a new question.
     *
     * @param question (the question to be posted)
     * @param tagIds   (the IDs of the tags associated with the question)
     * @return the posted question
     */
    @PostMapping("/postQuestion")
    public ResponseEntity<Question> postQuestion(@RequestBody Question question, @RequestParam List<Long> tagIds) {
        log.info("Posting new question with tags: {}", tagIds);
        try {
            Question postedQuestion = questionService.postQuestion(question, tagIds);
            return ResponseEntity.ok(postedQuestion);
        } catch (ValidationException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while posting a question", e);
        }
    }

    /**
     * Retrieves the top-voted questions with pagination.
     *
     * @param page (the page number to retrieve)
     * @param size (the number of questions per page)
     * @return the top-voted questions
     */
    @GetMapping("/top-voted")
    public ResponseEntity<Page<Question>> getTopVotedQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        log.info("Fetching top-voted questions, page: {}, size: {}", page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Question> topVotedQuestions = questionService.getTopVotedQuestions(pageable);
            return ResponseEntity.ok(topVotedQuestions);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while getting top voted questions", e);
        }
    }
    /**
     * Retrieves questions by tag name.
     *
     * @param tag (the name of the tag)
     * @return (the list of questions associated with the tag)
     */
    @GetMapping("/by-tag")
    public ResponseEntity<List<Question>> getQuestionsByTag(@RequestParam String tag) {
        log.info("Fetching questions by tag: {}", tag);
        try {
            List<Question> questions = questionService.findByTagName(tag);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred getting a question by tag", e);
        }
    }

    /**
     * Retrieves all questions.
     *
     * @return (the list of all questions)
     */
    @GetMapping("/getAllQuestions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        log.info("Fetching all questions");
        try {
            List<Question> questions = questionService.getAllQuestions();
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while getting all the question", e);
        }
    }

    /**
     * Retrieves questions based in searchTerm.
     * @param searchTerm (the searchTerm)
     * @return (the list of questions)
     */

    @GetMapping("/search")
    public ResponseEntity<List<Question>> searchQuestions(@RequestParam String searchTerm) {
        log.info("Fetching questions by term: {}", searchTerm);
        try {
            List<Question> questions = questionService.searchQuestions(searchTerm);
            return ResponseEntity.ok(questions);
        }catch (ValidationException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while searching for questions", e);
        }
    }
}
