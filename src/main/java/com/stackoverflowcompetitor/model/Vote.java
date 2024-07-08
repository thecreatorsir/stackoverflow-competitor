package com.stackoverflowcompetitor.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Vote extends DateTimeCreation{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "TINYINT(1)", nullable = false)
    private boolean isUpvote;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-votes")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonBackReference(value = "question-votes")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    @JsonBackReference(value = "answer-votes")
    private Answer answer;

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", isUpvote=" + isUpvote +
                '}';
    }
}