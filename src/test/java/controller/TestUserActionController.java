package controller;

import com.ey.tasktracker.controller.UserActionsController;
import com.ey.tasktracker.model.dto.UserCreationEntity;
import com.ey.tasktracker.model.entities.User;
import com.ey.tasktracker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.xml.bind.ValidationException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUserActionController extends BaseControllerTest{

    private final UserService userService = mock(UserService.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private User user;
    private UserCreationEntity userCreationEntity;
    private static final String EXCEPTION_MESSAGE = "Exception message";

    @Autowired
    private UserActionsController userActionsController;

    @Before
    public void setUp() {
        user = new User("user_id", "user_name");
        userCreationEntity = new UserCreationEntity("user_id", "user_name");
        ReflectionTestUtils.setField(userActionsController, "userService", userService);
    }

    @Test
    public void testGetUserWhenValidationsFail() throws Exception {
        when(userService.getUser(user.getId())).thenThrow(new ValidationException(EXCEPTION_MESSAGE));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/user/" + user.getId());
        MockHttpServletResponse response = getMockMvc().perform(builder).andReturn().getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        assertEquals(EXCEPTION_MESSAGE, response.getContentAsString().trim());
    }

    @Test
    public void testGetUser() throws Exception {
        when(userService.getUser(user.getId())).thenReturn(user);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/user/" + user.getId());
        MockHttpServletResponse response = getMockMvc().perform(builder).andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        User u = objectMapper.readValue(response.getContentAsString(), User.class);
        assertEquals(u.getId(), user.getId());
        assertEquals(u.getName(), user.getName());
        assertEquals(u.getCreatedAt(), user.getCreatedAt());
        assertEquals(u.getUpdatedAt(), user.getUpdatedAt());
    }

    @Test
    public void testAddUserWhenValidationsFail() throws Exception {
        when(userService.addUser(userCreationEntity)).thenThrow(new ValidationException(EXCEPTION_MESSAGE));

        MvcResult result = getMockMvc().perform(
                post("/user").contentType(
                        MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userCreationEntity)))
                .andExpect(status().is4xxClientError()).andReturn();

        assertEquals(EXCEPTION_MESSAGE, result.getResponse().getContentAsString().trim());
    }

    @Test
    public void testAddUser() throws Exception {
        when(userService.addUser(userCreationEntity)).thenReturn(user);

        MvcResult result = getMockMvc().perform(
                        post("/user").contentType(
                                MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userCreationEntity)))
                .andExpect(status().isOk()).andReturn();

        User u = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertEquals(u.getId(), user.getId());
        assertEquals(u.getName(), user.getName());
        assertEquals(u.getCreatedAt(), user.getCreatedAt());
        assertEquals(u.getUpdatedAt(), user.getUpdatedAt());
    }
}
