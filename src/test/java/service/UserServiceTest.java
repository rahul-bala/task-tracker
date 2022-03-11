package service;

import com.ey.tasktracker.model.dto.UserCreationEntity;
import com.ey.tasktracker.model.entities.User;
import com.ey.tasktracker.repository.UserRepository;
import com.ey.tasktracker.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.xml.bind.ValidationException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private UserService userService;
    private User sampleUser;
    private UserCreationEntity userCreationEntity;

    @Before
    public void setup() {
        userService = new UserService(userRepository);
        userCreationEntity = new UserCreationEntity("id_1", "name_1");
        sampleUser = userCreationEntity.generateUser();
    }

    @Test
    public void testAddUserWhenUserIdTaken() {
        Mockito.when(userRepository.findById(userCreationEntity.getUserId())).thenReturn(Optional.of(new User()));
        Assert.assertThrows(ValidationException.class, () -> userService.addUser(userCreationEntity));
    }

    @Test
    public void testAddUserWhenUSerIdAvailable() throws ValidationException{
        ArgumentCaptor<User> argCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.when(userRepository.findById(userCreationEntity.getUserId())).thenReturn(Optional.empty());
        userService.addUser(userCreationEntity);
        verify(userRepository, times(1)).save(argCaptor.capture());
        User passedUser = argCaptor.getValue();
        assertEquals(sampleUser.getId(), passedUser.getId());
        assertEquals(sampleUser.getName(), passedUser.getName());
    }

    @Test
    public void testGetUserWhenUserNotExists() {
        Mockito.when(userRepository.findById(userCreationEntity.getUserId())).thenReturn(Optional.empty());
        Assert.assertThrows(ValidationException.class, () -> userService.getUser(userCreationEntity.getUserId()));
    }

    @Test
    public void testGetUserWhenUseExists() throws ValidationException{
        Mockito.when(userRepository.findById(userCreationEntity.getUserId())).thenReturn(Optional.of(sampleUser));
        User returnedUser = userService.getUser(userCreationEntity.getUserId());
        assertEquals(sampleUser.getId(), returnedUser.getId());
        assertEquals(sampleUser.getName(), returnedUser.getName());
    }
}
