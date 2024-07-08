package com.stackoverflowcompetitor.controller;

import com.stackoverflowcompetitor.model.Vote;
import com.stackoverflowcompetitor.service.VoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        log.info("Voting for question with ID: {} and upvote: {}", questionId, isUpvote);
        try {
            Vote vote = voteService.voteForQuestion(questionId, isUpvote);
            return ResponseEntity.ok(vote);
        } catch (Exception e) {
            log.error("Error voting for question with ID: {}", questionId, e);
            return ResponseEntity.status(500).body(null);
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
        log.info("Voting for answer with ID: {} and upvote: {}", answerId, isUpvote);
        try {
            Vote vote = voteService.voteForAnswer(answerId, isUpvote);
            return ResponseEntity.ok(vote);
        } catch (Exception e) {
            log.error("Error voting for answer with ID: {}", answerId, e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
