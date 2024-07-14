package com.stackoverflowcompetitor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Tag extends DateTimeCreation{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 15)
    private String name;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private List<Question> questions;

}