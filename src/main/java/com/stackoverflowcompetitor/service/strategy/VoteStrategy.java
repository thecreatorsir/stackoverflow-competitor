package com.stackoverflowcompetitor.service.strategy;

import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.model.Vote;

public interface VoteStrategy<T> {
    T findEntityById(Long id);
    Vote findVoteByUserAndEntity(User user, T entity);
    Vote saveVote(Vote vote);
    void deleteVote(Vote vote);
    void setEntity(Vote vote, T entity);
}