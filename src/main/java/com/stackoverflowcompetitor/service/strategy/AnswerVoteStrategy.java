package com.stackoverflowcompetitor.service.strategy;

import com.stackoverflowcompetitor.model.Answer;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.model.Vote;
import com.stackoverflowcompetitor.repository.AnswerRepository;
import com.stackoverflowcompetitor.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AnswerVoteStrategy implements VoteStrategy<Answer> {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Override
    public Answer findEntityById(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found with id: " + id));
    }

    @Override
    public Vote findVoteByUserAndEntity(User user, Answer entity) {
        return voteRepository.findByUserAndAnswer(user, entity);
    }

    @Override
    public Vote saveVote(Vote vote) {
        return voteRepository.save(vote);
    }

    @Override
    public void deleteVote(Vote vote) {
        voteRepository.delete(vote);
    }

    @Override
    public void setEntity(Vote vote, Answer entity) {
        vote.setAnswer(entity);
    }
}

