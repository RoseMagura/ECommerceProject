package com.example.demo.security;

import com.example.demo.model.requests.CreateUserRequest;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.print.attribute.standard.Media;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<CreateUserRequest> json;

    public void createUser(String username, String password, String confirmPassword) throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        this.mockMvc.perform(post("/api/user/create")
                .content(json.write(request).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8));
    }
    @Test
    public void testCreateUserSuccess() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");
        this.mockMvc.perform(post("/api/user/create")
            .content(json.write(request).getJson())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    public void testCreateUserPasswordError() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("test");
        request.setConfirmPassword("testP");
        this.mockMvc.perform(post("/api/user/create")
                .content(json.write(request).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void testLoginSuccess() throws Exception {
        createUser("test", "testPassword", "testPassword");
        JSONObject obj = new JSONObject();
        obj.put("username", "test");
        obj.put("password", "testPassword");
        MvcResult mvcResult = this.mockMvc.perform(post("/login")
                .content(String.valueOf(obj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        System.out.println(mvcResult.getResponse());
        assertNotNull(mvcResult.getResponse().getHeaderValue("Authorization"));

//        mvcResult.andExpect(status().isOk())
//        .andExpect(MockMvcResultMatchers.jsonPath("$.Authorization").exists())

        
    }
    //TODO: tests
    //test that user profile gives 403 if not logged in

    //test that cart details gives 403 if not logged in

    //test that purchase history gives 403 if not logged in

    //using token, check that user profile gives 200

    //using token, check that cart details gives 200

    //using token, check that purchase history gives 200

}
