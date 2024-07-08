package com.stackoverflowcompetitor.repository;

import com.stackoverflowcompetitor.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q " +
            "LEFT JOIN q.votes v " +
            "GROUP BY q " +
            "ORDER BY SUM(CASE WHEN v.isUpvote = true THEN 1 ELSE 0 END) - SUM(CASE WHEN v.isUpvote = false THEN 1 ELSE 0 END) DESC")
    Page<Question> findTopVotedQuestions(Pageable pageable);

    List<Question> findByTags_Name(String tagName);

    @Query("SELECT q FROM Question q WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(q.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Question> searchQuestionsByTitleOrContent(@Param("searchTerm") String searchTerm);
}
