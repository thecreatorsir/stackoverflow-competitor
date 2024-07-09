package com.stackoverflowcompetitor.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import java.util.Date;

@Data
@MappedSuperclass
public class DateTimeCreation {

    @Column(name = "created_time")
    protected Date createdTime;

    @Column(name = "modified_time")
    protected Date modifiedTime;

    @PrePersist
    private void prePersist(){
        this.createdTime = new Date();
        this.modifiedTime = new Date();
    }

    @PreUpdate
    private void preUpdate(){
        this.modifiedTime = new Date();
    }
}
