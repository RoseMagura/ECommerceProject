//package com.example.demo.controllers;
//
//import com.example.demo.TestUtils;
//import com.example.demo.model.persistence.User;
//import com.example.demo.model.persistence.repositories.CartRepository;
//import com.example.demo.model.persistence.repositories.UserRepository;
//import com.example.demo.model.requests.CreateUserRequest;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class UserControllerTest {
//
//    private UserController userController;
//
//    private final UserRepository userRepo = mock(UserRepository.class);
//
//    private final CartRepository cartRepo = mock(CartRepository.class);
//
//    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
//    @Before
//    public void setUp() {
//        userController = new UserController();
//        TestUtils.injectObjects(userController, "userRepository", userRepo);
//        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
//        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
//    }
//
//    @Test
//    public void testSuccessfulUserCreation() throws Exception{
//        when(encoder.encode("testPasswordtset")).thenReturn("thisIsHashed");
//        CreateUserRequest r = new CreateUserRequest();
//        r.setUsername("test");
//        r.setPassword("testPassword");
//        r.setConfirmPassword("testPassword");
//        final ResponseEntity<User> response = userController.createUser(r);
//
//        assertNotNull(response);
//        assertEquals(200, response.getStatusCodeValue());
//
//        User u = response.getBody();
//        assertNotNull(u);
//        assertEquals(0, u.getId());
//        assertEquals("test", u.getUsername());
//        assertEquals("thisIsHashed", u.getPassword());
//    }
//
//    @Test
//    public void testCreatePasswordMismatchError() throws Exception{
//        CreateUserRequest r = new CreateUserRequest();
//        r.setUsername("test");
//        r.setPassword("testPassword");
//        r.setConfirmPassword("testPssword");
//
//        final ResponseEntity<User> response = userController.createUser(r);
//
//        assertNotNull(response);
//        assertEquals(400, response.getStatusCodeValue());
//    }
//
//    @Test
//    public void testCreatePasswordLengthError() throws Exception{
//        CreateUserRequest r = new CreateUserRequest();
//        r.setUsername("user");
//        r.setPassword("test");
//        r.setConfirmPassword("test");
//
//        final ResponseEntity<User> response = userController.createUser(r);
//
//        assertNotNull(response);
//        assertEquals(400, response.getStatusCodeValue());
//    }
//}
