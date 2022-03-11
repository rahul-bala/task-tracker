package com.ey.tasktracker.controller;

import com.ey.tasktracker.model.entities.Task;
import com.ey.tasktracker.model.dto.TaskCreationEntity;
import com.ey.tasktracker.service.TaskService;
import com.ey.tasktracker.util.HttpHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.util.List;

/**
 *
 * This controller class exposed restful endpoints that provide the capability to interact with tasks in the task tracker
 * The following operations are currently supported:
 * 1. Given a user id retrieve all tasks saved by that user
 * 2. Create a new task for a given user
 * 3. Updated a task that was formerly created
 * 4. Delete an existing task
 * */

@RestController
@RequestMapping(value = "task")
public class TaskController {

    Logger log = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    TaskService taskService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Task> fetchTasks(@RequestParam String userId, HttpServletResponse response){
        List<Task> t = null;
        try {
            t = taskService.getTaskList(userId);
        } catch (ValidationException v) {
            HttpHelpers.handleHttpResponses(v.getMessage(), response,400);
        } catch (Exception e){
            HttpHelpers.handleInternalServerError(e, response, log);
        }
        return t;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Task addTask(@RequestBody TaskCreationEntity taskCreationEntity, HttpServletResponse response){
        Task t = null;
        try {
            t = taskService.createTask(taskCreationEntity);
        } catch (ValidationException v) {
            HttpHelpers.handleHttpResponses(v.getMessage(), response,400);
        } catch (Exception e){
            HttpHelpers.handleInternalServerError(e, response, log);
        }
        return t;
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Task updateTask(@RequestParam String taskId, @RequestBody TaskCreationEntity taskCreationEntity, HttpServletResponse response){
        Task t = null;
        try {
            t = taskService.updateTask(taskId, taskCreationEntity);
        } catch (ValidationException v) {
            HttpHelpers.handleHttpResponses(v.getMessage(), response,400);
        } catch (Exception e){
            HttpHelpers.handleInternalServerError(e, response, log);
        }
        return t;
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteTask(@RequestParam String taskId){
        taskService.deleteTask(taskId);
    }
}
