package com.ey.tasktracker.service;

import com.ey.tasktracker.model.entities.Task;
import com.ey.tasktracker.model.dto.TaskCreationEntity;
import com.ey.tasktracker.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;

/**
 * This service class provides the implementations required to support the operations that will be performed
 * as part of the task management workflow. Docstrings on the methods below describe each operation in detail
 *
 * */

@Service
public class TaskService {

    Logger log = LoggerFactory.getLogger(TaskService.class);

    private UserService userService;

    private TaskRepository taskRepository;

    @Autowired
    public TaskService(UserService userService, TaskRepository taskRepository) {
        this.userService = userService;
        this.taskRepository = taskRepository;
    }


    /**
     * Workflow for new task creation:
     * 1. Validate if the user id with whom the task is tagged indeed exists. If not, throw a validation exception as we would want tag tasks to valid users
     * 2. If successfully validated, persist the task in the datastore
     * */
    public Task createTask(TaskCreationEntity taskCreationEntity) throws ValidationException {
        // The validation check can also be implemented using the Strategy design pattern if more complex validations are needed
        if(!userService.userExists(taskCreationEntity.getUserId()))
            throw new ValidationException(String.format("User id %s does not exist", taskCreationEntity.getUserId()));
        Task t = taskCreationEntity.generateTask();
        log.info(String.format("Generated new task with ID %s for user id %s", t.getTaskId(), t.getUserId()));
        taskRepository.save(t);
        return t;
    }


    /**
     * Workflow for task updates:
     * 1. Validate if the passed task id exists. To be able to update a task, it is necessary for the older version of the task to be present in the datastore
     * 2. Validate if the existing task is tagged to the same user. Updates to some other user's tasks are not permitted
     * 3. If all validations pass, make the requisite changes to task and persist the changes. Note that the audit field logging updated timestamp is updated to reflect the time of the update
     * */
    public Task updateTask(String taskId, TaskCreationEntity updatedTask) throws ValidationException {

        Optional<Task> opt = taskRepository.findById(taskId);
        if(!opt.isPresent()){
            log.error(String.format("Couldn't find task with ID %s to update", taskId));
            throw new ValidationException(String.format("Task id %s not present to update", taskId));
        }
        Task t = opt.get();
        if(!t.getUserId().equalsIgnoreCase(updatedTask.getUserId())){
            log.error(String.format("Task with ID %s does not belong to user %s", taskId, updatedTask.getUserId()));
            throw new ValidationException(
                    String.format("Task id %s does not belong to this user. Cannot update someone else's task",
                            taskId));
        }
        t.update(updatedTask.getDescription());
        taskRepository.save(t);
        return t;
    }

    /**
     * Workflow for task retrieval:
     * 1. Validate if passed user id exists. If not, throw a Validation exception as it is not possible to retrieve tasks for a non existent user
     * 2. Query the datastore for all tasks associated with the user and return the response to the caller
     * */
    public List<Task> getTaskList(String userId) throws ValidationException{
        // The validation check can also be implemented using the Strategy design pattern if more complex validations are needed
        if(!userService.userExists(userId))
            throw new ValidationException(String.format("User id %s does not exist", userId));
        return taskRepository.findAllByUserId(userId);
    }

    /**
     * Workflow for task deletion:
     * 1. Check if task id passed for deletion exists. If not, the function results in a no-op.
     * 2. If the task exists, delete it in the datastore
     * */
    public void deleteTask(String taskId){
        if (taskExists(taskId)) {
            log.info(String .format("Deleting task : %s", taskId));
            taskRepository.deleteById(taskId);
            return;
        }
        log.info(String .format("Couldn't find task : %s in the database. Skipping delete", taskId));
    }

    /**
     * Utility function that given a task id, checks for the presence of the corresponding task in the datastore.
     * */
    public boolean taskExists(String taskId){
        return taskRepository.existsById(taskId);
    }
}
