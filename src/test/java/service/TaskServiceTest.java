package service;

import com.ey.tasktracker.model.dto.TaskCreationEntity;
import com.ey.tasktracker.model.entities.Task;
import com.ey.tasktracker.repository.TaskRepository;
import com.ey.tasktracker.service.TaskService;
import com.ey.tasktracker.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.xml.bind.ValidationException;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    private final UserService userService = mock(UserService.class);
    private final TaskRepository taskRepository = mock(TaskRepository.class);

    private TaskService taskService;
    private Task task;
    private TaskCreationEntity taskCreationEntity;

    @Before
    public void setup() {
        taskService = new TaskService(userService, taskRepository);
        taskCreationEntity = new TaskCreationEntity("user_id", "description");
        task = taskCreationEntity.generateTask();
    }

    @Test
    public void testTaskCreation() throws ValidationException{
        ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.when(userService.userExists(taskCreationEntity.getUserId())).thenReturn(true);
        taskService.createTask(taskCreationEntity);
        verify(taskRepository, times(1)).save(argumentCaptor.capture());
        Task invokedWith = argumentCaptor.getValue();
        assertEquals(invokedWith.getUserId(), task.getUserId());
        assertEquals(invokedWith.getDescription(), task.getDescription());
    }

    @Test
    public void testTaskCreationWhenUserIdInvalid(){
        ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.when(userService.userExists(taskCreationEntity.getUserId())).thenReturn(false);
        Assert.assertThrows(ValidationException.class, () -> taskService.createTask(taskCreationEntity));
        verify(taskRepository, times(0)).save(argumentCaptor.capture());
    }

    @Test
    public void testUpdateTask() throws ValidationException{
        ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);

        taskCreationEntity.setDescription("Updated description");
        Mockito.when(taskRepository.findById(task.getTaskId())).thenReturn(Optional.of(task));
        taskService.updateTask(task.getTaskId(), taskCreationEntity);
        verify(taskRepository, times(1)).save(argumentCaptor.capture());

        Task passedTask = argumentCaptor.getValue();
        assertEquals(taskCreationEntity.getDescription(), passedTask.getDescription());
    }

    @Test
    public void testUpdateTaskWhenTaskIdInvalid(){
        taskCreationEntity.setDescription("Updated description");
        Mockito.when(taskRepository.findById(task.getTaskId())).thenReturn(Optional.empty());
        Assert.assertThrows(ValidationException.class, () -> taskService.updateTask(task.getTaskId(), taskCreationEntity));
    }

    @Test
    public void testUpdateTaskWhenTaskBelongsToAnotherUser(){
        taskCreationEntity.setDescription("Updated description");
        taskCreationEntity.setUserId("some_other_user");
        Mockito.when(taskRepository.findById(task.getTaskId())).thenReturn(Optional.of(task));
        Assert.assertThrows(ValidationException.class, () -> taskService.updateTask(task.getTaskId(), taskCreationEntity));
    }

    @Test
    public void testGetTask() throws ValidationException{
        List<Task> tasks = List.of(task);
        Mockito.when(userService.userExists(taskCreationEntity.getUserId())).thenReturn(true);
        Mockito.when(taskRepository.findAllByUserId(taskCreationEntity.getUserId())).thenReturn(tasks);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        List<Task> resp = taskService.getTaskList(task.getUserId());
        verify(taskRepository, times(1)).findAllByUserId(argumentCaptor.capture());
        assertEquals(tasks.size(), resp.size());
        assertEquals(tasks.get(0).getTaskId(), resp.get(0).getTaskId());
    }

    @Test
    public void testGetTaskWhenUserIdInvalid(){
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(userService.userExists(taskCreationEntity.getUserId())).thenReturn(false);
        Assert.assertThrows(ValidationException.class, () -> taskService.getTaskList(taskCreationEntity.getUserId()));
        verify(taskRepository, times(0)).findAllByUserId(argumentCaptor.capture());
    }

    @Test
    public void deleteTaskWhenTaskIdValid(){
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(taskService.taskExists(task.getTaskId())).thenReturn(true);
        taskService.deleteTask(task.getTaskId());
        verify(taskRepository, times(1)).deleteById(argumentCaptor.capture());
        String passedTaskId = argumentCaptor.getValue();
        assertEquals(task.getTaskId(), passedTaskId);
    }

    @Test
    public void deleteTaskWhenTaskIdInvalid(){
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.when(taskService.taskExists(task.getTaskId())).thenReturn(false);
        taskService.deleteTask(task.getTaskId());
        verify(taskRepository, times(0)).deleteById(argumentCaptor.capture());
    }
}
