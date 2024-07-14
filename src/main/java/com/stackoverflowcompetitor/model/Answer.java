package com.stackoverflowcompetitor.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Answer extends DateTimeCreation{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String content;

    private String mediaUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-answers")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference(value = "question-answers")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "parent_answer_id")
    @JsonBackReference(value = "parent-answer-replies")
    private Answer parentAnswer;

    @OneToMany(mappedBy = "parentAnswer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "parent-answer-replies")
    private List<Answer> replies;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "answer-votes")
    private List<Vote> votes;

}