package com.stackoverflowcompetitor.repository;


import com.stackoverflowcompetitor.model.Answer;
import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Vote findByUserAndQuestion(User user, Question question);
    Vote findByUserAndAnswer(User user, Answer answer);
}

