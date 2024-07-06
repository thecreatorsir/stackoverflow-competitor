package com.stackoverflowcompetitor.controller;

import com.stackoverflowcompetitor.model.Answer;
import com.stackoverflowcompetitor.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/answers")
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    @PostMapping("/question/{questionId}")
    public Answer answerQuestion(@PathVariable Long questionId, @RequestBody Answer answer) {
        return answerService.answerQuestion(questionId, answer);
    }

    @PostMapping("/reply/{questionID}/{answerId}")
    public Answer answerToAnswer(@PathVariable Long answerId, @RequestBody Answer reply, @PathVariable Long questionID) {
        return answerService.answerToAnswer(answerId, reply, questionID);
    }
}
