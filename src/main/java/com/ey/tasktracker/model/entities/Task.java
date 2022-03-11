package com.ey.tasktracker.model.entities;

import com.ey.tasktracker.util.Helpers;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * This model class represents the Task object that will be created in the datastore
 * */

@Data
@NoArgsConstructor
@Entity
public class Task {

    @Id
    private String taskId;

    private String userId;

    private String description;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;

    public Task(String userId, String description){
        this.taskId = Helpers.generateId();
        this.userId = userId;
        this.description = description;
        LocalDateTime now = Helpers.getCurrentTime();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void update(String description){
        this.description = description;
        this.updatedAt = Helpers.getCurrentTime();
    }
}

