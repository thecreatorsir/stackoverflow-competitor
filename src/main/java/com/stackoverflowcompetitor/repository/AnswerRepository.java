package com.stackoverflowcompetitor.repository;

import com.stackoverflowcompetitor.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query("SELECT a FROM Answer a WHERE LOWER(a.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Answer> searchAnswerByContent(@Param("searchTerm") String searchTerm);
}
