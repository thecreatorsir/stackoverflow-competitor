package com.stackoverflowcompetitor.repository;

import com.stackoverflowcompetitor.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
}
