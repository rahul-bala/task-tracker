package com.ey.tasktracker.controller;

import com.ey.tasktracker.model.dto.UserCreationEntity;
import com.ey.tasktracker.model.entities.User;
import com.ey.tasktracker.service.UserService;
import com.ey.tasktracker.util.HttpHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;

/**
 *
 * This controller class exposes restful endpoints that facilitate user management
 * The following functionalities are provided by the endpoint defined in this class:
 * 1. Given a user id retrieve the details of the user
 * 2. Create a new user
 * */

@RestController
@RequestMapping("/user")
public class UserActionsController {

    Logger log = LoggerFactory.getLogger(UserActionsController.class);

    @Autowired
    UserService userService;

    @RequestMapping(value="/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser(@PathVariable("userId") String userId, HttpServletResponse response){
        User u = null;
        try {
            u = userService.getUser(userId);
        } catch (ValidationException v) {
            HttpHelpers.handleHttpResponses(v.getMessage(), response,400);
        } catch (Exception e){
            HttpHelpers.handleInternalServerError(e, response, log);
        }
        return u;
    }

    @RequestMapping(method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(@RequestBody UserCreationEntity userInfo, HttpServletResponse response){
        User u = null;
        try {
            u = userService.addUser(userInfo);
        } catch (ValidationException v) {
            HttpHelpers.handleHttpResponses(v.getMessage(), response,400);
        } catch (Exception e){
            HttpHelpers.handleInternalServerError(e, response, log);
        }
        return u;
    }
}
