package com.stackoverflowcompetitor.repository;

import com.stackoverflowcompetitor.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q " +
            "LEFT JOIN q.votes v " +
            "GROUP BY q " +
            "ORDER BY SUM(CASE WHEN v.isUpvote = true THEN 1 ELSE 0 END) - SUM(CASE WHEN v.isUpvote = false THEN 1 ELSE 0 END) DESC")
    Page<Question> findTopVotedQuestions(Pageable pageable);

}
