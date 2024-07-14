package com.stackoverflowcompetitor.controller;

import com.stackoverflowcompetitor.model.Vote;
import com.stackoverflowcompetitor.service.VoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/votes")
@Slf4j
public class VoteController {

    @Autowired
    private VoteService voteService;

    /**
     * Votes for a question.
     *
     * @param questionId (the ID of the question being voted on)
     * @param isUpvote   (whether the vote is an upvote (true) or downvote (false))
     * @return (the Vote entity if a new vote is created or an existing vote is changed, null if the vote is reset)
     */
    @PostMapping("/question/{questionId}/{isUpvote}")
    public ResponseEntity<Vote> voteForQuestion(@PathVariable Long questionId, @PathVariable boolean isUpvote) {
        try {
            Vote vote = voteService.voteForQuestion(questionId, isUpvote);
            return ResponseEntity.ok(vote);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while voting for the question", e);
        }
    }

    /**
     * Votes for an answer.
     *
     * @param answerId (the ID of the answer being voted on)
     * @param isUpvote (whether the vote is an upvote (true) or downvote (false))
     * @return (the Vote entity if a new vote is created or an existing vote is changed, null if the vote is reset)
     */
    @PostMapping("/answer/{answerId}/{isUpvote}")
    public ResponseEntity<Vote> voteForAnswer(@PathVariable Long answerId, @PathVariable boolean isUpvote) {
        try {
            Vote vote = voteService.voteForAnswer(answerId, isUpvote);
            return ResponseEntity.ok(vote);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while voting for the answer", e);
        }
    }
}
