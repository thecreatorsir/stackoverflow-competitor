package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.common.AuthenticatedUserDetails;
import com.stackoverflowcompetitor.model.Answer;
import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.model.Vote;
import com.stackoverflowcompetitor.repository.AnswerRepository;
import com.stackoverflowcompetitor.repository.QuestionRepository;
import com.stackoverflowcompetitor.repository.VoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VoteService {
    private static final Logger log = LoggerFactory.getLogger(VoteService.class);
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
        log.info("Inside voteForQuestion");
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));
        User user = authenticatedUserDetails.getAuthenticatedUser();

        Vote existingVote = voteRepository.findByUserAndQuestion(user, question);

        if (existingVote != null) {
            log.info("Existing vote for Question: {}", existingVote.toString());
            if(existingVote.isUpvote() == isUpvote){
                log.info("Resetting the vote for question with id: {}", questionId);
                voteRepository.delete(existingVote);
                return null; // Return null to indicate the vote was reset
            }else {
                log.info("Changing the existing vote from {} to {}", existingVote.isUpvote(),isUpvote);
                existingVote.setUpvote(isUpvote);
                voteRepository.save(existingVote);
                return existingVote;
            }
        }

        Vote vote = new Vote();
        vote.setUpvote(isUpvote);
        vote.setQuestion(question);
        vote.setUser(user);
        log.info("User {} voted {} for questionId {}", user.getUsername(),isUpvote, questionId);
        return voteRepository.save(vote);
    }

    @Transactional
    public Vote voteForAnswer(Long answerId, boolean isUpvote) {
        log.info("Inside voteForAnswer");
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found with id: " + answerId));
        User user = authenticatedUserDetails.getAuthenticatedUser();

        Vote existingVote = voteRepository.findByUserAndAnswer(user, answer);

        if (existingVote != null) {
            log.info("Existing vote for answer: {}", existingVote.toString());
            if(existingVote.isUpvote() == isUpvote){
                log.info("Resetting the vote for answer with id: {}", answerId);
                voteRepository.delete(existingVote);
                return null; // Return null to indicate the vote was reset
            }else {
                log.info("Changing the existing vote from {} to {}", existingVote.isUpvote(),isUpvote);
                existingVote.setUpvote(isUpvote);
                voteRepository.save(existingVote);
                return existingVote;
            }
        }

        Vote vote = new Vote();
        vote.setUpvote(isUpvote);
        vote.setAnswer(answer);
        vote.setUser(user);
        log.info("User {} voted {} for answerId {}", user.getUsername(),isUpvote, answerId);
        return voteRepository.save(vote);
    }
}

