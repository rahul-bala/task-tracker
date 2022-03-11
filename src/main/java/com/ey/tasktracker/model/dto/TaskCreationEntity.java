package com.ey.tasktracker.model.dto;

import com.ey.tasktracker.model.entities.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * This model class governs the structure for communication with the endpoints for the
 * purpose of creation and updation of a task
 * */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreationEntity {

    private String userId;
    private String description;

    public Task generateTask(){
        return new Task(this.userId, this.description);
    }

    public Task generateTask(String taskId){
        return new Task(this.userId, this.description);
    }
}
