package com.ey.tasktracker.service;

import com.ey.tasktracker.model.dto.UserCreationEntity;
import com.ey.tasktracker.model.entities.User;
import com.ey.tasktracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository= userRepository;
    }

    /**
     * Workflow for user creation:
     * 1. Check if the user id the new user wishes to take is available. If the user id is taken, throw a validation exception
     * 2. If not, add an entry for the newly created user in the datastore
     * */
    public User addUser(UserCreationEntity userCreationEntity) throws ValidationException {
        Optional<User> user = userRepository.findById(userCreationEntity.getUserId());
        if(user.isPresent())
            throw new ValidationException(String.format("User id %s is already taken. Please use another user id", userCreationEntity.getUserId()));
        User u = userCreationEntity.generateUser();
        userRepository.save(u);
        return u;
    }

    /**
     * Workflow for user retrieval:
     * 1. Check if user id passed actually exists in the datastore. If the user exists, return the relevant info
     * 2. If not, throw a validation exception
     * */
    public User getUser(String userId) throws ValidationException {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent())
            return user.get();
        throw new ValidationException(String.format("User id %s does not exist", userId));
    }

    /**
     * Utility function that given a user id, checks for the presence of the corresponding user in the datastore.
     * */
    public boolean userExists(String userId){
        return userRepository.existsById(userId);
    }
}

