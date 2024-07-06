package com.stackoverflowcompetitor.controller;


import com.stackoverflowcompetitor.model.Vote;
import com.stackoverflowcompetitor.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votes")
public class VoteController {
    @Autowired
    private VoteService voteService;

    @PostMapping("/question/{questionId}/{isUpvote}")
    public Vote voteForQuestion(@PathVariable Long questionId, @PathVariable boolean isUpvote) {
        return voteService.voteForQuestion(questionId, isUpvote);
    }

    @PostMapping("/answer/{answerId}/{isUpvote}")
    public Vote voteForAnswer(@PathVariable Long answerId, @PathVariable boolean isUpvote) {
        return voteService.voteForAnswer(answerId, isUpvote);
    }
}
