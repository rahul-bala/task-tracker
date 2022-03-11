package controller;

import com.ey.tasktracker.controller.TaskController;
import com.ey.tasktracker.model.dto.TaskCreationEntity;
import com.ey.tasktracker.model.entities.Task;
import com.ey.tasktracker.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.xml.bind.ValidationException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestTaskActionController extends BaseControllerTest{

    private final TaskService taskService = mock(TaskService.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private Task task;
    private TaskCreationEntity taskCreationEntity;
    private static final String EXCEPTION_MESSAGE = "Exception message";
    private static final String USER_ID = "user_id";

    @Autowired
    private TaskController taskController;

    @Before
    public void setUp() {
        taskCreationEntity = new TaskCreationEntity(USER_ID, "description");
        task = taskCreationEntity.generateTask();
        ReflectionTestUtils.setField(taskController, "taskService", taskService);
    }

    @Test
    public void getTask() throws Exception{
        List<Task> taskList = List.of(task);
        when(taskService.getTaskList(USER_ID)).thenReturn(taskList);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/task?userId=" + USER_ID);
        MockHttpServletResponse response = getMockMvc().perform(builder).andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        Task[] tasks = objectMapper.readValue(response.getContentAsString(), Task[].class);
        assertEquals(tasks.length, taskList.size());
        assertEquals(tasks[0].getTaskId(), taskList.get(0).getTaskId());
    }

    @Test
    public void testAddTaskWhenValidationsFail() throws Exception{
        when(taskService.createTask(taskCreationEntity)).thenThrow(new ValidationException(EXCEPTION_MESSAGE));

        MvcResult result = getMockMvc().perform(
                        post("/task").contentType(
                                MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(taskCreationEntity)))
                .andExpect(status().is4xxClientError()).andReturn();

        assertEquals(EXCEPTION_MESSAGE, result.getResponse().getContentAsString().trim());
    }

    @Test
    public void testAddTask() throws Exception{
        when(taskService.createTask(taskCreationEntity)).thenReturn(task);

        MvcResult result = getMockMvc().perform(
                        post("/task").contentType(
                                MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(taskCreationEntity)))
                .andExpect(status().isOk()).andReturn();

        Task t = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);
        assertEquals(t.getTaskId(), task.getTaskId());
        assertEquals(t.getUserId(), task.getUserId());
        assertEquals(t.getDescription(), task.getDescription());
        assertEquals(t.getCreatedAt(), task.getCreatedAt());
        assertEquals(t.getUpdatedAt(), task.getUpdatedAt());
    }

    @Test
    public void testUpdateTaskWhenValidationsFail() throws Exception{
        when(taskService.updateTask(task.getTaskId(), taskCreationEntity))
                .thenThrow(new ValidationException(EXCEPTION_MESSAGE));

        MvcResult result = getMockMvc().perform(
                        put("/task?taskId=" + task.getTaskId()).contentType(
                                MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(taskCreationEntity)))
                .andExpect(status().is4xxClientError()).andReturn();

        assertEquals(EXCEPTION_MESSAGE, result.getResponse().getContentAsString().trim());
    }

    @Test
    public void testUpdateTask() throws Exception{
        taskCreationEntity.setDescription("Updated description");

        Task expectedTask = taskCreationEntity.generateTask();
        expectedTask.setTaskId(task.getTaskId());

        when(taskService.updateTask(task.getTaskId(), taskCreationEntity)).thenReturn(expectedTask);

        MvcResult result = getMockMvc().perform(
                        put("/task?taskId=" + task.getTaskId()).contentType(
                                MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(taskCreationEntity)))
                .andExpect(status().isOk()).andReturn();

        Task t = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);
        assertEquals(t.getTaskId(), expectedTask.getTaskId());
        assertEquals(t.getUserId(), expectedTask.getUserId());
        assertEquals(t.getDescription(), expectedTask.getDescription());
    }

    @Test
    public void testDelete() throws Exception{
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        MvcResult result = getMockMvc().perform(
                        delete("/task?taskId=" + task.getTaskId()).contentType(
                                MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(taskCreationEntity)))
                .andExpect(status().isOk()).andReturn();
        verify(taskService, times(1)).deleteTask(argumentCaptor.capture());
        String passedTaskId = argumentCaptor.getValue();
        assertEquals(task.getTaskId(), passedTaskId);
    }
}
