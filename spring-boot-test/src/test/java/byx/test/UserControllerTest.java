package byx.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testListAllUsers() throws Exception {
        MvcResult result = mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andReturn();
        String json = result.getResponse().getContentAsString();
        List<User> users = mapper.readValue(json, new TypeReference<>() {});
        assertEquals(3, users.size());
        assertEquals(new User(1, "user1", "123"), users.get(0));
        assertEquals(new User(2, "user2", "456"), users.get(1));
        assertEquals(new User(3, "user3", "789"), users.get(2));
    }
}
