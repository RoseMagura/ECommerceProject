package com.example.demo.security;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import net.bytebuddy.utility.RandomString;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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

    @InjectMocks
//    @Autowired
    private UserController userController;

    @Mock
    private UserRepository userRepo;

    @Mock
    private CartRepository cartRepo;


//    public MvcResult createUser(String username, String password, String confirmPassword) throws Exception {
//        CreateUserRequest request = new CreateUserRequest();
//        request.setUsername(username);
//        request.setPassword(password);
//        request.setConfirmPassword(confirmPassword);
//        return this.mockMvc.perform(post("/api/user/create")
//                .content(json.write(request).getJson())
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON_UTF8))
//                .andReturn();
//    }
    public void createUserSwitch(String type) throws Exception {
        switch(type) {
            case "testSuccess":
                CreateUserRequest request = new CreateUserRequest();
                String username = RandomString.make(5);
                request.setUsername(username);
                request.setPassword("testPassword");
                request.setConfirmPassword("testPassword");
                this.mockMvc.perform(post("/api/user/create")
                        .content(json.write(request).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username));
            case "failure":
                CreateUserRequest req = new CreateUserRequest();
                String errorUsername = RandomString.make(5);
                req.setUsername(errorUsername);
                req.setPassword("test");
                req.setConfirmPassword("testP");
                this.mockMvc.perform(post("/api/user/create")
                        .content(json.write(req).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                        .andExpect(status().isBadRequest());
            case "setup":
                CreateUserRequest request2 = new CreateUserRequest();
                request2.setUsername("test");
                request2.setPassword("testPassword");
                request2.setConfirmPassword("testPassword");
                this.mockMvc.perform(post("/api/user/create")
                        .content(json.write(request2).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_UTF8));
        }
    }

    public String login(boolean testing) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("username", "test");
        obj.put("password", "testPassword");
        MvcResult mvcResult;
        if(testing){
            mvcResult = this.mockMvc.perform(post("/login")
                    .content(String.valueOf(obj))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andReturn();
            assertNotNull(mvcResult.getResponse().getHeaderValue("Authorization"));
        } else {
            mvcResult = this.mockMvc.perform(post("/login")
                    .content(String.valueOf(obj))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON_UTF8))
                    .andReturn();
        }
        return (String) mvcResult.getResponse().getHeaderValue("Authorization");
    }
    @Test
    public void testCreateUserSuccess() throws Exception {
        createUserSwitch("testSuccess");
//        CreateUserRequest request = new CreateUserRequest();
//        String username = RandomString.make(5);
//        request.setUsername(username);
//        request.setPassword("testPassword");
//        request.setConfirmPassword("testPassword");
//        this.mockMvc.perform(post("/api/user/create")
//            .content(json.write(request).getJson())
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON_UTF8))
//        .andExpect(status().isOk())
//        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
//        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username));
    }

    @Test
    public void testCreateUserPasswordError() throws Exception {
        createUserSwitch("failure");
//        CreateUserRequest request = new CreateUserRequest();
//        request.setUsername("test");
//        request.setPassword("test");
//        request.setConfirmPassword("testP");
//        this.mockMvc.perform(post("/api/user/create")
//                .content(json.write(request).getJson())
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON_UTF8))
//                .andExpect(status().isBadRequest());
    }
    @Test
    public void testLoginSuccess() throws Exception {
//        createUser("test", "testPassword", "testPassword");
        createUserSwitch("setup");
        login(true);
//        JSONObject obj = new JSONObject();
//        obj.put("username", "test");
//        obj.put("password", "testPassword");
//        MvcResult mvcResult = this.mockMvc.perform(post("/login")
//                .content(String.valueOf(obj))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON_UTF8))
//                .andExpect(status().isOk())
//                .andReturn();
//        assertNotNull(mvcResult.getResponse().getHeaderValue("Authorization"));
    }

    //test that user profile gives 403 if not logged in
    @Test
    public void testUserProfileUnauthenticated() throws Exception {
        mockMvc.perform(get("api/user/test"))
                .andExpect(status().isForbidden());
    }

    //test that cart details gives 403 if not logged in
    @Test
    public void testCartUnauthenticated() throws Exception {
        mockMvc.perform(get("api/cart/details?username=test"))
                .andExpect(status().isForbidden());
    }
    //test that purchase history gives 403 if not logged in
    @Test
    public void testHistoryUnauthenticated() throws Exception {
        mockMvc.perform(get("api/order/history/test"))
                .andExpect(status().isForbidden());
    }
    //TODO: DEBUGGING THESE
    //using token, check that user profile gives 200
    @Test
    public void testUserProfileAccess() throws Exception {
        createUserSwitch("setup");
        String token = login(false);
//        when(userRepo.findById(1L)).thenReturn(Optional.of(new User()));
//        ResponseEntity<User> user = ResponseEntity.of(userRepo.findById(1L));
//        User user = new User();
//        user.setUsername("testUser");
//        user.setPassword("password");
//        when(userRepo.findByUsername("testUser"))
//                .thenReturn(user);
//        ResponseEntity<User> userResponseEntity = userController.findByUserName("testUser");
//        System.out.println(userResponseEntity.toString());
        mockMvc.perform(get("api/user/testUser")
//        mockMvc.perform(get("api/user/id/1")
                        .header("Authorization", token))
//                .andExpect(status().isOk())
                ;
    }
    //using token, check that cart details gives 200
    @Test
    public void testCartAccess() throws Exception {
        createUserSwitch("setup");
        String token = login(false);
        mockMvc.perform(get("api/cart/details?username=test")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }
    //using token, check that purchase history gives 200
    @Test
    public void checkHistoryAccess() throws Exception {
        createUserSwitch("setup");
        String token = login(false);
        mockMvc.perform(get("api/order/history/test")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }
}
