package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.common.AuthenticatedUserDetails;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.model.Vote;
import com.stackoverflowcompetitor.service.strategy.AnswerVoteStrategy;
import com.stackoverflowcompetitor.service.strategy.QuestionVoteStrategy;
import com.stackoverflowcompetitor.service.strategy.VoteStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class VoteService {

    @Autowired
    private AuthenticatedUserDetails authenticatedUserDetails;

    @Autowired
    private QuestionVoteStrategy questionVoteStrategy;

    @Autowired
    private AnswerVoteStrategy answerVoteStrategy;

    @Transactional
    public Vote voteForQuestion(Long questionId, boolean isUpvote) {
        return vote(questionId, isUpvote, questionVoteStrategy);
    }

    @Transactional
    public Vote voteForAnswer(Long answerId, boolean isUpvote) {
        return vote(answerId, isUpvote, answerVoteStrategy);
    }

    private <T> Vote vote(Long id, boolean isUpvote, VoteStrategy<T> strategy) {
        log.info("In a vote method");

        T entity = strategy.findEntityById(id);
        User user = authenticatedUserDetails.getAuthenticatedUser();
        Vote existingVote = strategy.findVoteByUserAndEntity(user, entity);

        if (existingVote != null) {
            log.info("Existing vote for Entity: {}", existingVote.toString());
            if (existingVote.isUpvote() == isUpvote) {
                log.info("Resetting the vote for entity with ID: {}", id);
                strategy.deleteVote(existingVote);
                return null; // Return null to indicate the vote was reset
            } else {
                log.info("Changing the existing vote from {} to {}", existingVote.isUpvote(), isUpvote);
                existingVote.setUpvote(isUpvote);
                strategy.saveVote(existingVote);
                return existingVote;
            }
        }

        Vote vote = new Vote();
        vote.setUpvote(isUpvote);
        strategy.setEntity(vote, entity);
        vote.setUser(user);
        log.info("User {} voted {} for entityId {}", user.getUsername(), isUpvote, id);
        return strategy.saveVote(vote);
    }
}
