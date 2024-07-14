package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.common.AuthenticatedUserDetails;
import com.stackoverflowcompetitor.model.Answer;
import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.model.Vote;
import com.stackoverflowcompetitor.repository.AnswerRepository;
import com.stackoverflowcompetitor.repository.QuestionRepository;
import com.stackoverflowcompetitor.repository.VoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@Slf4j
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AuthenticatedUserDetails authenticatedUserDetails;

    @Transactional
    public Vote voteForQuestion(Long questionId, boolean isUpvote) {
        log.info("In a voteForQuestion method");
        try {
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> {
                        log.error("Question not found with ID: {}", questionId);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId);
                    });

            User user = authenticatedUserDetails.getAuthenticatedUser();
            Vote existingVote = voteRepository.findByUserAndQuestion(user, question);

            if (existingVote != null) {
                log.info("Existing vote for Question: {}", existingVote.toString());
                if (existingVote.isUpvote() == isUpvote) {
                    log.info("Resetting the vote for question with ID: {}", questionId);
                    voteRepository.delete(existingVote);
                    return null; // Return null to indicate the vote was reset
                } else {
                    log.info("Changing the existing vote from {} to {}", existingVote.isUpvote(), isUpvote);
                    existingVote.setUpvote(isUpvote);
                    voteRepository.save(existingVote);
                    return existingVote;
                }
            }

            Vote vote = new Vote();
            vote.setUpvote(isUpvote);
            vote.setQuestion(question);
            vote.setUser(user);
            log.info("User {} voted {} for questionId {}", user.getUsername(), isUpvote, questionId);
            return voteRepository.save(vote);
        } catch (Exception e) {
            log.error("Error voting for question with ID: {}", questionId, e);
            throw e;
        }
    }

    @Transactional
    public Vote voteForAnswer(Long answerId, boolean isUpvote) {
        log.info("In a voteForAnswer method");
        try {
            Answer answer = answerRepository.findById(answerId)
                    .orElseThrow(() -> {
                        log.error("Answer not found with ID: {}", answerId);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found with id: " + answerId);
                    });

            User user = authenticatedUserDetails.getAuthenticatedUser();
            Vote existingVote = voteRepository.findByUserAndAnswer(user, answer);

            if (existingVote != null) {
                log.info("Existing vote for Answer: {}", existingVote.toString());
                if (existingVote.isUpvote() == isUpvote) {
                    log.info("Resetting the vote for answer with ID: {}", answerId);
                    voteRepository.delete(existingVote);
                    return null; // Return null to indicate the vote was reset
                } else {
                    log.info("Changing the existing vote from {} to {}", existingVote.isUpvote(), isUpvote);
                    existingVote.setUpvote(isUpvote);
                    voteRepository.save(existingVote);
                    return existingVote;
                }
            }

            Vote vote = new Vote();
            vote.setUpvote(isUpvote);
            vote.setAnswer(answer);
            vote.setUser(user);
            log.info("User {} voted {} for answerId {}", user.getUsername(), isUpvote, answerId);
            return voteRepository.save(vote);
        } catch (Exception e) {
            log.error("Error voting for answer with ID: {}", answerId, e);
            throw e;
        }
    }
}
