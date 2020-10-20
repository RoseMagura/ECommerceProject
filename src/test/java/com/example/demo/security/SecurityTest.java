package com.example.demo.security;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import net.bytebuddy.utility.RandomString;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
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

    private UserController userController;

    private CartController cartController;

    private OrderController orderController;

    private final UserRepository userRepo = mock(UserRepository.class);

    private final CartRepository cartRepo = mock(CartRepository.class);

    private final ItemRepository itemRepo = mock(ItemRepository.class);


    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);

        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);

        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
    }

    public void initRepo(){
        User user = new User("test", "testPassword", new Cart());
        when(userRepo.findByUsername("test")).thenReturn(user);
        Cart cart = new Cart();
        cart.setUser(user);
        List<Item> items = new ArrayList<>();
        items.add(new Item("Round Widget", 2.99, "A widget that is round"));
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(2.99));
        user.setCart(cart);
        when(itemRepo.findById(1L)).thenReturn(
                java.util.Optional.of(new Item("Round Widget", 2.99, "A widget that is round")));
    }

    public String createUserSwitch(String type) throws Exception {
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
                return username;
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
                return errorUsername;
            case "setup":
                CreateUserRequest request2 = new CreateUserRequest();
                String username2 = RandomString.make(5);
                request2.setUsername(username2);
                request2.setPassword("testPassword");
                request2.setConfirmPassword("testPassword");
                this.mockMvc.perform(post("/api/user/create")
                        .content(json.write(request2).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_UTF8));
                return username2;
        }
        return "finished";
    }

    public String login(boolean testing, String username) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("username", username);
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
    }

    @Test
    public void testCreateUserPasswordError() throws Exception {
        createUserSwitch("failure");
    }
    @Test
    public void testLoginSuccess() throws Exception {
        String username = createUserSwitch("setup");
        login(true, username);
    }

    // Test that user profile gives 403 if not logged in
    @Test
    public void testUserProfileUnauthenticated() throws Exception {
        mockMvc.perform(get("api/user/test"))
                .andExpect(status().isForbidden());
    }

    // Test that cart details gives 403 if not logged in
    @Test
    public void testCartUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/cart/details?username=test"))
                .andExpect(status().isForbidden());
    }
    // Test that purchase history gives 403 if not logged in
    @Test
    public void testHistoryUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/order/history/test"))
                .andExpect(status().isForbidden());
    }
    // Using token, check that accessing user profile gives 200
    @Test
    public void testUserProfileAccess() throws Exception {
        String username = createUserSwitch("setup");
        String token = login(false, username);
        User user = new User();
        user.setUsername(username);
        user.setPassword("password123");
        when(userRepo.findByUsername(username))
                .thenReturn(user);
        mockMvc.perform(get("/api/user/" + username)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username));
    }
    // Using token, check that accessing cart details gives 200
    @Test
    public void testCartAccess() throws Exception {
        String username = createUserSwitch("setup");
        String token = login(false, username);
        User user = new User();
        user.setUsername(username);
        when(userRepo.findByUsername(username))
                .thenReturn(user);
        Cart cart = new Cart();
        cart.setUser(user);
        when(cartRepo.findByUser(user)).thenReturn(cart);
        mockMvc.perform(get("/api/cart/details?username=" + username)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.username").value(username));
    }
    // Using token, check that accessing purchase history gives 200
    @Test
    public void checkHistoryAccess() throws Exception {
        String username = createUserSwitch("setup");
        String token = login(false, username);
        User user = new User();
        user.setUsername(username);
        when(userRepo.findByUsername(username))
                .thenReturn(user);
        mockMvc.perform(get("/api/order/history/" + username)
                .header("Authorization", token))
                .andExpect(status().isOk());
    }
}
