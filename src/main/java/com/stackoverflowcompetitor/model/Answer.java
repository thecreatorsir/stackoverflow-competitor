package com.stackoverflowcompetitor.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String content;

    private String mediaUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    //@JsonBackReference
    private Question question;

    @ManyToOne
    @JoinColumn(name = "parent_answer_id")
    //@JsonBackReference
    private Answer parentAnswer;

    @OneToMany(mappedBy = "parentAnswer", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonManagedReference
    private List<Answer> replies;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonManagedReference
    private List<Vote> votes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
