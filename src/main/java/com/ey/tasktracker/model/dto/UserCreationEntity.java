package com.ey.tasktracker.model.dto;

import com.ey.tasktracker.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * This model class governs the structure for communication with the endpoints for the
 * purpose of creation and retrieval of users
 * */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreationEntity {

    private String userId;
    private String userName;

    public User generateUser(){
        return new User(this.userId, this.userName);
    }
}
