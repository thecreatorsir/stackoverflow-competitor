package com.stackoverflowcompetitor.controller;

import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
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
}

