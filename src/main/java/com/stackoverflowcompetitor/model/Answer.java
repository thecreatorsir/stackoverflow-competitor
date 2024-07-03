package com.stackoverflowcompetitor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long questionId;
    private Long userId;
    private String content;
    private String mediaUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
