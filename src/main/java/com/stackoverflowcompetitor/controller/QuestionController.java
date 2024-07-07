package com.stackoverflowcompetitor.controller;

import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @PostMapping("/postQuestion")
    public Question postQuestion(@RequestBody Question question, @RequestParam List<Long> tagIds) {
        return questionService.postQuestion(question, tagIds);
    }

    @GetMapping("/top-voted")
    public ResponseEntity<Page<Question>> getTopVotedQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> topVotedQuestions = questionService.getTopVotedQuestions(pageable);
        return ResponseEntity.ok(topVotedQuestions);
    }

    @GetMapping("/getAllQuestions")
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }
}

